package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户签到表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_user_sign")
public class UserSign implements Serializable {

    /**
     * Id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private String userId;

    @TableField("sign_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime signTime;
}
