package cn.finetool.pay.strategy;


import cn.finetool.api.service.OrderAPIService;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.enums.Status;
import cn.finetool.common.po.OrderStatus;
import cn.finetool.common.po.RoomOrder;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomReserveOrderStrategy implements OrderTypeStrategy {

    private static final Map<String, OrderStatus> ORDER_STATUS_MAP = new ConcurrentHashMap<>();

    private static final Map<String, RoomOrder> ROOM_ORDER_MAP = new ConcurrentHashMap<>();

    @Resource
    private OrderAPIService orderAPIService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Object queryOrder(String orderId) {
        OrderStatus orderStatus = orderAPIService.queryOrder(orderId);
        ORDER_STATUS_MAP.put(orderId, orderStatus);
        return orderStatus;
    }

    @Override
    public boolean equalsPayAmount(String orderId, Integer userPayAmount) {
        RoomOrder orderInfo = orderAPIService.queryOrderInfo(orderId);
        ROOM_ORDER_MAP.put(orderId, orderInfo);
        if (!userPayAmount.equals(orderInfo.getUserPayAmount().intValue())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean verifyOrderIsPayAmount(String orderId) {
        OrderStatus orderStatus = ORDER_STATUS_MAP.get(orderId);
        if (orderStatus.getOrderStatus() == Status.ORDER_SUCCESS.getCode()) {
            ROOM_ORDER_MAP.remove(orderId);
            ORDER_STATUS_MAP.remove(orderId);
            return true;
        }
        return false;
    }

    @Override
    public void handleOrder(String orderId) {
        // 处理订单 (更改订单状态)
        orderAPIService.changeOrderStatus(orderId, Status.ORDER_SUCCESS.getCode(), null);
    }

    @Override
    public void deleteOrderFlag(String orderId) {
        // 删除订单标识
        redisTemplate.delete(RedisCache.ROOM_RESERVED_ORDER_IS_TIMEOUT + orderId);
    }
}
