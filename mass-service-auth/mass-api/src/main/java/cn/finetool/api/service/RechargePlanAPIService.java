package cn.finetool.api.service;


import cn.finetool.common.configuration.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;




@FeignClient(name = "mass-recharge-plan-service", path = "/rechargeplan/api", configuration = MultipartSupportConfig.class)
public interface RechargePlanAPIService {

    /**====== 更改充值方案状态 =====*/
    @PostMapping("/updateStatus")
    boolean updateRechargePlanStatus(@RequestParam("planId") Integer planId,
                                     @RequestParam("status") Integer status);
}
