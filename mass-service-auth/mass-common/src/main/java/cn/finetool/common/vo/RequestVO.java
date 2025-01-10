package cn.finetool.common.vo;

import cn.finetool.common.po.UserRequest;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 呼叫请求展示数据
 */
@Data
public class RequestVO extends UserRequest implements java.io.Serializable {


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
     * 请求人姓名
     */
    private String username;
    
    public RequestVO(){}
    
    public RequestVO(UserRequest userRequest){
        this.requestId = userRequest.getRequestId();
        this.userId = userRequest.getUserId();
        this.content = userRequest.getContent();
        this.status = userRequest.getStatus();
        this.requestType = userRequest.getRequestType();
    }
}
