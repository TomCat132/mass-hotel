package cn.finetool.hotel.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.finetool.common.dto.PlanDto;
import cn.finetool.common.dto.RoomDto;
import cn.finetool.common.util.Response;
import cn.finetool.hotel.handler.HotelAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/hotel/admin")
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
    public Response getWillFinishOrderList(@RequestParam("merchantId") String merchantId) {
        return hotelAdminHandler.getWillFinishOrderList(merchantId);
    }

    @PutMapping("/finishRoomOut")
    @ApiOperation(value = "开始办理退房", notes = "PMS : 开始办理退房")
    public Response startFinishRoomOut(@RequestParam("id") Integer id) {
        return hotelAdminHandler.startFinishRoomOut(id);
    }
    
    @GetMapping("/get-room-name-list")
    @ApiOperation(value = "获取房间名称列表", notes = "根据商户ID获取房间名称列表")
    public Response getRoomNameList(@RequestParam("merchantId") String merchantId){
        return hotelAdminHandler.getRoomNameList(merchantId);
    }
    
    @GetMapping("/get-room-info")
    @ApiOperation(value = "获取房间类型信息", notes = "根据房间ID获取房间信息")
    public Response getRoomTypeInfo(@RequestParam("roomId") String roomId){
        return hotelAdminHandler.getRoomInfoVO(roomId);
    }

    @PostMapping(value = "/batch-save-room-info-image", consumes = "multipart/form-data")
    @ApiOperation(value = "宣传图片更新", notes = "更新房间宣传图片")
    public Response updateRoomImage(@RequestParam("id") String id,
                                    @RequestPart(value = "avatarList", required = false) List<MultipartFile> avatarList,
                                    @RequestParam(value = "deleteIds", required = false) List<String> deleteIds){
        return hotelAdminHandler.updateRoomImage(id, avatarList, deleteIds);
    }
    
    @GetMapping("/get-room-info-vo")
    @ApiOperation(value = "获取房间信息", notes = "根据房间ID获取房间信息")
    public Response getRoomInfoVo(@RequestParam("id") String id,
                                  @RequestParam("queryTime") String queryTime){
        return hotelAdminHandler.findRoomInfoById(id, queryTime);
    }

    @PutMapping("/update-room-price")
    @ApiOperation(value = "更新房间价格", notes = "根据房间日期ID更新房间价格")
    public Response updateRoomDatePrice(@RequestParam("id") Integer id,
                                        @RequestParam("newPrice") BigDecimal newPrice) {
        return hotelAdminHandler.updateRoomDatePrice(id, newPrice);
    }
    
    @GetMapping("/get-room-avatar-list")
    @ApiOperation(value = "获取房间宣传图片列表", notes = "根据房间ID获取房间宣传图片列表")
    public Response getRoomInfoAvatarList(@RequestParam("id") String id){
        return hotelAdminHandler.findRoomInfoAvatarList(id);
    }
    
    @PutMapping("/update-room-info")
    @ApiOperation(value = "修改房间信息", notes = "根据房间ID更新房间信息")
    public Response updateRoom(@RequestBody RoomDto roomDto){
        return hotelAdminHandler.updateRoom(roomDto);
    }
    
    @DeleteMapping("/delete-room")
    @ApiOperation(value = "删除房间类型相关的所有配置信息", notes = "根据房间ID删除房间类型相关的所有配置信息")
    public Response deleteRoom(@RequestParam("roomId") String roomId){
        return hotelAdminHandler.deleteRoom(roomId);
    }

    /**
     * @param id room_booking_id
     * @return
     */
    @PutMapping("/cancel-reserve-room")
    @ApiOperation(value = "取消预订房间", notes = "根据预订ID取消预订房间")
    public Response cancelReserveRoom(@RequestParam("id") Integer id){
        return hotelAdminHandler.cancelReserveRoom(id);
    }
}
