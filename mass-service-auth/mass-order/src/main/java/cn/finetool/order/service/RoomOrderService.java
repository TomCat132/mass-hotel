package cn.finetool.order.service;

import cn.finetool.common.dto.RoomBookingDto;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RoomOrderService extends IService<RoomOrder> {
    Response createRoomOrder(RoomBookingDto roomBookingDto);
}
