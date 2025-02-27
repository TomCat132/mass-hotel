package cn.finetool.activity.handler;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.activity.bo.PointExchangeBO;
import cn.finetool.activity.mapper.CouponMapper;
import cn.finetool.activity.mapper.PointExchangeMapper;
import cn.finetool.activity.mapper.PointUsageRecordMapper;
import cn.finetool.activity.mapper.SignRewardMapper;
import cn.finetool.activity.mapper.UserVoucherMapper;
import cn.finetool.activity.mapper.VoucherMapper;
import cn.finetool.activity.mapper.VoucherSystemMapper;
import cn.finetool.activity.service.VoucherService;
import cn.finetool.activity.strategy.VoucherOperationContext;
import cn.finetool.api.service.AccountAPIService;
import cn.finetool.api.service.HotelAPIService;
import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqRoutingKey;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.enums.PointExchangeType;
import cn.finetool.common.enums.SysEnum;
import cn.finetool.common.enums.Status;
import cn.finetool.common.enums.VoucherType;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.PointExchange;
import cn.finetool.common.po.PointUsageRecord;
import cn.finetool.common.po.SignReward;
import cn.finetool.common.po.User;
import cn.finetool.common.po.UserVoucher;
import cn.finetool.common.po.Voucher;
import cn.finetool.common.po.VoucherCoupon;
import cn.finetool.common.po.VoucherSystem;
import cn.finetool.common.util.FunUtil;
import cn.finetool.common.util.MqUtils;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.common.util.Strings;
import cn.finetool.common.util.TimeUtil;
import cn.finetool.common.vo.ActivityVO;
import cn.finetool.common.vo.PointProDuctVO;
import cn.finetool.common.vo.PointUsageRecordVO;
import cn.finetool.common.vo.VoucherVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

import static cn.finetool.common.util.Response.success;

@Service
public class VoucherHandler extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {

    private static final SnowflakeIdWorker ID_WORKER = new SnowflakeIdWorker(2, 0);
    private static final Logger LOGGER = LoggerFactory.getLogger(VoucherHandler.class);
    @Resource
    private VoucherOperationContext voucherOperationContext;
    @Resource
    private VoucherMapper voucherMapper;
    @Resource
    private AccountAPIService accountAPIService;
    @Resource
    private UserVoucherMapper userVoucherMapper;
    @Resource
    private SignRewardMapper signRewardMapper;
    @Resource
    private CouponMapper couponMapper;
    @Resource
    private HotelAPIService hotelAPIService;
    @Resource
    private PointExchangeMapper pointExchangeMapper;
    @Resource
    private PointUsageRecordMapper pointUsageRecordMapper;
    @Resource
    private VoucherSystemMapper voucherSystemMapper;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public Response addVoucher(VoucherDto voucherDto) throws JsonProcessingException {
        String voucherId = SysEnum.VOUCHER_PREFIX.code() + ID_WORKER.nextId();
        voucherDto.setVoucherId(voucherId);
        LocalDateTime nowTime = TimeUtil.now();
        String userId = StpUtil.getLoginIdAsString();
        String merchantId = accountAPIService.queryMerchantOfUser(userId);
        Voucher voucher = new Voucher();
        voucher.setVoucherId(voucherId);
        voucher.setCreateTime(nowTime);
        voucher.setVoucherType(voucherDto.getVoucherType());
        voucher.setUserId(userId);
        if (Strings.isNotBlank(merchantId)) {
            voucher.setMerchantId(merchantId);
        }
        // 根据不同的活动券类型加入不同的表中
        voucherOperationContext.saveVoucher(voucherDto);
        save(voucher);

        return success("操作成功");
    }


