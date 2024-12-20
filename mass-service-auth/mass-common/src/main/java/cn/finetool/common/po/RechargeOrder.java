package cn.finetool.common.po;

import cn.finetool.common.configuration.CustomLocalDateTimeDeserializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *  充值订单表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_recharge_order")
public class  RechargeOrder implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键：ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 主键：订单号ID
     */
    @TableField(value = "order_id")
    private String orderId;

    /**
     * 充值方案：默认为0（自定义充值）
     */
    @TableField(value = "plan_id")
    private Integer planId;

    /**
     * 用户支付金额
     */
    @TableField(value = "user_pay_amount")
    private BigDecimal userPayAmount;

    /**
     * 总充值金额
     */
    @TableField(value = "total_amount")
    private BigDecimal totalAmount;

    /**
     * 订单创建时间
     */
    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 充值用户Id
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 0: 未删   1: 已删
     */
    private Integer isDeleted;
}
