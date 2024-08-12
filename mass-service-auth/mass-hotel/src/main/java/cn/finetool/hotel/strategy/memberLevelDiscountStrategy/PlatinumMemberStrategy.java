package cn.finetool.hotel.strategy.memberLevelDiscountStrategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PlatinumMemberStrategy implements MemberLevelDiscountStrategy {

    /**
     * 铂金会员折扣策略
     *
     * @param basicPrice
     * @return
     */
    @Override
    public BigDecimal calculatePrice(BigDecimal basicPrice) {
        return basicPrice.multiply(new BigDecimal("0.85"));
    }
}
