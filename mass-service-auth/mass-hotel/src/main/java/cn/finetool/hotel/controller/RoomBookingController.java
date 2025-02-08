package cn.finetool.hotel.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.finetool.common.util.Response;
import cn.finetool.hotel.service.RoomBookingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roomBooking")
@Api(tags = "房间预订流程管理")
public class RoomBookingController {

    @Resource
    private RoomBookingService roomBookingService;

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @GetMapping("/query")
    @ApiOperation(value = "查询用户预定的房间（手机号、订单号）", notes = "根据用户ID查询用户预定的房间信息")
    public Response queryRoomBooking(@RequestParam("queryType") Integer queryType,
                                     @RequestParam("queryValue") String queryValue) {
        return roomBookingService.queryRoomBooking(queryType, queryValue);
    }

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @PostMapping("/startHandleCheckIn")
    @ApiOperation(value = "开始办理入住", notes = "PMS: 根据预定号开始处理入住")
    public Response startHandleCheckIn(@RequestParam("id") Integer id) {
        return roomBookingService.startHandleCheckIn(id);
    }

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @GetMapping("/checkRoomDateInfo/{id}")
    @ApiOperation(value = "检查当日房间情况", notes = "PMS: 根据房间ID检查当日房间情况")
    public Response checkRoomDateInfo(@PathVariable("id") Integer id) {
        return roomBookingService.checkRoomDateInfo(id);
    }

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @PutMapping("/deposit")
    @ApiOperation(value = "确认缴纳押金", notes = "PMS : 确认缴纳押金")
    public Response receiveDeposit(@RequestParam("id") String id) {
        return roomBookingService.receiveDeposit(id);
    }

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @PostMapping("/bindingDoorKey")
    @ApiOperation(value = "绑定门禁卡/发放随机密码", notes = "PMS : 绑定门禁卡/发放随机密码")
    public Response bindingDoorKey(@RequestParam("id") Integer id,
                                   @RequestParam("doorKey") String doorKey) {
        return roomBookingService.bindingDoorKey(id, doorKey);
    }

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @PutMapping("/unBindingDoorKey")
    @ApiOperation(value = "解绑门禁卡", notes = "PMS : 解绑门禁卡")
    public Response unBindingDoorKey(@RequestParam("id") Integer id) {
        return roomBookingService.unBindingDoorKey(id);
    }

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @PutMapping("/finishHandleCheckIn")
    @ApiOperation(value = "完成入住手续办理", notes = "根据订单号完成入住")
    public Response finishHandleCheckIn(@RequestParam("id") Integer id,
                                        @RequestParam("type") Integer type,
                                        //非必填参数
                                        @RequestParam(required = false, value = "doorKey") String doorKey) throws JsonProcessingException {
        return roomBookingService.finishHandleCheckIn(id, type, doorKey);
    }

    @PostMapping(value = "/endCheckInRoomOrder")
    @ApiOperation(value = "结束入住", notes = "结束入住")
    public Response endCheckInRoomOrder(@RequestParam("id") Integer id){
        return roomBookingService.endCheckInRoomOrder(id);
    }
    
    @PostMapping(value = "/cancelRoomBooking")
    @ApiOperation(value = "取消房间预定", notes = "PMS、C端共用: 取消房间预定")
    public Response cancelRoomBooking(@RequestParam("id") Integer id){
        return roomBookingService.cancelRoomBooking(id);
    }
    
    @PostMapping(value = "/startHandleCheckInOnline")
    @ApiOperation(value = "用户选择线上入住", notes = "C端: 用户选择线上办理入住")
    public Response startHandleCheckInOnline(@RequestParam("id") Integer id){
        return roomBookingService.startHandleCheckInOnline(id);
    }
}
