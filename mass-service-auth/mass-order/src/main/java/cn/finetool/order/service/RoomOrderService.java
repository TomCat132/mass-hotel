package cn.finetool.order.service;

import cn.finetool.common.dto.CreateOrderDto;
import cn.finetool.common.dto.OrderPayDto;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.util.Response;
import cn.finetool.common.vo.OrderVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;

public interface RoomOrderService extends IService<RoomOrder> {
    
    Response accountPayRoomOrder(OrderPayDto orderPayDto) throws JsonProcessingException;

    Response queryOrder(String orderId);

    RoomOrder queryRoomOrderInfo(String orderId);

    void createRoomOrderInfo(CreateOrderDto createOrderDto);

    List<OrderVO> getRoomOrderList(String userId);
}
