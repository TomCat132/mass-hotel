package cn.finetool.hotel.controller;

import cn.finetool.common.dto.RoomDto;
import cn.finetool.common.util.Response;
import cn.finetool.hotel.service.RoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
