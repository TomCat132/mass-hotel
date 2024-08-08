package cn.finetool.hotel.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.finetool.common.po.Hotel;
import cn.finetool.common.util.Response;
import cn.finetool.hotel.service.HotelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.CtField;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
