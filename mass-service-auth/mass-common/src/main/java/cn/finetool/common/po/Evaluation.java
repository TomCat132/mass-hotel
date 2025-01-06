package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>评价表</p>
 */
@Data
@TableName(value = "tb_evaluation")
public class Evaluation implements Serializable {

    /**
     * 唯一编号
     */
    @TableId(value = "evaluation_id")
    private String evaluationId;

    /**
     * 商户编号
     */
    @TableField(value = "merchant_id")
    private String merchantId;

    /**
     * 订单编号
     */
    @TableField(value = "order_id")
    private String orderId;

    /**
     * 消费用户编号
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 评级(1-5星)
     */
    @TableField(value = "star")
    private Integer star;

    /**
     * 评论内容 (200字以内)
     */
    @TableField(value = "content")
    private String content;

    /**
     * 评论时间
     */
    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
