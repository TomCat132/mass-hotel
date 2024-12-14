package cn.finetool.order.service;

import cn.finetool.common.util.Response;
import cn.finetool.common.vo.OrderVO;

import cn.finetool.common.vo.RoomOrderBaseInfo;
import java.util.List;

public interface OrderService {
    void deleteOrder(String orderId);

    Response getAppRechargeOrderList();

    List<OrderVO> getMerchantOrderList(String merchantId);

    RoomOrderBaseInfo getOrderBaseInfo(String orderId);
}
