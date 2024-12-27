package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 登录日志
 */
@Data
@TableName(value = "tb_login_log")
public class LoginLog implements java.io.Serializable {
    
    @TableId(value = "log_id")
    private String id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 登录IP
     */
    @TableField(value = "login_ip")
    private String loginIp;

    /**
     * 国家
     */
    @TableField(value = "country")
    private String country;

    /**
     * 省份
     */
    @TableField(value = "province")
    private String province;

    /**
     * 城市
     */
    @TableField(value = "city")
    private String city;

    /**
     * 纬度
     */
    @TableField(value = "lat")
    private String lat;

    /**
     * 经度
     */
    @TableField(value = "lon")
    private String lon;

    /**
     * 登录时间
     */
    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    
    @TableField(value = "system")
    private String System;

    /**
     * 是否删除
     */
    @TableField(value = "is_delete")
    private Boolean isDelete;
}
