package cn.finetool.pay.strategy;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class OrderTypeStrategyFactory {


    @Resource
    private RechargeOrderStrategy rechargeOrderStrategy;

    private static final Map<Integer, OrderTypeStrategy> strategies = new HashMap<>();

    @PostConstruct
    public void init() {
        log.info("init OrderTypeStrategy");
        // Populate the map with the Spring-managed bean
        strategies.put(0, rechargeOrderStrategy);
        // You can add more strategies here as needed
        // strategies.put(1, anotherStrategy);
    }

    public OrderTypeStrategy getStrategy(Integer orderType) {
        return strategies.get(orderType);
    }
}
