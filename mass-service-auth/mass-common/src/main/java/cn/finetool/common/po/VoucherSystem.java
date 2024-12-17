package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_voucher_system")
public class VoucherSystem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号id
     */
    @TableId
    private String voucherId;

    /**
     * 优惠券标题
     */
    private String voucherTitle;

    /**
     * 优惠券副标题
     */
    private String voucherSubTitle;

    /**
     * 数量 (-1 为不限量)
     */
    private Integer count;

    /**
     * 优惠券规则
     */
    private String voucherRule;

    /**
     * 优惠金额
     */
    private BigDecimal voucherAmount;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    /**
     * 状态 (0-准备中, 1-已上架, 2-已过期)
     */
    private Integer status;

    /**
     * 是否删除标志
     */
    private Integer flag;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime beginTime;

    /**
     * 逻辑删除标志
     */
    private Integer isDelete;
}
