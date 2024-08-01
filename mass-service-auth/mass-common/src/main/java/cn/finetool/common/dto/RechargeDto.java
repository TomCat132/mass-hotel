package cn.finetool.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RechargeDto implements Serializable {

    /**
     * 充值方案
     */
    private Integer planId;

    /**
     *  用户支付金额
     */
    private BigDecimal userPayAmount;

    /**
     *  赠送金额
     */
    private BigDecimal bonusAmount;



}
