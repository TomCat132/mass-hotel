package cn.finetool.activity.handler;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.activity.mapper.CouponMapper;
import cn.finetool.activity.mapper.SignRewardMapper;
import cn.finetool.activity.mapper.UserVoucherMapper;
import cn.finetool.activity.mapper.VoucherMapper;
import cn.finetool.activity.service.VoucherService;
import cn.finetool.activity.strategy.VoucherOperationContext;
import cn.finetool.api.service.AccountAPIService;
import cn.finetool.api.service.HotelAPIService;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.enums.SysEnum;
import cn.finetool.common.enums.Status;
import cn.finetool.common.enums.VoucherType;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.SignReward;
import cn.finetool.common.po.UserVoucher;
import cn.finetool.common.po.Voucher;
import cn.finetool.common.po.VoucherCoupon;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.common.util.Strings;
import cn.finetool.common.util.TimeUtil;
import cn.finetool.common.vo.ActivityVO;
import cn.finetool.common.vo.VoucherVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

import static cn.finetool.common.util.Response.success;

@Service
public class VoucherHandler extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {

    private static final SnowflakeIdWorker ID_WORKER = new SnowflakeIdWorker(2, 0);

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
    public Response getAllCategoryVoucherList(String merchantId) {
        // 查询商户的优惠券
        List<VoucherVO> voucherList = new ArrayList<>();
        voucherList = voucherMapper.findMerchantVoucherListById(merchantId);

        voucherList = voucherList.stream().sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).toList();
        return success(voucherList);
    }
    @Override
    public Response getPlatFormVoucherList() {
        List<VoucherVO> voucherList = new ArrayList<>();
        // 查询平台发放的系统券
        List<VoucherVO> voucherSystemList = voucherMapper.findPVoucherSystemList();
        voucherList.addAll(voucherSystemList);
        voucherList = voucherList.stream().sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).toList();
        return success(voucherList);
    }
    

    @Override
    public void updateVoucherStatus(Integer voucherType, String voucherId, Integer status) {
        voucherOperationContext.changStatus(voucherType, voucherId, status);
    }

    @Override
    public Response getValidVoucherList() {
        List<VoucherVO> validVoucherList = new ArrayList<>();
        List<UserVoucher> userVoucherList = userVoucherMapper.selectList(new QueryWrapper<UserVoucher>()
                .eq("user_id", StpUtil.getLoginIdAsString())
                .eq("status", Status.VOUCHER_CAN_USE.code()));
        List<String> voucherIds = userVoucherList.stream().map(UserVoucher::getVoucherId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(voucherIds)) {
            return success(validVoucherList);
        }
        List<Voucher> voucherList = voucherMapper.selectList(new QueryWrapper<Voucher>()
                .in("voucher_id", voucherIds));
        // 根据不同类型的活动券进行分组
        Map<Integer, List<Voucher>> collect = voucherList.stream()
                .collect(Collectors.groupingBy(Voucher::getVoucherType));
        for (Map.Entry<Integer, List<Voucher>> entry : collect.entrySet()) {
            // TODO: 目前只有优惠券
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
            }
        }
        return success(validVoucherList);
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
    public Response deleteVoucherByVoucherId(String voucherId) {
        // 查询优惠券类型
        Voucher voucher = voucherMapper.selectOne(new QueryWrapper<Voucher>()
                .eq("voucher_id", voucherId));
        voucherOperationContext.deleteVoucher(voucherId, voucher.getVoucherType());
        return Response.success("删除成功");
    }

    @Override
    public Response signRewardSetting(SignReward signReward) {

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
        return success("设置成功");
    }


    @Override
    public Response changeRewardContent(String rewardId, String content) {
        signRewardMapper.update(new UpdateWrapper<SignReward>()
                .set("reward_content", content)
                .eq("reward_id", rewardId));
        return success("修改成功");
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
                    
                    if (Strings.equals(SysEnum.MERCHANT_PLATFORM_PREFIX.code(), voucher.getMerchantId())){
                        activityVO.setMerchantName("平台活动");
                    } else {
                        activityVO.setMerchantName(hotelAPIService.findMerchantNameByMerchantId(voucher.getMerchantId()));
                    }
                    activityVO.setActivityId(voucher.getVoucherId());
                    activityVO.setActivityType(Status.ACTIVITY_VOUCHER.code());
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
                    return activityVO;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        activityVOList.addAll(voucherActivities);
        // 按结束时间降序排列
        activityVOList.sort( (o1, o2) -> o2.getEndTime().compareTo(o1.getEndTime()));
        return success(activityVOList);
    }

    @Override
    public Response isReceivedVoucher(String activityId) {

        Voucher voucher = voucherMapper.selectOne(new QueryWrapper<Voucher>()
                .eq("voucher_id", activityId));

        String userId = StpUtil.getLoginIdAsString();
        if (Objects.nonNull(voucher)){
            // 优惠券
            if (Strings.equals(VoucherType.COUPON.code(), voucher.getVoucherType())){
                UserVoucher userVoucher = userVoucherMapper.selectOne(new QueryWrapper<UserVoucher>()
                        .eq("user_id", userId)
                        .eq("voucher_id", voucher.getVoucherId()));
                if (Objects.isNull(userVoucher)){
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
        if (Strings.equals(SysEnum.VOUCHER_PREFIX.code(), prefix)){
            activityVO.setActivityType(Status.ACTIVITY_VOUCHER.code());
            // 活动券
            Voucher voucher = voucherMapper.selectOne(new QueryWrapper<Voucher>()
                    .eq("voucher_id", activityId));
            activityVO.setVoucherType(voucher.getVoucherType());
            activityVO.setMerchantName(hotelAPIService.findMerchantNameByMerchantId(voucher.getMerchantId()));
            if (Strings.equals(VoucherType.COUPON.code(), voucher.getVoucherType())){
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
            }
        }
        activityVO.setActivityId(activityId);
        return success(activityVO);
    }


}
