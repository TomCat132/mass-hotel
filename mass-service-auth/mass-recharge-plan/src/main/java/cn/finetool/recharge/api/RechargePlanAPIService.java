package cn.finetool.recharge.api;

import cn.finetool.common.vo.OrderVo;
import cn.finetool.recharge.service.RechargePlanService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rechargeplan/api")
public class RechargePlanAPIService {

    @Resource
    private RechargePlanService rechargePlanService;

    /** ========= 充值方案状态更改 ========== */
    @RequestMapping("/updateStatus")
    public boolean updateRechargePlanStatus(@RequestParam("planId") Integer planId){
        return rechargePlanService.updateRechargePlanStatus(planId);
    }


}
