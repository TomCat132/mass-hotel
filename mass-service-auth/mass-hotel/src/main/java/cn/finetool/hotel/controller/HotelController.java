package cn.finetool.hotel.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.finetool.common.dto.QueryRoomTypeDto;
import cn.finetool.common.po.Hotel;
import cn.finetool.common.util.Response;
import cn.finetool.hotel.service.HotelService;
import com.fasterxml.jackson.databind.type.LogicalType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import static cn.dev33.satoken.annotation.SaMode.OR;

@RestController
@RequestMapping("/hotel")
@Api(tags = "酒店管理")
public class HotelController {


    @Resource
    HotelService hotelService;

    @SaCheckRole("super_admin")
    @ApiOperation(value = "添加酒店信息", notes = "添加酒店信息")
    @PostMapping("/addHotelInfo")
    public Response addHotelInfo(@RequestBody Hotel hotel){
        return hotelService.addHotelInfo(hotel);
    }

    @SaCheckRole(value = {"super_admin", "admin", "user"}, mode = SaMode.OR)
    @ApiOperation(value = "获取附近的酒店列表", notes = "获取附近的酒店列表")
    @GetMapping("/getNearByHotelList")
    public Response getNearByHotelList(@RequestParam("userLng") Double userLng,
                                       @RequestParam("userLat") Double userLat,
                                       @RequestParam("queryRange") Double queryRange){
        return hotelService.getNearByHotelList(userLng, userLat, queryRange);
    }

    @ApiOperation(value = "查看酒店所有类型的房间信息", notes = "查看酒店所有类型的房间信息")
    @GetMapping("/getHotelRoomTypeList")
    public Response getHotelRoomTypeList(@ModelAttribute QueryRoomTypeDto queryRoomTypeDto){
        return hotelService.getHotelRoomTypeList(queryRoomTypeDto);
    }


}
