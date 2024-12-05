package cn.finetool.pay.strategy;

import cn.finetool.api.service.OrderAPIService;
import cn.finetool.api.service.UserAPIService;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.enums.PayType;
import cn.finetool.common.enums.Status;
import cn.finetool.common.vo.OrderVO;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RechargeOrderStrategy implements OrderTypeStrategy{

    private static final Map<String, OrderVO> RECHARGE_ORDER_MAP = new ConcurrentHashMap<>();

    @Resource
    private OrderAPIService orderAPIService;

    @Resource
    private UserAPIService userAPIService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    // TODO: 冷热数据分离，待修改
    @Override
    public Object queryOrder(String orderId) {
        OrderVO rechargeOrder = orderAPIService.queryRechargeOrder(orderId);
        RECHARGE_ORDER_MAP.put(orderId,rechargeOrder);
        return rechargeOrder;

    }

    @Override
    public boolean equalsPayAmount(String orderId, Integer userPayAmount) {
        OrderVO rechargeOrder = RECHARGE_ORDER_MAP.get(orderId);
        if (userPayAmount != rechargeOrder.getUserPayAmount().intValue()){
            RECHARGE_ORDER_MAP.remove(orderId);
            return false;
        }
        return true;
    }

    @Override
    public boolean verifyOrderIsPayAmount(String orderId) {
        OrderVO rechargeOrder = RECHARGE_ORDER_MAP.get(orderId);
        if (rechargeOrder.getOrderStatus() == Status.ORDER_SUCCESS.getCode()){
            RECHARGE_ORDER_MAP.remove(orderId);
            return true;
        }
        return false;
    }

    @Override
    public void handleOrder(String orderId) {
        // 处理充值订单
        orderAPIService.changeOrderStatus(orderId, Status.ORDER_SUCCESS.getCode(), PayType.ALI_PAY.getCode());

        OrderVO rechargeOrder = RECHARGE_ORDER_MAP.get(orderId);
        // 更新用户信息
        userAPIService.updateUserInfo(rechargeOrder.getUserId(),rechargeOrder.getTotalAmount());
    }

    @Override
    public void deleteOrderFlag(String orderId) {
        redisTemplate.delete(RedisCache.RECHARGE_ORDER_IS_TIMEOUT + orderId);
    }

}
