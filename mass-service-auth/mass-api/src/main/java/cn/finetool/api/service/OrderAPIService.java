package cn.finetool.api.service;


import cn.finetool.common.po.RechargeOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "mass-order-service", path = "/order/api", contextId = "rechargeOrder")
public interface OrderAPIService {

    @PutMapping(value = "/updateOrderStatus")
    void updateOrderStatus(@RequestParam("orderId") String orderId, @RequestParam("orderStatus") Integer orderStatus);

    @GetMapping("/queryRechargeOrder/{orderId}")
    RechargeOrder queryRechargeOrder(@PathVariable("orderId") String orderId);

    @PutMapping("/handleRechargeOrder")
    void handleRechargeOrder(@RequestParam("orderId") String orderId);


}
