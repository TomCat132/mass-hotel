package cn.finetool.api.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "mass-recharge-plan-service", path = "/api/rechargeplan", contextId = "rechargePlans")
public interface RechargePlanAPIService {

    /**====== 更改充值方案状态 =====*/
    @RequestMapping("/updateStatus")
    boolean updateRechargePlanStatus(@RequestParam("planId") Integer planId);
}
