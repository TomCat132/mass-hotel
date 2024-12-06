package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_voucher_coupon")
public class VoucherCoupon implements Serializable {

    @TableId("voucher_id")
    private String voucherId;

    @TableField("voucher_title")
    private String voucherTitle;

    @TableField("voucher_sub_title")
    private String voucherSubTitle;

    @TableField("count")
    private Integer count;

    @TableField("voucher_rule")
    private BigDecimal voucherRule;

    @TableField("voucher_amount")
    private BigDecimal voucherAmount;

    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime endTime;

    @TableField("status")
    private Integer status;

    @TableField("flag")
    private Integer flag;
}
