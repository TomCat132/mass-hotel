package cn.finetool.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RoomBookingDto implements Serializable {


    /**
     * 房间类型ID
     */
    private String roomId;

    /**
     * 入住时间
     */
    private LocalDate checkInDate;

    /**
     * 入住时间
     */
    private LocalDate checkOutDate;

    /**
     * 会员等级
     */
    private Integer memberLevel;

    /**
     * 价格（临时）
     */
    private BigDecimal tempPrice;

    /**
     * 用户应该支付价格
     */
    private BigDecimal userPayAmount;
}
