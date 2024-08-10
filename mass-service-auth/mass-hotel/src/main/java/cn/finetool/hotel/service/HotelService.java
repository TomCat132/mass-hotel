package cn.finetool.hotel.service;

import cn.finetool.common.po.Hotel;
import cn.finetool.common.util.Response;
import cn.finetool.common.vo.HotelVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface HotelService extends IService<Hotel> {

    Response addHotelInfo(Hotel hotel);

    Response getNearByHotelList(Double userLng, Double userLat, Double queryRange);

    Response getHotelRoomTypeList(Integer hotelId);
}
