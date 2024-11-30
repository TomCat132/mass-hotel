package cn.finetool.recharge.api;

import cn.finetool.common.vo.OrderVo;
import cn.finetool.recharge.service.RechargePlanService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rechargeplan/api")
public class RechargePlanAPIService {

    @Resource
    private RechargePlanService rechargePlanService;

    /** ========= 充值方案状态更改 ========== */
    @PostMapping("/updateStatus")
    public boolean updateRechargePlanStatus(@RequestParam("planId") Integer planId,
                                            @RequestParam("status") Integer status){
        return rechargePlanService.updateRechargePlanStatus(planId,status);
    }


}
