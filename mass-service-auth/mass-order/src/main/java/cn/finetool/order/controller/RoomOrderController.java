package cn.finetool.order.controller;

import cn.finetool.common.dto.RoomBookingDto;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.util.Response;
import cn.finetool.order.service.RoomOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roomOrder")
@Api(tags = "房间订单管理")
public class RoomOrderController {

    @Resource
    private RoomOrderService roomOrderService;

    @ApiOperation(value = "创建房间订单", notes = "创建房间订单")
    @PostMapping("/create")
    public Response createRoomOrder(@RequestBody RoomBookingDto roomBookingDto){
        return roomOrderService.createRoomOrder(roomBookingDto);
    }
}
