package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tb_point_exchange")
public class PointExchange implements Serializable {

    /**
     * 主键id
     */
    @TableId(value = "id")
    private String id;

    /**
     * CDK码
     */
    @TableField(value = "cdk")
    private String cdk;

    /**
     * 兑换类型
     */
    @TableField(value = "exchange_type")
    private Integer exchangeType;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 开始时间
     */
    @TableField(value = "begin_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    /**
     * 状态 0 待上架，1 可兑换，2 已售罄 3.已结束
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 商家ID
     */
    @TableField(value = "merchant_id")
    private String merchantId;

    /**
     * 可兑换数量
     */
    @TableField(value = "count")
    private Integer count;

    /**
     * 库存
     */
    @TableField(value = "stock")
    private Integer stock;

    /**
     * 兑换积分
     */
    @TableField(value = "need_points")
    private Integer needPoints;
    
    /**
     * 是否删除：0 未删除，1 已删除
     */
    @TableField(value = "is_delete")
    private Integer isDelete;
}