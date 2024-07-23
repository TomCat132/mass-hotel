package cn.finetool.recharge.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.common.po.RechargePlans;

import cn.finetool.common.util.Response;
import cn.finetool.recharge.service.RechargePlanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rechargePlan")
@Api(tags = "充值方案")
public class RechargePlanController {

    @Resource
    private RechargePlanService rechargePlanService;

    @SaCheckRole("super_admin")
    @PostMapping("/addChargePlan")
    @ApiOperation(value = "新增充值方案", notes = "新增充值方案")
    public Response addChargePlan(@RequestBody RechargePlans rechargePlans) throws JsonProcessingException {
        return rechargePlanService.addChargePlan(rechargePlans);
    }

    @GetMapping("/test")
    public Response test(){
        return Response.success(StpUtil.getLoginIdAsString());
    }

}
