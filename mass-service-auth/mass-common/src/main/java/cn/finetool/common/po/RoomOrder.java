package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 房间订单表
 */
@Data
@TableName(value = "tb_room_order")
public class RoomOrder implements Serializable {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     *  订单号
     */
    private String orderId;

    /**
     *  room_date_id
     */
    private Integer roomDateId;

    /**
     *  用户Id
     */
    private String userId;

    /**
     * 房间号
     */
    private String roomId;

    /**
     * 用户实际支付金额
     */
    private BigDecimal userPayAmount;

    /**
     * 订单创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 数据状态: 0:删除 1：正常
     */
    private Integer status;

    /**
     * 入住时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate checkInDate;

    /**
     * 离店时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate checkOutDate;

    /**
     * 0: 未删   1: 已删
     */
    private Integer isDeleted;
}
