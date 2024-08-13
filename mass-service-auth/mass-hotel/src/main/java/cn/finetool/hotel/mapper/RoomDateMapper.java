package cn.finetool.hotel.mapper;

import cn.finetool.common.po.RoomDate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

@Mapper
public interface RoomDateMapper extends BaseMapper<RoomDate> {
    BigDecimal queryHotelMinPrice(@Param("hotelId") Integer hotelId,
                                  @Param("today")LocalDate today);

    void updateRoomDateStatus(Integer roomDateId, LocalDate checkInDate, LocalDate checkOutDate, Integer status);
}
