package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
     * 订单号: HotelOrderPrefix: 1014 +  分布式ID(雪花算法)
     */
    private String orderId;

    /**
     *  room_date表: id 主键
     */
    private Integer RoomDateId;

    /**
     * 入住时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime checkInDate;

    /**
     * 离店时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime checkOutDate;

    /**
     * 押金是否已支付
     */
    private boolean securityDeposit;

    /**
     *  状态：0：已预定 1：办理中 2：入住中 3：已取消 4：已退房 5:已更换
     */
    private Integer status;

    /**
     * 门禁卡编号
     */
    private String doorKey;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}
