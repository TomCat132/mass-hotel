package cn.finetool.common.dto;

import cn.finetool.common.po.OrderStatus;
import cn.finetool.common.po.RoomOrder;
import lombok.Data;

@Data
public class CreateOrderDto implements java.io.Serializable {

    /**
     * 房间订单
     */
    private RoomOrder roomOrder;

    /**
     * 订单状态
     */
    private OrderStatus orderStatus;
}
