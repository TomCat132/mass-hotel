package cn.finetool.order.service;

import cn.finetool.common.util.Response;
import cn.finetool.common.vo.OrderVO;

import cn.finetool.common.vo.RoomOrderBaseInfo;
import java.util.List;

public interface OrderService {
    /**======== 删除订单 ======= */
    void deleteOrder(String orderId);
    /**======== 获取应用充值订单列表 ======= */
    Response getAppRechargeOrderList();
    /**======== 获取商户订单列表 ======= */
    List<OrderVO> getMerchantOrderList(String merchantId);
    /**======== 获取房间预订基本信息 ======= */
    RoomOrderBaseInfo getOrderBaseInfo(String orderId);
    /**======== 根据订单号查询商户id ======= */
    String findMerchantIdByOrderId(String orderId);
    /**======== 根据订单号查询用户id ======= */
    String findUserIdByOrderId(String orderId);
    /**======== 更改订单评价状态 ======= */
    void updateEvaluateStatus(String orderId, Integer isEvaluate);
    /**======== 根据订单号查询订单信息 ======= */
    OrderVO findOrderInfoByOrderId(String orderId);
    /**======== 处理订单 ======= */
    void handleOrder(String orderId, Integer status);
    /**======== 根据用户id查询订单基本信息 ======= */
    RoomOrderBaseInfo findOrderBaseInfoByUserId(String userId);
    /**======== 根据用户id查询订单基本信息列表 ======= */
    List<RoomOrderBaseInfo> findOrderBaseInfoListByUserId(String userId);
}
