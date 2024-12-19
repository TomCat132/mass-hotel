package cn.finetool.order.controller;

import cn.finetool.common.dto.OrderPayDto;
import cn.finetool.common.util.Response;
import cn.finetool.order.service.RoomOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roomOrder")
@Api(tags = "房间订单管理")
public class RoomOrderController {

    @Resource
    private RoomOrderService roomOrderService;


    @PostMapping("/createRoomOrderInfo")
    @ApiOperation(value = "支付房间订单", notes = "支付房间订单")
    public Response accountPayRoomOrder(@RequestBody OrderPayDto orderPayDto) {
        return roomOrderService.accountPayRoomOrder(orderPayDto);
    }

    @ApiOperation(value = "查询订单详情", notes = "查询订单详情")
    @GetMapping("/queryOrder/{orderId}")
    public Response queryOrder(@PathVariable("orderId") String orderId) {
        return roomOrderService.queryOrder(orderId);
    }


}
