package cn.finetool.hotel.strategy.discountStrategy;

import cn.finetool.api.service.ActivityAPIService;
import cn.finetool.common.dto.RoomBookingDto;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.enums.VoucherType;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.VoucherCoupon;
import cn.finetool.common.util.Strings;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class VoucherDisCountStrategy implements RoomPricingStrategy{
    
    @Resource
    private ActivityAPIService activityAPIService;
    
    @Override
    public BigDecimal calculatePrice(RoomBookingDto roomBookingDto) {
        LocalDateTime nowTime = LocalDateTime.now();
        String voucherId = roomBookingDto.getVoucherId();
        if(Objects.nonNull(voucherId)){
           VoucherDto voucherDto = activityAPIService.getVoucherBaseInfo(voucherId);
           if (Strings.equals(voucherDto.getVoucherType(), VoucherType.COUPON.code())){
               // 优惠券
               VoucherCoupon voucherCoupon = voucherDto.getVoucherCoupon();
               if (voucherCoupon.getEndTime().isBefore(nowTime)){
                   throw new BusinessRuntimeException("优惠券已过期");
               }
               BigDecimal tempPrice = roomBookingDto.getTempPrice();
               // 计算价格
               if (tempPrice.compareTo(voucherCoupon.getVoucherRule()) >= 0){
                   BigDecimal voucherAmount = voucherCoupon.getVoucherAmount();
                   tempPrice = tempPrice.subtract(voucherAmount);
                   return tempPrice;
               }
           }
        }
        return roomBookingDto.getTempPrice();
    }
}
