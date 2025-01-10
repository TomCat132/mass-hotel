package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 用户请求
 */
@Data
@TableName("tb_user_request")
public class UserRequest implements java.io.Serializable {

    /**
     * 唯一编号
     */
    @TableId(value = "request_id")
    private String requestId;
    /**
     * 请求用户ID
     */
    private String userId;
    /**
     * 处理人ID
     */
    private String conductorId;
    /**
     * 请求内容
     */
    private String content;
    /**
     *  请求状态 0:未处理 1：已回应 2：已处理 3：已超期
     */
    private int status;
    /**
     * 请求时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime requestTime;
    /**
     * 关联编号
     */
    private String relationId;
    /**
     * 聊天服务记录编号
     */
    private String chatId;
    /**
     * 回应时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime responseTime;

    /**
     * 请求所属商户编号
     */
    private String merchantId;

    /**
     * 请求类型 
     */
    private Integer requestType;
    
    /**
     *  暂不使用（待扩展）
     */
    private String extr1;
    /**
     *暂不使用（待扩展）
     */
    private String extr2;
}
