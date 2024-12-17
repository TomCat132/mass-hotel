package cn.finetool.activity.strategy;

import cn.finetool.activity.service.CouponService;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.enums.VoucherType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class CouponStrategy extends SaveVoucherStrategy {

    @Resource
    private CouponService couponService;

    @Override
    public void save(VoucherDto voucherDto) {
        if (voucherDto.getVoucherType() == VoucherType.COUPON.getCode()) {
            voucherDto.getVoucherCoupon().setVoucherId(voucherDto.getVoucherId());
            couponService.save(voucherDto.getVoucherCoupon());
        }
    }

    @Override
    public void changeStatus(String voucherId, Integer status) {
        couponService.update()
                .set("status", status)
                .eq("voucher_id", voucherId)
                .update();
    }

    @Override
    public void decreaseVoucherStock(String voucherId) {
        couponService.update()
                .setSql("count = count - 1")
                .eq("voucher_id", voucherId)
                .update();
    }
}
