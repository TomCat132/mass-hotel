package cn.finetool.hotel.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.common.dto.ExamineDto;
import cn.finetool.common.enums.Status;
import cn.finetool.common.util.Response;
import cn.finetool.hotel.service.RoomBookingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.bouncycastle.util.Strings;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roomBooking")
@Api(tags = "房间预订流程管理")
public class RoomBookingController {

    @Resource
    private RoomBookingService roomBookingService;

    @SaCheckRole(value = {"admin","super_admin"}, mode = SaMode.OR)
    @GetMapping("/query")
    @ApiOperation(value = "查询用户预定的房间（手机号、订单号）", notes = "根据用户ID查询用户预定的房间信息")
    public Response queryRoomBooking( @RequestParam("queryType") Integer queryType,
                                      @RequestParam("queryValue") String queryValue){
        return roomBookingService.queryRoomBooking(queryType, queryValue);
    }

    @SaCheckRole(value = {"admin"})
    @PutMapping("/startHandleCheckIn")
    @ApiOperation(value = "开始办理入住", notes = "根据订单号开始处理入住")
    public Response startHandleCheckIn(@RequestParam("orderId") String orderId){
        return roomBookingService.startHandleCheckIn(orderId);
    }

    @SaCheckRole(value = {"admin"})
    @GetMapping("/checkRoomDateInfo")
    @ApiOperation(value = "检查当日房间情况", notes = "酒店前台: 根据房间ID检查当日房间情况")
    public Response checkRoomDateInfo(@RequestParam("id") Integer id){
        return roomBookingService.checkRoomDateInfo(id);
    }

    @SaCheckRole(value = {"admin"})
    @PutMapping("/deposit")
    @ApiOperation(value = "缴纳押金", notes = "酒店前台: 缴纳押金")
    public Response receiveDeposit(@RequestParam("id") String id){
        return roomBookingService.receiveDeposit(id);
    }

    @SaCheckRole(value = {"admin"})
    @PostMapping("/bindingDoorKey")
    @ApiOperation(value = "绑定门禁卡", notes = "酒店前台: 绑定门禁卡")
    public Response bindingDoorKey(@RequestParam("id") Integer id,
                                   @RequestParam("doorKey") String doorKey){
        return roomBookingService.bindingDoorKey(id,doorKey);
    }

    @SaCheckRole(value = {"admin"})
    @PutMapping("/finishHandleCheckIn")
    @ApiOperation(value = "完成入住手续办理", notes = "根据订单号完成入住")
    public Response finishHandleCheckIn(@RequestBody ExamineDto examineDto){
        return roomBookingService.finishHandleCheckIn(examineDto);
    }
}
