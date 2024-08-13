package cn.finetool.hotel.service;

import cn.finetool.common.po.RoomDate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;

public interface RoomDateService extends IService<RoomDate> {
    void updateRoomDateStatus(Integer roomDateId, LocalDate checkInDate, LocalDate checkOutDate, Integer status);
}
