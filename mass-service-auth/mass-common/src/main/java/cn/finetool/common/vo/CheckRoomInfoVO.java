package cn.finetool.common.vo;

import cn.finetool.common.po.Room;
import cn.finetool.common.po.RoomBooking;
import lombok.Data;

@Data
public class CheckRoomInfoVO implements java.io.Serializable {

    /**
     * 房间入住信息表
     */
    private RoomBooking roomBooking;

    /**
     * 房间信息
     */
    private Room room;

    /**
     * 房间状态: 可用，不可用
     */
    private Integer status;
}
