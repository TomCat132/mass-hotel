package cn.finetool.hotel.strategy.discountStrategy;

import cn.finetool.common.dto.RoomBookingDto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class RoomPricingContext {

    private final List<RoomPricingStrategy> strategies = new ArrayList<>();

    @Resource
    private MemberDiscountStrategy memberDiscountStrategy;

    /** ======== 价格计算策略 ======= */
    @PostConstruct
    public void addStrategy(){
        log.info("添加价格计算策略");
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
