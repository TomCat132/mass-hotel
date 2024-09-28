package cn.finetool.common.dto;

import cn.finetool.common.po.Coupon;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class VoucherDto implements Serializable {

    /**
     * 唯一编号
     */
    @TableId("id")
    private Integer id;

    /**
     * 活动券编号
     */
    @TableField("voucher_id")
    private String voucherId;

    /**
     * 活动券类型
     */
    @TableField("voucher_name")
    private Integer voucherType;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;


    private Coupon coupon;
}
