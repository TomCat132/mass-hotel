package cn.finetool.hotel.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.finetool.common.po.Hotel;
import cn.finetool.common.util.Response;
import cn.finetool.hotel.service.HotelService;
import com.fasterxml.jackson.databind.type.LogicalType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotel")
@Api(tags = "酒店模块")
public class HotelController {


    @Resource
    HotelService hotelService;

    @SaCheckRole("super_admin")
    @ApiOperation(value = "添加酒店信息", notes = "添加酒店信息")
    @PostMapping("/addHotelInfo")
    public Response addHotelInfo(@RequestBody Hotel hotel){
        return hotelService.addHotelInfo(hotel);
    }

    @SaCheckRole("user")
    @ApiOperation(value = "获取附近的酒店列表", notes = "获取附近的酒店列表")
    @GetMapping("/getNearByHotelList")
    public Response getNearByHotelList(@RequestParam("userLng") Double userLng,
                                       @RequestParam("userLat") Double userLat,
                                       @RequestParam("queryRange") Double queryRange){
        return hotelService.getNearByHotelList(userLng, userLat, queryRange);
    }


}
