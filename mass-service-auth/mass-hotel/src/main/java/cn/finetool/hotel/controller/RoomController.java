package cn.finetool.hotel.controller;

import cn.finetool.common.dto.RoomBookingDto;
import cn.finetool.common.dto.RoomDto;
import cn.finetool.common.util.Response;
import cn.finetool.hotel.service.RoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/room")
@Api(tags = "房间管理")
public class RoomController {

    @Resource
    RoomService roomService;

//    @SaCheckRole("hotel_admin")
    @ApiOperation(value = "添加房间信息", notes = "添加房间信息")
    @PostMapping("/add")
    public Response addRoomInfo(@RequestBody RoomDto roomDto){
        return roomService.addRoomInfo(roomDto);
    }

    @ApiOperation(value = "查询房间类型信息", notes = "查询房间类型信息")
    @GetMapping("/query")
    public Response queryRoomInfo(@RequestParam("roomId") String roomId){
        return roomService.queryRoomInfo(roomId);
    }

    @ApiOperation(value = "计算实际需支付金额", notes = "计算实际需支付金额")
    @PostMapping("/calculatePrice")
    public Response calculatePrice(@RequestBody RoomBookingDto roombookingDto){
        return roomService.calculatePrice(roombookingDto);
    }

}
