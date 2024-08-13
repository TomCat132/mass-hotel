package cn.finetool.hotel.mapper;

import cn.finetool.common.po.RoomInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RoomInfoMapper extends BaseMapper<RoomInfo> {
    List<RoomInfo> getCanUseRoomInfoList();

    List<Integer> queryResidualRoomInfo(@Param("roomId") String roomId,
                                  @Param("checkInDate") LocalDate checkInDate,
                                  @Param("checkOutDate") LocalDate checkOutDate);

    BigDecimal getRoomPrice(@Param("roomId") String roomId,
                            @Param("date") LocalDate date,
                            @Param("liveCount") Integer liveCount);
}
