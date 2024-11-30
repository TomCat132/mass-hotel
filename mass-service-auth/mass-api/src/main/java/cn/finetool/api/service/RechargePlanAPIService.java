package cn.finetool.api.service;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;




@FeignClient(name = "mass-recharge-plan-service", path = "/rechargeplan/api", contextId = "rechargePlans")
public interface RechargePlanAPIService {

    /**====== 更改充值方案状态 =====*/
    @PostMapping("/updateStatus")
    boolean updateRechargePlanStatus(@RequestParam("planId") Integer planId,
                                     @RequestParam("status") Integer status);
}
