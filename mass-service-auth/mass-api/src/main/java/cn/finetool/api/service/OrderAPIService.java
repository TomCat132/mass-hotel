package cn.finetool.api.service;


import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.vo.OrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mass-order-service", path = "/order/api")
public interface OrderAPIService {

    @PutMapping(value = "/updateOrderStatus")
    void updateOrderStatus(@RequestParam("orderId") String orderId, @RequestParam("orderStatus") Integer orderStatus);

    @GetMapping("/queryRechargeOrder/{orderId}")
    RechargeOrder queryRechargeOrder(@PathVariable("orderId") String orderId);

    @PutMapping("/handleRechargeOrder")
    void handleRechargeOrder(@RequestParam("orderId") String orderId);

    /**====== 获取 用户 充值订单列表 =====*/
    @GetMapping("/getRechargeOrderList")
    List<OrderVo> getRechargeOrderList(@RequestParam("userId") String userId);

    @PutMapping("/changeOrderStatus")
    void changeOrderStatus(@RequestParam("orderId") String orderId,
                           @RequestParam("orderStatus") Integer orderStatus);
}
