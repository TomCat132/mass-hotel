package cn.finetool.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderPayDto implements Serializable {

    /**
     * 支付订单号
     */
    private String orderId;

    /**
     * 支付金额
     */
    private BigDecimal userPayAmount;


    /**
     * 订单标题
     */
    private String subject;

    /**
     * 订单类型
     */
    private Integer orderType;
}
