package cn.finetool.pay.strategy;


import cn.finetool.api.service.OrderAPIService;
import cn.finetool.common.po.OrderStatus;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class RoomReserveOrderStrategy implements OrderTypeStrategy{


    @Resource
    private OrderAPIService orderAPIService;

    @Override
    public Object queryOrder(String orderId) {
        OrderStatus RoomReserveOrderStatus =  orderAPIService.queryOrder(orderId);

        return null;
    }

    @Override
    public boolean equalsPayAmount(String orderId, Integer userPayAmount) {
        return false;
    }

    @Override
    public boolean verifyOrderIsPayAmount(String orderId) {
        return false;
    }

    @Override
    public void handleOrder(String orderId) {

    }

    @Override
    public void deleteOrderFlag(String orderId) {

    }
}
