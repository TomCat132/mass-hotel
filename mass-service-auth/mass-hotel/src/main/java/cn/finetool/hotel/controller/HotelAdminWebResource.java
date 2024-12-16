package cn.finetool.hotel.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.finetool.common.dto.PlanDto;
import cn.finetool.common.util.Response;
import cn.finetool.hotel.handler.HotelAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotel/admin")
//@Consumes({MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
@Api(value = "酒店后台管理系统Web资源接口")
public class HotelAdminWebResource {

    @Resource
    private HotelAdminService hotelAdminHandler;

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @GetMapping("/getHotelReserveRoomBookingList/{merchantId}")
    @ApiOperation(value = "获取酒店房间预订列表")
    public Response getHotelReserveRoomBookingList(@PathVariable("merchantId") String merchantId) {
        return hotelAdminHandler.getHotelReserveRoomBookingList(merchantId);
    }

    @SaCheckRole(value = {"sys_admin"})
    @GetMapping("/queryCooperationMerchantList")
    @ApiOperation(value = "查询合作商户列表", notes = "查询合作商户列表")
    public Response queryCooperationMerchantList() {
        return hotelAdminHandler.queryCooperationMerchantList();
    }

    @SaCheckRole(value = {"sys_admin"})
    @PostMapping("/updateStatus")
    @ApiOperation(value = "更新商户状态", notes = "更新商户状态")
    public Response updateStatus(@RequestBody PlanDto planDto) {
        return hotelAdminHandler.updateStatus(planDto);
    }

    @SaCheckRole(value = {"sys_admin"})
    @GetMapping("/merchantInfo/{merchantId}")
    @ApiOperation(value = "查询商户详情页面", notes = "根据商户ID查询商户详情页面")
    public Response merchantInfo(@PathVariable("merchantId") String merchantId) {
        return hotelAdminHandler.merchantInfo(merchantId);
    }

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @GetMapping("/getWillFinishOrderList")
    @ApiOperation(value = "退房办理订单信息列表", notes = "PMS : 退房办理订单信息列表")
    public Response getWillFinishOrderList(@RequestParam("merchantId") String merchantId){
        return hotelAdminHandler.getWillFinishOrderList(merchantId);
    }
    
    @PutMapping("/finishRoomOut")
    @ApiOperation(value = "开始办理退房", notes = "PMS : 开始办理退房")
    public Response startFinishRoomOut(@RequestParam("id") Integer id){
        return hotelAdminHandler.startFinishRoomOut(id);
    }
}
