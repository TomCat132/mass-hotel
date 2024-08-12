package cn.finetool.hotel.strategy.discountStrategy;

import cn.finetool.common.dto.RoomBookingDto;

import java.math.BigDecimal;

public interface RoomPricingStrategy {
    BigDecimal calculatePrice(RoomBookingDto roomBookingDto);
}
