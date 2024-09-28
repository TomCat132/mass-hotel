package cn.finetool.api.service;


import cn.finetool.common.po.OrderStatus;
import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.vo.OrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mass-order-service", path = "/order/api")
public interface OrderAPIService {

    @GetMapping("/queryRechargeOrder/{orderId}")
    RechargeOrder queryRechargeOrder(@PathVariable("orderId") String orderId);

    @PutMapping("/handleRechargeOrder")
    void handleRechargeOrder(@RequestParam("orderId") String orderId);

    /**====== 获取 用户 充值订单列表 =====*/
    @GetMapping("/getRechargeOrderList")
    List<RechargeOrder> getRechargeOrderList(@RequestParam("userId") String userId);

    @PutMapping("/changeOrderStatus")
    void changeOrderStatus(@RequestParam("orderId") String orderId,
                           @RequestParam("orderStatus") Integer orderStatus,
                           @RequestParam(value = "payType",required = false) Integer payType);

    /** ======== 获取 充值订单状态 =======*/
    @GetMapping("/queryOrder")
    OrderStatus queryOrder(@RequestParam("orderId") String orderId);

    /** ======== 获取 房间订单信息 =======*/
    @GetMapping("/queryOrderInfo")
    RoomOrder queryOrderInfo(@RequestParam("orderId") String orderId);


}
