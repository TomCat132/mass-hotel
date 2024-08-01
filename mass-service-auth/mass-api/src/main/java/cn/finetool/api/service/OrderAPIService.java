package cn.finetool.api.service;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "mass-order-service", path = "/rechargeOrder/api", contextId = "rechargeOrder")

    public interface OrderAPIService {
    @PutMapping(value = "/updateOrderStatus")
    public void updateOrderStatus(@RequestParam("orderId") String orderId, @RequestParam("orderStatus") Integer orderStatus);
}
