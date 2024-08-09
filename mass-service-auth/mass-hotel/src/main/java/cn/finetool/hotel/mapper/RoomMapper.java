package cn.finetool.hotel.mapper;

import cn.finetool.common.po.Room;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoomMapper extends BaseMapper<Room> {
    List<Room> getRoomPriceList(List<String> roomIdList);
}
