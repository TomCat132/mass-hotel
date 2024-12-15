package cn.finetool.common.vo;

import cn.finetool.common.po.Room;
import cn.finetool.common.po.RoomBooking;
import cn.finetool.common.po.RoomInfo;
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
    
    private RoomInfo roomInfo;

    /**
     * 房间状态: 可用，不可用
     */
    private Integer status;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 房间号
     */
    private String roomInfoId;
}
