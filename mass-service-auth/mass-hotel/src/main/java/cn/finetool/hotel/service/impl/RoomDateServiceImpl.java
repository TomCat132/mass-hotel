package cn.finetool.hotel.service.impl;

import cn.finetool.common.po.RoomDate;
import cn.finetool.hotel.mapper.RoomDateMapper;
import cn.finetool.hotel.service.RoomDateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RoomDateServiceImpl extends ServiceImpl<RoomDateMapper, RoomDate> implements RoomDateService {
}
