package cn.finetool.hotel.service.impl;

import cn.finetool.common.po.RoomInfo;
import cn.finetool.common.util.Response;
import cn.finetool.hotel.mapper.RoomInfoMapper;
import cn.finetool.hotel.service.RoomInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo> implements RoomInfoService {

    @Override
    public Response addRoomInfo(RoomInfo roomInfo) {
        // TODO: 简单的添加功能，尚未考虑细节
        save(roomInfo);
        return Response.success("添加成功");
    }
}
