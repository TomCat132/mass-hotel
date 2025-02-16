package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private LocalDateTime createTime;

    /**
     * 开始时间
     */
    @TableField(value = "begin_time")
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private LocalDateTime endTime;

    /**
     * 状态
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
     * 是否删除：0 未删除，1 已删除
     */
    @TableField(value = "is_delete")
    private Integer isDelete;
}