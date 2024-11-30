package cn.finetool.hotel.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.finetool.common.dto.PlanDto;
import cn.finetool.common.util.Response;
import cn.finetool.hotel.handler.HotelAdminHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotel/admin")
//@Consumes({MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
@Api(value = "酒店后台管理系统Web资源接口")
public class HotelAdminWebResource {

    @Resource
    private HotelAdminHandler hotelAdminHandler;

    @SaCheckRole(value = {"admin","super_admin"}, mode = SaMode.OR)
    @GetMapping("/getHotelReserveRoomBookingList")
    @ApiOperation(value = "获取酒店房间预订列表")
    public Response getHotelReserveRoomBookingList(@RequestParam("hotelId") Integer hotelId){
        return hotelAdminHandler.getHotelReserveRoomBookingList(hotelId);
    }

    @SaCheckRole(value = {"sys_admin"})
    @GetMapping("/queryCooperationMerchantList")
    @ApiOperation(value = "查询合作商户列表", notes = "查询合作商户列表")
    public Response queryCooperationMerchantList(){
        return hotelAdminHandler.queryCooperationMerchantList();
    }

    @SaCheckRole(value = {"sys_admin"})
    @ApiOperation(value = "更新商户状态",notes = "更新商户状态")
    @PostMapping("/updateStatus")
    public Response updateStatus(@RequestBody PlanDto planDto){
        return hotelAdminHandler.updateStatus(planDto);
    }
}
