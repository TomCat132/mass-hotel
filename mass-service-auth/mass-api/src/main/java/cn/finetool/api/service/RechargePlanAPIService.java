package cn.finetool.api.service;

import cn.finetool.common.vo.OrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "mass-recharge-plan-service", path = "/rechargeplan/api", contextId = "rechargePlans")
public interface RechargePlanAPIService {

    /**====== 更改充值方案状态 =====*/
    @RequestMapping("/updateStatus")
    boolean updateRechargePlanStatus(@RequestParam("planId") Integer planId);
}
