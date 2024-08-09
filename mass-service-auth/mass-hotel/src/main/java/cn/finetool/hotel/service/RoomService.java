package cn.finetool.hotel.service;

import cn.finetool.common.dto.RoomDto;
import cn.finetool.common.po.Room;

import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RoomService extends IService<Room> {
    Response addRoomInfo(RoomDto roomDto);
}
