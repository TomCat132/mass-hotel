package cn.finetool.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 限时活动VO
 */
@Data
public class ActivityVO implements java.io.Serializable {

    /**
     * 编号
     */
    private String activityId;

    /**
     * 发放活动商户名
     */
    private String merchantName;

    /**
     * 活动类型
     */
    private Integer activityType;

    /**
     * 活动名称
     */
    private String activityTitle;

    /**
     * 描述
     */
    private String activitySubTitle;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    /**
     * 剩余数量
     */
    private Integer count;

    /**
     * 活动券活动券独有: 券类型
     */
    private Integer voucherType;
}
