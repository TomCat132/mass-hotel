package cn.finetool.order.service;

import cn.finetool.common.dto.CreateOrderDto;
import cn.finetool.common.dto.OrderPayDto;
import cn.finetool.common.po.OrderStatus;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.util.Response;
import cn.finetool.common.vo.OrderVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RoomOrderService extends IService<RoomOrder> {

    void createRoomOrderInfo(CreateOrderDto createOrderDto);

    Response accountPayRoomOrder(OrderPayDto orderPayDto);

    List<OrderVo> getRoomOrderList(String userId);

    Response queryOrder(String orderId);
}
