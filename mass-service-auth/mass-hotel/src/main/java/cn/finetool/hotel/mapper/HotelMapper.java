package cn.finetool.hotel.mapper;

import cn.finetool.common.po.Hotel;
import cn.finetool.common.vo.HotelVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HotelMapper extends BaseMapper<Hotel> {

    List<HotelVo> getHotelGeoList();
}
