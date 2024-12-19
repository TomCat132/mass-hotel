package cn.finetool.order.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.common.dto.RechargeDto;
import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.util.Response;
import cn.finetool.order.service.RechargeOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public Response createOrder(@RequestBody RechargeDto rechargeDto) {
        return rechargeOrderService.createOrder(rechargeDto);
    }

    @ApiOperation(value = "查询订单订单详情", notes = "查询订单详情")
    @GetMapping("/queryOrder/{orderId}")
    public Response queryOrder(@PathVariable("orderId") String orderId) {
        return rechargeOrderService.queryOrder(orderId);
    }


}
