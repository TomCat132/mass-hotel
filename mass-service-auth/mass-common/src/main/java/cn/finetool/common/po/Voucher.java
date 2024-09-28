package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tb_voucher")
public class Voucher implements Serializable {

    /**
     * 唯一编号
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 活动券编号
     */
    @TableField("voucher_id")
    private String voucherId;

    /**
     * 活动券类型
     */
    @TableField("voucher_type")
    private Integer voucherType;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
