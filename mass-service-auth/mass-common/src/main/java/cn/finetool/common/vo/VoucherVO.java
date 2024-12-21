package cn.finetool.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 活动券显示数据
 */
@Data
public class VoucherVO implements java.io.Serializable {

    /**
     * 活动券编号
     */
    private String voucherId;

    /**
     * 类型
     */
    private Integer voucherType;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 操作员工编号
     */
    private String userId;

    /**
     * 标题
     */
    private String voucherTitle;

    /**
     * 副标题
     */
    private String voucherSubTitle;

    /**
     * 描述
     */
    private Integer status;

    /**
     * 优惠券数量
     */
    private Integer count;


    /**
     * 有效开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime beginTime;

    /**
     * 有效结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
}
