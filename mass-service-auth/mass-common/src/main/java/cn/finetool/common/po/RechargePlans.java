package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author rock
 * @since 2024-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_recharge_plans")
public class RechargePlans implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "plan_id", type = IdType.AUTO)
    private Integer planId;

    /**
     * 用户支付金额
     */
    private BigDecimal userPayAmount;

    /**
     * 赠送金额
     */
    private BigDecimal bonusAmount;

    /**
     * 总充值金额
     */
    private BigDecimal totalAmount;

    /**
     * 方案种类
     */
    private Integer planType;

    /**
     *  方案状态
     */
    private Integer status;

    /**
     * 活动方案下架时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expirationDate;


    /**
     * 0：正常 ;1：删除
     */
    private Integer isDelete;
}
