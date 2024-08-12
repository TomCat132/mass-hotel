package cn.finetool.hotel.strategy.memberLevelDiscountStrategy;

import java.math.BigDecimal;

public interface MemberLevelDiscountStrategy {
    BigDecimal calculatePrice(BigDecimal basicPrice);
}
