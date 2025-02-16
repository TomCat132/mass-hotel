package cn.finetool.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PointProDuctVO implements java.io.Serializable {

    /**
     * 唯一id （tb_point_exchange） 
     */
    private String id;

    /**
     * 兑换名称
     */
    private String productName;

    /**
     * 兑换数量
     */
    private Integer stock;

    /**
     * 可兑换数量（每人）
     */
    private Integer count;

    /**
     * 兑换积分
     */
    private Integer needPoints;

    /**
     * 兑换商品类型
     */
    private Integer exchangeType;

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

    /**
     * 状态 tb_point_exchange
     */
    private Integer status;
}
