package cn.finetool.hotel.service;

import cn.finetool.common.po.RoomInfo;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RoomInfoService extends IService<RoomInfo> {
    Response addRoomInfo(RoomInfo roomInfo);
}
