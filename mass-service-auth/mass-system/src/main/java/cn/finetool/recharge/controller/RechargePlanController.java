package cn.finetool.recharge.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.finetool.common.enums.Status;
import cn.finetool.common.po.RechargePlans;

import cn.finetool.common.util.Response;
import cn.finetool.recharge.service.RechargePlanService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    @SaCheckRole("sys_admin")
    @PostMapping("/addChargePlan")
    @ApiOperation(value = "新增充值方案", notes = "新增充值方案")
    public Response addChargePlan(@RequestBody RechargePlans rechargePlans) throws JsonProcessingException {
        return rechargePlanService.addChargePlan(rechargePlans);
    }

    @SaCheckRole("user")
    @GetMapping("/validRechargePlanList")
    @ApiOperation(value = "查询有效充值方案列表", notes = "查询有效充值方案列表")
    public Response validRechargePlanList() {
        return rechargePlanService.validRechargePlanList();
    }

    @SaCheckRole(value = {"sys_admin"})
    @GetMapping("/list")
    @ApiOperation(value = "所有充值充值方案")
    public Response getRechargePlanList() {
        return Response.success(rechargePlanService.list(new LambdaQueryWrapper<RechargePlans>()
                .eq(RechargePlans::getIsDelete, Status.NOT_DELETED.getCode())));
    }

    @SaCheckRole(value = {"sys_admin"})
    @PutMapping("/update/{planId}")
    @ApiOperation(value = "删除充值方案", notes = "逻辑删除")
    public Response deleteById(@PathVariable("planId") Integer planId) {
        return rechargePlanService.deleteById(planId);
    }

}
