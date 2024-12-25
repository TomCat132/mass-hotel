package cn.finetool.activity.handler;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.activity.mapper.CouponMapper;
import cn.finetool.activity.mapper.UserVoucherMapper;
import cn.finetool.activity.mapper.VoucherMapper;
import cn.finetool.activity.service.VoucherService;
import cn.finetool.activity.strategy.SaveVoucherContext;
import cn.finetool.api.service.AccountAPIService;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.enums.SysEnum;
import cn.finetool.common.enums.Status;
import cn.finetool.common.enums.VoucherType;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.UserVoucher;
import cn.finetool.common.po.Voucher;
import cn.finetool.common.po.VoucherCoupon;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.common.util.Strings;
import cn.finetool.common.util.TimeUtil;
import cn.finetool.common.vo.VoucherVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

import static cn.finetool.common.util.Response.success;

@Service
public class VoucherHandler extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {

    private static final SnowflakeIdWorker WORKER_ID = new SnowflakeIdWorker(2, 0);

    @Resource
    private SaveVoucherContext saveVoucherContext;
    @Resource
    private VoucherMapper voucherMapper;
    @Resource
    private AccountAPIService accountAPIService;
    @Resource
    private UserVoucherMapper userVoucherMapper;
    @Resource
    private CouponMapper couponMapper;

    @Override
    @Transactional
    public Response addVoucher(VoucherDto voucherDto) throws JsonProcessingException {


        String voucherId = SysEnum.VoucherPrefix.getCode() + String.valueOf(WORKER_ID.nextId());
        voucherDto.setVoucherId(voucherId);
        LocalDateTime nowTime = LocalDateTime.now();

        String userId = StpUtil.getLoginIdAsString();
        String merchantId = accountAPIService.queryMerchantOfUser(userId);
        voucherDto.setMerchantId(merchantId);
        if (Strings.isBlank(merchantId)) {
            throw new BusinessRuntimeException("用户未关联商户");
        }
        
        saveVoucherContext.saveVoucher(voucherDto);

        Voucher voucher = new Voucher();
        voucher.setVoucherId(voucherId);
        voucher.setCreateTime(nowTime);
        voucher.setVoucherType(voucherDto.getVoucherType());


        voucher.setUserId(userId);
        voucher.setMerchantId(merchantId);
        save(voucher);
        // 根据不同的活动券类型加入不同的表中


        return success("操作成功");
    }



    @Override
    public Response getAllCategoryVoucherList(String merchantId) {
        // 查询商户的优惠券
        List<VoucherVO> voucherList = voucherMapper.findMerchantVoucherListById(merchantId);
        voucherList = voucherList.stream().sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).toList();
        return success(voucherList);
    }

    @Override
    public void updateVoucherStatus(Integer voucherType, String voucherId, Integer status) {
        saveVoucherContext.changStatus(voucherType, voucherId, status);
    }

    @Override
    public Response getValidVoucherList() {
        List<VoucherVO> validVoucherList = new ArrayList<>();
        List<UserVoucher> userVoucherList = userVoucherMapper.selectList(new QueryWrapper<UserVoucher>()
                .eq("user_id", StpUtil.getLoginIdAsString())
                .eq("status", Status.VOUCHER_CAN_USE.code()));
        List<String> voucherIds = userVoucherList.stream().map(UserVoucher::getVoucherId).collect(Collectors.toList());
        List<Voucher> voucherList = voucherMapper.selectList(new QueryWrapper<Voucher>()
                .in("voucher_id", voucherIds));
        // 根据不同类型的活动券进行分组
        Map<Integer, List<Voucher>> collect = voucherList.stream()
                .collect(Collectors.groupingBy(Voucher::getVoucherType));
        for (Map.Entry<Integer, List<Voucher>> entry : collect.entrySet()){
            // TODO: 目前只有优惠券
            if (Strings.equals(entry.getKey(), VoucherType.COUPON.code())){
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
            if(Strings.equals(voucher.getVoucherType(), VoucherType.COUPON.code())){
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
}
