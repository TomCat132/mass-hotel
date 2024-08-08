package cn.finetool.hotel.service.impl;

import cn.finetool.common.po.Hotel;
import cn.finetool.common.util.Response;
import cn.finetool.hotel.mapper.HotelMapper;
import cn.finetool.hotel.service.HotelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements HotelService {


    @Override
    public Response addHotelInfo(Hotel hotel) {

        save(hotel);
        return Response.success("添加成功");
    }
}
