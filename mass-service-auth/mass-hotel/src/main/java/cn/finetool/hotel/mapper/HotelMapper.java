package cn.finetool.hotel.mapper;

import cn.finetool.common.po.Hotel;
import cn.finetool.common.vo.CheckRoomInfoVO;
import cn.finetool.common.vo.HotelVo;
import cn.finetool.common.vo.RoomInfoVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HotelMapper extends BaseMapper<Hotel> {

    List<HotelVo> getHotelGeoList();

    HotelVo queryHotelInfo(@Param("hotelId") int hotelId);

    List<RoomInfoVo> queryHotelRoomTypeList(@Param(("hotelId")) Integer hotelId,
                                            @Param("checkInDate") LocalDate checkInDate,
                                            @Param("checkOutDate") LocalDate checkOutDate);


    List<CheckRoomInfoVO> queryHotelReserveRoomBookingList(@Param("hotelId") Integer hotelId);

    String queryUserColumn(@RequestParam("roomDateId") Integer roomDateId);
}
