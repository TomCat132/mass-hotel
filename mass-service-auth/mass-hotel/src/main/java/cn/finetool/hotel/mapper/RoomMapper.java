package cn.finetool.hotel.mapper;

import cn.finetool.common.po.Room;
import cn.finetool.common.vo.RoomInfoVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RoomMapper extends BaseMapper<Room> {
    List<Room> getRoomPriceList(List<String> roomIdList);

    RoomInfoVo queryRoomInfoByDate(@Param(("roomId")) String roomId,
                                   @Param("date") LocalDate date);
}
