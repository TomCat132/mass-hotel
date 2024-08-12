package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 房间预定信息表
 */
@Data
@TableName(value = "tb_room_booking")
public class RoomBooking implements Serializable {

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单号
     */
    private String orderId;

    /**
     *  room_date表: id 主键
     */
    private Integer RoomDateId;

    /**
     * 入住时间
     */
    private LocalDate checkInDate;

    /**
     * 押金
     */
    private LocalDateTime securityDeposit;

}
