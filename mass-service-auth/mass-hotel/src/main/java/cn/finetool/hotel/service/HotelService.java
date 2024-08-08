package cn.finetool.hotel.service;

import cn.finetool.common.po.Hotel;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;

public interface HotelService extends IService<Hotel> {

    Response addHotelInfo(Hotel hotel);
}
