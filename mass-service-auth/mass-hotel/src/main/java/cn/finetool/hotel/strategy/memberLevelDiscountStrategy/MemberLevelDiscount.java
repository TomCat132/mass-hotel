package cn.finetool.hotel.strategy.memberLevelDiscountStrategy;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MemberLevelDiscount {

    @Resource
    private SilverMemberStrategy silverMemberStrategy;

    @Resource
    private GoldMemberStrategy goldMemberStrategy;

    @Resource
    private PlatinumMemberStrategy platinumMemberStrategy;

    @Resource
    private DiamondMemberStrategy diamondMemberStrategy;

    private static final Map<Integer, MemberLevelDiscountStrategy> strategies = new HashMap<>();

    @PostConstruct
    public void init(){
        strategies.put(0, null);
        strategies.put(1, silverMemberStrategy);
        strategies.put(2, goldMemberStrategy);
        strategies.put(3, platinumMemberStrategy);
        strategies.put(4, diamondMemberStrategy);
    }

    public MemberLevelDiscountStrategy getStrategy(Integer memberLevel){
        return strategies.get(memberLevel);
    }
}
