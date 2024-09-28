package cn.finetool.hotel.strategy.discountStrategy;



import cn.finetool.common.dto.RoomBookingDto;
import cn.finetool.hotel.strategy.memberLevelDiscountStrategy.MemberLevelDiscount;
import cn.finetool.hotel.strategy.memberLevelDiscountStrategy.MemberLevelDiscountStrategy;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MemberDiscountStrategy implements RoomPricingStrategy {

    @Resource
    private MemberLevelDiscount memberLevelDiscount;

    @Override
    public BigDecimal calculatePrice(RoomBookingDto roomBookingDto) {
        BigDecimal tempPrice = roomBookingDto.getTempPrice();
        Integer memberLevel = roomBookingDto.getMemberLevel();
        if (memberLevel != 0){
            MemberLevelDiscountStrategy strategy = memberLevelDiscount.getStrategy(memberLevel);
            return strategy.calculatePrice(tempPrice);
        }
        return tempPrice;
    }
}
