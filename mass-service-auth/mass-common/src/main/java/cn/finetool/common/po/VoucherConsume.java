package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tb_voucher_consume")
public class VoucherConsume implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动券编号
     */
    @TableId
    private String voucherId;

    /**
     * 消费券编号
     */
    private Integer status;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
