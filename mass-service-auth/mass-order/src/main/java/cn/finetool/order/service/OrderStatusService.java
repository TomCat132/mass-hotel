package cn.finetool.order.service;

import cn.finetool.common.po.OrderStatus;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderStatusService extends IService<OrderStatus> {
    void changeOrderStatus(String orderId, Integer orderStatus,Integer payType);
}
