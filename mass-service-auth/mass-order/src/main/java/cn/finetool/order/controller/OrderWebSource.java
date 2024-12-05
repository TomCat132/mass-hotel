package cn.finetool.order.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.finetool.common.util.Response;
import cn.finetool.order.handler.OrderHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Api(tags = "订单模块Web后台资源接口")
public class OrderWebSource {

    @Resource
    private OrderHandler orderHandler;

    @SaCheckRole(value = {"sys_admin"})
    @GetMapping("/getAppRechargeOrderList")
    @ApiOperation(value = "获取应用充值订单列表")
    public Response getAppRechargeOrderList(){
        return orderHandler.getAppRechargeOrderList();
    }
}
