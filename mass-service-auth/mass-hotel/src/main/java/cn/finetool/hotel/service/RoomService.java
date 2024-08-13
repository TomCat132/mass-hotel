package cn.finetool.hotel.service;

import cn.finetool.common.dto.RoomBookingDto;
import cn.finetool.common.dto.RoomDto;
import cn.finetool.common.po.Room;

import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;

public interface RoomService extends IService<Room> {
    Response addRoomInfo(RoomDto roomDto);

    Response queryRoomInfo(String roomId);

    List<Integer> queryResidualRoomInfo(String roomId, LocalDate checkInDate, LocalDate checkOutDate);

    Response calculatePrice(RoomBookingDto roombookingDto);
}
