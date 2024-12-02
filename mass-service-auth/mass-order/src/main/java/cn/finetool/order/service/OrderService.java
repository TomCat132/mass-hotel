package cn.finetool.order.service;

import cn.finetool.common.util.Response;

public interface OrderService {
    void deleteOrder(String orderId);

    Response getAppRechargeOrderList();
}
