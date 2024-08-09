package cn.finetool.hotel.controller;

import cn.finetool.common.po.RoomInfo;
import cn.finetool.common.util.Response;
import cn.finetool.hotel.service.RoomInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roomInfo")
@Api(tags = "具体房间信息模块", description = "提供具体房间信息的查询接口")
public class RoomInfoController {

    @Resource
    RoomInfoService roomInfoService;

    @ApiOperation(value = "添加具体房间信息", notes = "添加具体房间信息")
    @PostMapping("/add")
    public Response addRoomInfo(@RequestBody RoomInfo roomInfo){
        return roomInfoService.addRoomInfo(roomInfo);
    }
}
