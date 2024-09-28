package cn.finetool.activity.strategy;

import cn.finetool.activity.service.CouponService;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.enums.VoucherType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class CouponStrategy implements SaveVoucherStrategy{

    @Resource
    private CouponService couponService;

    @Override
    public void save(VoucherDto voucherDto) {
        if (voucherDto.getVoucherType() == VoucherType.COUPON.getCode()){
            couponService.save(voucherDto.getCoupon());
        }
    }
}
