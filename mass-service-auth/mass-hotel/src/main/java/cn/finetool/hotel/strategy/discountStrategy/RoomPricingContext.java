package cn.finetool.hotel.strategy.discountStrategy;

import cn.finetool.common.dto.RoomBookingDto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class RoomPricingContext {

    private List<RoomPricingStrategy> strategies;

    @Resource
    private MemberDiscountStrategy memberDiscountStrategy;

    /** ======== 价格计算策略 ======= */
    @PostConstruct
    public void addStrategy(){
        strategies.add(memberDiscountStrategy);
    }

    public BigDecimal calculatePrice(RoomBookingDto roomBookingDto){
        for (RoomPricingStrategy strategy : strategies) {
            BigDecimal tempPrice = strategy.calculatePrice(roomBookingDto);
            roomBookingDto.setTempPrice(tempPrice);
        }
        return roomBookingDto.getTempPrice();
    }

}
