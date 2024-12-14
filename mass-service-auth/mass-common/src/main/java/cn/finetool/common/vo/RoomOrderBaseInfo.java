package cn.finetool.common.vo;

import cn.finetool.common.po.Hotel;
import cn.finetool.common.po.OrderStatus;
import cn.finetool.common.po.Room;
import cn.finetool.common.po.RoomBooking;
import cn.finetool.common.po.RoomInfo;
import cn.finetool.common.po.RoomOrder;
import java.util.Map;
import lombok.Data;

/**
 * 房间订单基本信息
 */
@Data
public class RoomOrderBaseInfo {

    /**
     * 房间订单
     */
    private RoomOrder roomOrder;

    /**
     * 订单状态
     */
    private OrderStatus orderStatus;

    /**
     * 房间类型信息
     */
    private RoomInfo roomInfo;

    /**
     * 房间信息
     */
    private Room room;

    /**
     * 房间预定信息
     */
    private RoomBooking roomBooking;

    /**
     * 酒店信息
     */
    private Hotel hotel;

    /**
     * 额外数据
     */
    private Map<String, Object>  extraData;
}
