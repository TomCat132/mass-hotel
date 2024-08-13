package cn.finetool.hotel.service.impl;

import cn.finetool.common.po.RoomDate;
import cn.finetool.hotel.mapper.RoomDateMapper;
import cn.finetool.hotel.service.RoomDateService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RoomDateServiceImpl extends ServiceImpl<RoomDateMapper, RoomDate> implements RoomDateService {

    @Resource
    private RoomDateMapper roomDateMapper;

    @Override
    public void updateRoomDateStatus(Integer roomDateId, LocalDate checkInDate, LocalDate checkOutDate, Integer status) {
        // 只入住一天的情况
        if (checkInDate.equals(checkOutDate)){
            roomDateMapper.update(new UpdateWrapper<RoomDate>()
                    .eq("id",roomDateId)
                    .eq("date",checkInDate)
                    .set("status",status));
        } else {
            // 入住多天的情况
            roomDateMapper.updateRoomDateStatus(roomDateId, checkInDate, checkOutDate, status);
        }
    }
}
