package cn.finetool.order.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.finetool.common.util.Response;
import cn.finetool.order.handler.OrderHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Api(tags = "订单模块Web后台资源接口")
public class OrderWebSource {

    @Resource
    private OrderHandler orderHandler;

    @SaCheckRole(value = {"sys_admin"})
    @GetMapping("/getAppRechargeOrderList")
    @ApiOperation(value = "获取应用充值订单列表", notes = "后台系统后台（甲方）")
    public Response getAppRechargeOrderList() {
        return orderHandler.getAppRechargeOrderList();
    }

    @GetMapping("/merchant/getOrderList/{merchantId}")
    @ApiOperation(value = "获取商户订单列表")
    public Response getOrderOfMerchant(@PathVariable("merchantId") String merchantId) {
        return Response.success(orderHandler.getMerchantOrderList(merchantId));
    }

    @ApiOperation(value = "获取房间预订基本信息", notes = "移动: 获取房间预订基本信息")
    @GetMapping("/getRoomBookingBaseInfo/{orderId}")
    public Response getRoomBookingBaseInfo(@PathVariable("orderId") String orderId) {
        return Response.success(orderHandler.getOrderBaseInfo(orderId));
    }
}
