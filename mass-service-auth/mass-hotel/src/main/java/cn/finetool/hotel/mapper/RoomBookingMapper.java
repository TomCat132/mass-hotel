package cn.finetool.hotel.mapper;

import cn.finetool.common.po.RoomBooking;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomBookingMapper extends BaseMapper<RoomBooking> {
    List<RoomBooking> queryRoomBookingList(@Param("queryType") Integer queryType,
                                           @Param("queryValue") String queryValue,
                                           @Param("hotelId") String hotelId);

    void changeStatus(@Param("id") int id,
                      @Param("status") int status);
}
