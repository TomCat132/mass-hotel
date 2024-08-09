package cn.finetool.hotel.mapper;

import cn.finetool.common.po.RoomInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoomInfoMapper extends BaseMapper<RoomInfo> {
    List<RoomInfo> getCanUseRoomInfoList();
}
