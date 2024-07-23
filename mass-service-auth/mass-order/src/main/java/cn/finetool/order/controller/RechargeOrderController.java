package cn.finetool.order.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.util.Response;
import cn.finetool.order.service.RechargeOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rechargeOrder")
@Api(tags = "充值订单管理")
public class RechargeOrderController {

    @Resource
    private RechargeOrderService rechargeOrderService;

    @SaCheckRole("user")
    @ApiOperation(value = "创建充值订单", notes = "创建充值订单")
    @PostMapping("/createOrder")
    public Response createOrder(@RequestBody RechargeOrder rechargeOrder){
        return rechargeOrderService.createOrder(rechargeOrder);
    }






}
