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
            couponService.save(voucherDto.getVoucherCoupon());
        }
    }

    @Override
    public void changeStatus(Integer voucherId, Integer status) {
        couponService.update()
                .set("status",status)
                .eq("voucher_id",voucherId)
                .update();
    }

    @Override
    public void decreaseVoucherStock(String voucherId) {
        couponService.update()
                .setSql("count = count - 1")
                .eq("voucher_id",voucherId)
                .update();
    }
}
