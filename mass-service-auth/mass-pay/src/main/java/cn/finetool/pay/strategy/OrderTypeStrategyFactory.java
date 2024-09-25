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

    @Resource
    private RoomReserveOrderStrategy roomReserveOrderStrategy;

    private static final Map<Integer, OrderTypeStrategy> strategies = new HashMap<>();

    @PostConstruct
    private void init() {
        log.info("init OrderTypeStrategy");
        strategies.put(0, rechargeOrderStrategy);
        strategies.put(1, roomReserveOrderStrategy);

    }

    public OrderTypeStrategy getStrategy(Integer orderType) {
        return strategies.get(orderType);
    }
}
