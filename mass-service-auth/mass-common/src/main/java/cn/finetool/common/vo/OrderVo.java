package cn.finetool.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVo implements Serializable {

    /**
     * 订单号ID
     */
    private String orderId;

    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 订单支付金额
     */
    private BigDecimal userPayAmount;

    /**
     * 订单创建时间
     */
    private LocalDateTime createTime;


}