    @Override
    public List<VoucherVO> getAllCategoryVoucherList(String merchantId) {
        // 查询商户的优惠券
        List<VoucherVO> voucherList = new ArrayList<>();
        voucherList = voucherMapper.findMerchantVoucherListById(merchantId);

        voucherList = voucherList.stream().sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).toList();
        return voucherList;
    }

    @Override
    public List<VoucherVO> getPlatFormVoucherList() {
        List<VoucherVO> voucherList = new ArrayList<>();
        // 查询平台发放的系统券
        List<VoucherVO> voucherSystemList = voucherMapper.findPVoucherSystemList();
        voucherList.addAll(voucherSystemList);
        voucherList = voucherList.stream().sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).toList();
        return voucherList;
    }

    @Override
    public String addPointsMallProduct(PointExchange pointExchange) {
        // 参数校验
        LocalDateTime beginTime = pointExchange.getBeginTime();
        LocalDateTime endTime = pointExchange.getEndTime();
        if (beginTime.isAfter(endTime)) {
            throw new BusinessRuntimeException("开始时间不能大于结束时间");
        }
        if (!Strings.equals(pointExchange.getCount(), -1) && Strings.equals(pointExchange.getStock(), -1)) {
            if (pointExchange.getCount() > pointExchange.getStock()) {
                throw new BusinessRuntimeException("兑换数量不能大于库存数量");
            }
        }
        // 判断是否重复
        PointExchange product = pointExchangeMapper.selectOne(new QueryWrapper<PointExchange>()
                .eq("cdk", pointExchange.getCdk()));
        if (Objects.nonNull(product)) {
            throw new BusinessRuntimeException("该商品已添加，请勿重复添加");
        }
        // 保存商品信息
        String id = SysEnum.POINT_EXCHANGE_PREFIX.code() + ID_WORKER.nextId();
        pointExchange.setId(id);
        pointExchange.setCreateTime(TimeUtil.now());
        pointExchange.setIsDelete(Status.NOT_DELETED.code());
        pointExchange.setStatus(Status.POINT_EXCHANGE_WAIT.code());
        // 根据 cdk 获取商品类型
        String cdk = pointExchange.getCdk();
        // 截取 cdk 前4位
        String prefix = cdk.substring(0, 4);
        if (Strings.equals(SysEnum.VOUCHER_PREFIX.code(), prefix)) {
            pointExchange.setExchangeType(PointExchangeType.VOUCHER.code());
        }
        pointExchangeMapper.insert(pointExchange);
        // TODO:发送消息

        // 计算延迟时间
        LocalDateTime nowTime = TimeUtil.now();
        // 开始时间-当前时间
        long delayUpTime = TimeUtil.betweenToMillis(beginTime, endTime);
        long delayDownTime = TimeUtil.betweenToMillis(nowTime, endTime);

        try {
            MqUtils.sendMessage(rabbitTemplate, MqExchange.POINT_MALL_PRODUCT_EXCHANGE,
                    MqRoutingKey.POINT_MALL_PRODUCT_ROUTING_KEY, ImmutableMap.of("id", id, "type", "up"),
                    message -> {
                        message.getMessageProperties().getHeaders().put("x-delay", delayUpTime);
                        return message;
                    });
            redisTemplate.opsForValue().set(RedisCache.POINT_PRODUCT_UP_SIGN + id, "");
            LOGGER.info("商品:{} 在 {} ms后上架", id, delayUpTime);
            MqUtils.sendMessage(rabbitTemplate, MqExchange.POINT_MALL_PRODUCT_EXCHANGE,
                    MqRoutingKey.POINT_MALL_PRODUCT_ROUTING_KEY, ImmutableMap.of("id", id, "type", "down"),
                    message -> {
                        message.getMessageProperties().getHeaders().put("x-delay", delayDownTime);
                        return message;
                    });
            redisTemplate.opsForValue().set(RedisCache.POINT_PRODUCT_DOWN_SIGN + id, "");
            LOGGER.info("商品:{} 在 {} ms后下架", id, delayDownTime);
        } catch (Exception e) {
            LOGGER.error("RabbitMQ 发送消息失败", e);
            throw new RuntimeException(e);
        }

        return "已成功添加商品";
    }

    @Override
    public String userRedeemProduct(PointExchangeBO pointExchangeBO) {
        // 校验是否已经兑换超出限制
        String pointExchangeId = pointExchangeBO.getId();
        String userId = StpUtil.getLoginIdAsString();
        List<PointUsageRecord> pointUsageRecords = pointUsageRecordMapper.selectList(new QueryWrapper<PointUsageRecord>()
                .eq("user_id", userId)
                .eq("pe_id", pointExchangeId));
        PointExchange product = pointExchangeMapper.selectOne(new QueryWrapper<PointExchange>()
                .eq("id", pointExchangeId));
        if (Strings.equals(product.getCount(), pointUsageRecords.size())) {
            throw new BusinessRuntimeException("兑现次数达到上限");
        }
        // 校验用户积分是否足够一次兑换
        User userInfoByUserId = accountAPIService.findUserInfoByUserId(userId);
        if (userInfoByUserId.getPoints() < product.getNeedPoints()) {
            throw new BusinessRuntimeException("积分不足，无法兑换");
        }
        // 判断商品类型是否是限量商品
        if (product.getStock() != -1) {
            // 进行商品兑换
            RLock lock = redissonClient.getLock(RedisCache.POINT_EXCHANGE_LOCK + pointExchangeId);
            try {
                boolean isLocked = lock.tryLock(2, 2, TimeUnit.SECONDS);
                if (!isLocked) {
                    throw new BusinessRuntimeException("系统繁忙，请稍后再试");
                }
                // 扣减库存
                int affectedRows = pointExchangeMapper.update(new UpdateWrapper<PointExchange>()
                        .setSql("stock = stock - 1")
                        .eq("id", pointExchangeId)
                        .gt("stock", 0));
                if (affectedRows == 0) {
                    // 库存不足
                    throw new BusinessRuntimeException("库存不足");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
        // 扣减用户兑换积分（异步）
        accountAPIService.usePoints(userId, product.getNeedPoints());
        // 记录兑换记录
        PointUsageRecord pointUsageRecord = new PointUsageRecord();
        pointUsageRecord.setId(SysEnum.POINT_USAGE_RECORD_PREFIX.code() + ID_WORKER.nextId());
        pointUsageRecord.setUserId(userId);
        pointUsageRecord.setPeId(pointExchangeId);
        pointUsageRecord.setCreateTime(TimeUtil.now());
        pointUsageRecord.setStatus(Status.POINT_USAGE_EXCHANGED.code());
        pointUsageRecord.setIsDelete(Status.NOT_DELETED.code());
        pointUsageRecordMapper.insert(pointUsageRecord);
        // 根据兑换类型进行物品发放
        if (Strings.equals(product.getExchangeType(), PointExchangeType.VOUCHER.code())) {
            // 发放活动券
            UserVoucher userVoucher = new UserVoucher();
            userVoucher.setUserId(userId);
            userVoucher.setVoucherId(product.getCdk());
            userVoucher.setStatus(Status.VOUCHER_CAN_USE.code());
            userVoucherMapper.insert(userVoucher);
        }
        return "兑换成功";
    }

    @Override
    public List<PointProDuctVO> getPointMallProductList() {
        List<PointExchange> pointExchanges = pointExchangeMapper.selectList(new QueryWrapper<PointExchange>()
                .eq("is_delete", Status.NOT_DELETED.code())
                .orderByDesc("create_time"));
        // 按照商品类型进行分组处理
        Map<Integer, List<PointExchange>> productsMap = FunUtil.groupBy(pointExchanges, PointExchange::getExchangeType);
        if (Objects.nonNull(productsMap.get(0))) {
            // 活动券 tb_voucher
            List<PointExchange> voucherPointExchange = productsMap.get(0);
            return voucherPointExchange.stream()
                    .map(pointExchange -> {
                        PointProDuctVO pointProDuctVO = new PointProDuctVO();
                        Voucher voucher = voucherMapper.selectOne(new QueryWrapper<Voucher>()
                                .eq("voucher_id", pointExchange.getCdk()));
                        if (Strings.equals(voucher.getVoucherType(), VoucherType.COUPON.code())) {
                            // 优惠券
                            VoucherCoupon coupon = couponMapper.selectOne(new QueryWrapper<VoucherCoupon>()
                                    .eq("voucher_id", voucher.getVoucherId()));
                            pointProDuctVO.setProductName(coupon.getVoucherTitle());
                        } else if (Strings.equals(voucher.getVoucherType(), VoucherType.SYSTEM.code())) {
                            // 系统券
                            VoucherSystem system = voucherSystemMapper.selectOne(new QueryWrapper<VoucherSystem>()
                                    .eq("voucher_id", voucher.getVoucherId()));
                            pointProDuctVO.setProductName(system.getVoucherTitle());
                        }
                        pointProDuctVO.setStock(pointExchange.getStock());
                        pointProDuctVO.setId(pointExchange.getId());
                        pointProDuctVO.setCount(pointExchange.getCount());
                        pointProDuctVO.setNeedPoints(pointExchange.getNeedPoints());
                        pointProDuctVO.setExchangeType(pointExchange.getExchangeType());
                        pointProDuctVO.setBeginTime(pointExchange.getBeginTime());
                        pointProDuctVO.setEndTime(pointExchange.getEndTime());
                        pointProDuctVO.setStatus(pointExchange.getStatus());
                        return pointProDuctVO;
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<PointUsageRecordVO> pointExchangeRecordList(String userId) {
        List<PointUsageRecord> pointUsageRecords = pointUsageRecordMapper.selectList(new QueryWrapper<PointUsageRecord>()
                .eq("user_id", userId)
                .eq("is_delete", Status.NOT_DELETED.code())
                .orderByDesc("create_time"));
        if (CollectionUtils.isEmpty(pointUsageRecords)) {
            return Collections.emptyList();
        }
        return pointUsageRecords.stream()
                .map(pointUsageRecord -> {
                    PointUsageRecordVO pointUsageRecordVO = new PointUsageRecordVO();
                    pointUsageRecordVO.setId(pointUsageRecord.getId());
                    pointUsageRecordVO.setPeId(pointUsageRecord.getPeId());
                    pointUsageRecordVO.setStatus(pointUsageRecord.getStatus());
                    PointExchange pointExchange = pointExchangeMapper.selectById(pointUsageRecord.getPeId());
                    if (Objects.isNull(pointExchange)) {
                        return null;
                    }
                    pointUsageRecordVO.setNeedPoints(pointExchange.getNeedPoints());
                    pointUsageRecordVO.setExchangeType(pointExchange.getExchangeType());
                    pointUsageRecordVO.setCount(pointExchange.getCount());
                    pointUsageRecordVO.setStock(pointExchange.getStock());
                    pointUsageRecordVO.setBeginTime(pointExchange.getBeginTime());
                    pointUsageRecordVO.setEndTime(pointExchange.getEndTime());
                    pointUsageRecordVO.setCreateTime(pointUsageRecord.getCreateTime());

                    Voucher voucher = voucherMapper.selectOne(new QueryWrapper<Voucher>()
                            .eq("voucher_id", pointExchange.getCdk()));
                    if (Strings.equals(voucher.getVoucherType(), VoucherType.COUPON.code())) {
                        // 优惠券
                        VoucherCoupon coupon = couponMapper.selectOne(new QueryWrapper<VoucherCoupon>()
                                .eq("voucher_id", voucher.getVoucherId()));
                        pointUsageRecordVO.setProductName(coupon.getVoucherTitle());
                    } else if (Strings.equals(voucher.getVoucherType(), VoucherType.SYSTEM.code())) {
                        // 系统券
                        VoucherSystem system = voucherSystemMapper.selectOne(new QueryWrapper<VoucherSystem>()
                                .eq("voucher_id", voucher.getVoucherId()));
                        pointUsageRecordVO.setProductName(system.getVoucherTitle());
                    }
                    return pointUsageRecordVO;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherVO> getVoucherListByUserId(String userId) {
        // TODO: 待实现
        return List.of();
    }

    @Override
    public List<PointProDuctVO> getMerchantPointProductList(String merchantId) {
        List<PointExchange> pointExchanges = pointExchangeMapper.selectList(new QueryWrapper<PointExchange>()
                .eq("merchant_id", merchantId)
                .eq("is_delete", Status.NOT_DELETED.code())
                .orderByDesc("create_time"));
        return pointExchanges.stream().map(pointExchange -> {
                    PointProDuctVO pointProDuctVO = new PointProDuctVO();
                    pointProDuctVO.setId(pointExchange.getId());
                    pointProDuctVO.setStock(pointExchange.getStock());
                    pointProDuctVO.setCount(pointExchange.getCount());
                    pointProDuctVO.setNeedPoints(pointExchange.getNeedPoints());
                    pointProDuctVO.setExchangeType(pointExchange.getExchangeType());
                    pointProDuctVO.setBeginTime(pointExchange.getBeginTime());
                    pointProDuctVO.setEndTime(pointExchange.getEndTime());
                    pointProDuctVO.setStatus(pointExchange.getStatus());
                    // 根据 cdk 获取商品名称
                    String cdk = pointExchange.getCdk();
                    if (Strings.equals(SysEnum.VOUCHER_PREFIX.code(), cdk.substring(0, 4))) {
                        // 活动券
                        Voucher voucher = voucherMapper.selectOne(new QueryWrapper<Voucher>()
                                .eq("voucher_id", cdk));
                        if (Strings.equals(voucher.getVoucherType(), VoucherType.COUPON.code())) {
                            // 优惠券
                            VoucherCoupon coupon = couponMapper.selectOne(new QueryWrapper<VoucherCoupon>()
                                    .eq("voucher_id", voucher.getVoucherId()));
                            pointProDuctVO.setProductName(coupon.getVoucherTitle());
                        } else if (Strings.equals(voucher.getVoucherType(), VoucherType.SYSTEM.code())) {
                            // 系统券
                            VoucherSystem system = voucherSystemMapper.selectOne(new QueryWrapper<VoucherSystem>()
                                    .eq("voucher_id", voucher.getVoucherId()));
                            pointProDuctVO.setProductName(system.getVoucherTitle());
                        }
                    }
                    return pointProDuctVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Object deletePointMallProduct(String id) {
        PointExchange pointExchange = pointExchangeMapper.selectById(id);
        if (Objects.isNull(pointExchange)) {
            throw new BusinessRuntimeException("商品不存在");
        }
        if (Strings.equals(pointExchange.getStatus(), Status.POINT_EXCHANGE_CAN_EXCHANGE.code())
                || Strings.equals(pointExchange.getStatus(), Status.POINT_EXCHANGE_SOLD_OUT.code())) {
            throw new BusinessRuntimeException("只能删除下架状态或已过期的商品");
        }
        pointExchangeMapper.update(new UpdateWrapper<PointExchange>()
                .set("is_delete", Status.IS_DELETED.code())
                .eq("id", id));
        return "删除成功";
    }

    @Override
    public void updateStatusById(String id, Integer status) {
        pointExchangeMapper.update(new UpdateWrapper<PointExchange>()
                .set("status", status)
                .eq("id", id));
    }


    @Override
    public void updateVoucherStatus(Integer voucherType, String voucherId, Integer status) {
        voucherOperationContext.changStatus(voucherType, voucherId, status);
    }

    @Override
    public List<VoucherVO> getValidVoucherList() {
        List<VoucherVO> validVoucherList = new ArrayList<>();
        List<UserVoucher> userVoucherList = userVoucherMapper.selectList(new QueryWrapper<UserVoucher>()
                .eq("user_id", StpUtil.getLoginIdAsString())
                .eq("status", Status.VOUCHER_CAN_USE.code()));
        List<String> voucherIds = userVoucherList.stream().map(UserVoucher::getVoucherId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(voucherIds)) {
            return validVoucherList;
        }
        List<Voucher> voucherList = voucherMapper.selectList(new QueryWrapper<Voucher>()
                .in("voucher_id", voucherIds));
        // 根据不同类型的活动券进行分组
        Map<Integer, List<Voucher>> collect = voucherList.stream()
                .collect(Collectors.groupingBy(Voucher::getVoucherType));
        for (Map.Entry<Integer, List<Voucher>> entry : collect.entrySet()) {
            if (Strings.equals(entry.getKey(), VoucherType.COUPON.code())) {
                List<String> couponList = entry.getValue().stream()
                        .map(Voucher::getVoucherId)
                        .collect(Collectors.toList());
                List<VoucherCoupon> couponVoucherList = couponMapper.selectList(new QueryWrapper<VoucherCoupon>()
                        .in("voucher_id", couponList));
                List<VoucherVO> voucherVOList = couponVoucherList.stream().map(coupon -> {
                    VoucherVO voucherVO = new VoucherVO();
                    voucherVO.setVoucherId(coupon.getVoucherId());
                    voucherVO.setVoucherTitle(coupon.getVoucherTitle());
                    voucherVO.setVoucherRule(coupon.getVoucherRule());
                    return voucherVO;
                }).collect(Collectors.toList());
                validVoucherList.addAll(voucherVOList);
            } // 系统券
            else if (Strings.equals(entry.getKey(), VoucherType.SYSTEM.code())) {
                List<String> systemList = entry.getValue().stream()
                        .map(Voucher::getVoucherId)
                        .collect(Collectors.toList());
                List<VoucherSystem> systemVoucherList = voucherSystemMapper.selectList(new QueryWrapper<VoucherSystem>()
                        .in("voucher_id", systemList));
                List<VoucherVO> voucherVOList = systemVoucherList.stream().map(system -> {
                    VoucherVO voucherVO = new VoucherVO();
                    voucherVO.setVoucherId(system.getVoucherId());
                    voucherVO.setVoucherTitle(system.getVoucherTitle());
                    voucherVO.setVoucherRule(system.getVoucherRule());
                    return voucherVO;
                }).collect(Collectors.toList());
                validVoucherList.addAll(voucherVOList);
            }
        }
        return validVoucherList;
    }

    @Override
    public VoucherDto getVoucherBaseInfo(String voucherId) {
        VoucherDto voucherDto = new VoucherDto();
        Voucher voucher = voucherMapper.selectOne(new QueryWrapper<Voucher>()
                .eq("voucher_id", voucherId));
        if (Objects.nonNull(voucher)) {
            voucherDto.setVoucherId(voucher.getVoucherId());
            voucherDto.setVoucherType(voucher.getVoucherType());
            //判断活动券类型
            if (Strings.equals(voucher.getVoucherType(), VoucherType.COUPON.code())) {
                VoucherCoupon voucherCoupon = couponMapper.selectOne(new QueryWrapper<VoucherCoupon>()
                        .eq("voucher_id", voucherId));
                voucherDto.setVoucherCoupon(voucherCoupon);
            }
            return voucherDto;
        }
        return voucherDto;
    }

    @Override
    public void usedVoucher(String voucherId, Integer status) {
        //查询优惠券类型
        Integer voucherType = voucherMapper.selectOne(new QueryWrapper<Voucher>()
                .eq("voucher_id", voucherId)).getVoucherType();
        userVoucherMapper.update(new UpdateWrapper<UserVoucher>()
                .eq("voucher_id", voucherId)
                .set("status", status)
                .set("use_time", TimeUtil.now()));
    }

    @Override
    public void deleteVoucherByVoucherId(String voucherId) {
        // 查询优惠券类型
        Voucher voucher = voucherMapper.selectOne(new QueryWrapper<Voucher>()
                .eq("voucher_id", voucherId));
        voucherOperationContext.deleteVoucher(voucherId, voucher.getVoucherType());
    }

    @Override
    public void signRewardSetting(SignReward signReward) {

        LocalDate rewardDate = signReward.getRewardDate();
        LocalDate today = TimeUtil.currentDate();
        // 只能设置未来的奖励
        if (rewardDate.isBefore(today) || rewardDate.isEqual(today)) {
            throw new BusinessRuntimeException("奖励日期不能小于当前日期");
        }
        SignReward signRewardInfo = signRewardMapper.selectOne(new QueryWrapper<SignReward>()
                .eq("reward_date", rewardDate));
        if (Objects.nonNull(signRewardInfo)) {
            throw new BusinessRuntimeException("该奖励已设置，请勿重复设置");
        }
        signReward.setRewardId(SysEnum.SIGN_REWARD_PREFIX.code() + ID_WORKER.nextId());
        // TODO:校验参数
        signRewardMapper.insert(signReward);

    }


    @Override
    public void changeRewardContent(String rewardId, String content) {
        signRewardMapper.update(new UpdateWrapper<SignReward>()
                .set("reward_content", content)
                .eq("reward_id", rewardId));
    }

    @Override
    public Response getRewardContentListByMonth(String date) {
        // yyyy-MM
        YearMonth yearMonth = TimeUtil.parseToYearMonth(date);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        // 查询当月所有的签到奖励内容 
        List<SignReward> signRewards = signRewardMapper.selectList(new QueryWrapper<SignReward>()
                .ge("reward_date", startOfMonth)
                .le("reward_date", endOfMonth));
        return success(signRewards);
    }

    @Override
    public Response getTimeLimitedActivities() {
        List<ActivityVO> activityVOList = new ArrayList<>();
        // 查询所有 有效活动
        // 1. 查询活动券（有效）
        List<Voucher> validVoucherList = voucherMapper.selectList(null);
        // 组成数据
        List<ActivityVO> voucherActivities = validVoucherList
                .stream()
                .map(voucher -> {
                    ActivityVO activityVO = new ActivityVO();
                    if (Objects.isNull(voucher.getMerchantId())) {
                        activityVO.setMerchantName("平台活动");
                    } else {
                        activityVO.setMerchantName(hotelAPIService.findMerchantNameByMerchantId(voucher.getMerchantId()));
                    }
                    activityVO.setActivityId(voucher.getVoucherId());
                    activityVO.setActivityType(Status.ACTIVITY_VOUCHER.code());
                    // 优惠券
                    if (Strings.equals(voucher.getVoucherType(), VoucherType.COUPON.code())) {
                        VoucherCoupon coupon = couponMapper.selectOne(new QueryWrapper<VoucherCoupon>()
                                .eq("voucher_id", voucher.getVoucherId())
                                .eq("status", Status.VOUCHER_UP.code()));
                        if (Objects.nonNull(coupon)) {
                            activityVO.setActivityTitle(coupon.getVoucherTitle());
                            activityVO.setActivitySubTitle(coupon.getVoucherSubTitle());
                            activityVO.setStatus(coupon.getStatus());
                            activityVO.setBeginTime(coupon.getBeginTime());
                            activityVO.setEndTime(coupon.getEndTime());
                            activityVO.setCount(coupon.getCount());
                        } else {
                            return null;
                        }
                    }
                    // 系统券
                    else if (Strings.equals(VoucherType.SYSTEM.code(), voucher.getVoucherType())) {
                        VoucherSystem system = voucherSystemMapper.selectOne(new QueryWrapper<VoucherSystem>()
                                .eq("voucher_id", voucher.getVoucherId()));
                        if (Objects.nonNull(system)) {
                            activityVO.setActivityTitle(system.getVoucherTitle());
                            activityVO.setActivitySubTitle(system.getVoucherSubTitle());
                            activityVO.setStatus(system.getStatus());
                            activityVO.setBeginTime(system.getBeginTime());
                            activityVO.setEndTime(system.getEndTime());
                            activityVO.setCount(system.getCount());
                        } else {
                            return null;
                        }
                    }
                    return activityVO;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        activityVOList.addAll(voucherActivities);
        return success(activityVOList);
    }

    @Override
    public Response isReceivedVoucher(String activityId) {

        Voucher voucher = voucherMapper.selectOne(new QueryWrapper<Voucher>()
                .eq("voucher_id", activityId));

        String userId = StpUtil.getLoginIdAsString();
        if (Objects.nonNull(voucher)) {
            // 优惠券
            if (Strings.equals(VoucherType.COUPON.code(), voucher.getVoucherType())) {
                UserVoucher userVoucher = userVoucherMapper.selectOne(new QueryWrapper<UserVoucher>()
                        .eq("user_id", userId)
                        .eq("voucher_id", voucher.getVoucherId()));
                if (Objects.isNull(userVoucher)) {
                    return success(false);
                } else {
                    return success(true);
                } // 系统券
            } else if (Strings.equals(VoucherType.SYSTEM.code(), voucher.getVoucherType())) {
                UserVoucher userVoucher = userVoucherMapper.selectOne(new QueryWrapper<UserVoucher>()
                        .eq("user_id", userId)
                        .eq("voucher_id", voucher.getVoucherId()));
                if (Objects.isNull(userVoucher)) {
                    return success(false);
                } else {
                    return success(true);
                }
            }
        }
        throw new BusinessRuntimeException("活动不存在");
    }

    @Override
    public Response getActivityInfo(String activityId) {
        ActivityVO activityVO = new ActivityVO();
        //截取前4位
        String prefix = activityId.substring(0, 4);
        if (Strings.equals(SysEnum.VOUCHER_PREFIX.code(), prefix)) {
            activityVO.setActivityType(Status.ACTIVITY_VOUCHER.code());
            // 活动券
            Voucher voucher = voucherMapper.selectOne(new QueryWrapper<Voucher>()
                    .eq("voucher_id", activityId));
            activityVO.setVoucherType(voucher.getVoucherType());
            if (Objects.isNull(voucher.getMerchantId())) {
                activityVO.setMerchantName("平台活动");
            } else {
                activityVO.setMerchantName(hotelAPIService.findMerchantNameByMerchantId(voucher.getMerchantId()));
            }

            if (Strings.equals(VoucherType.COUPON.code(), voucher.getVoucherType())) {
                // 活动券-优惠券
                VoucherCoupon coupon = couponMapper.selectOne(new QueryWrapper<VoucherCoupon>()
                        .eq("voucher_id", activityId));
                activityVO.setActivityTitle(coupon.getVoucherTitle());
                activityVO.setActivitySubTitle(coupon.getVoucherSubTitle());
                activityVO.setStatus(coupon.getStatus());
                activityVO.setBeginTime(coupon.getBeginTime());
                activityVO.setEndTime(coupon.getEndTime());
                activityVO.setStatus(coupon.getStatus());
                activityVO.setCount(coupon.getCount());
            } else if (Strings.equals(VoucherType.SYSTEM.code(), voucher.getVoucherType())) {
                // 活动券-系统券
                VoucherSystem system = voucherSystemMapper.selectOne(new QueryWrapper<VoucherSystem>()
                        .eq("voucher_id", activityId));
                activityVO.setActivityTitle(system.getVoucherTitle());
                activityVO.setActivitySubTitle(system.getVoucherSubTitle());
                activityVO.setStatus(system.getStatus());
                activityVO.setBeginTime(system.getBeginTime());
                activityVO.setEndTime(system.getEndTime());
                activityVO.setCount(system.getCount());
            }
        }
        activityVO.setActivityId(activityId);
        return success(activityVO);
    }


}
