package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tb_point_usage_record")
public class PointUsageRecord implements Serializable {

    /**
     * 主键id
     */
    @TableId(value = "id")
    private String id;

    /**
     * 积分使用编号
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 积分兑换表唯一id
     */
    @TableField(value = "pe_id")
    private String peId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 是否删除：0 未删除，1 已删除
     */
    @TableField(value = "is_delete")
    private Integer isDelete;

    /**
     * 状态
     */
    @TableField(value = "status")
    private Integer status;
}