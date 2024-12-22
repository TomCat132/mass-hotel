package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("tb_message_box")
public class MessageBox {

    /**
     * message_id 消息ID
     */
    @TableId(value = "message_id")
    private String messageId;

    /**
     * sender_id 发送者ID
     */
    @TableField("sender_id")
    private String senderId;

    @TableField("message_content")
    private String messageContent;

    /**
     * affair_id 事项ID
     */
    @TableField("affair_id")
    private String affairId;

    /**
     * accept_id 接收者id
     */
    @TableField("accept_id")
    private String acceptId;

    /**
     * sender_time 发送时间
     */
    @TableField("sender_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime senderTime;

    /**
     * is_delete 0 未删除 1 已删除
     */
    @TableField("is_delete")
    private Integer isDelete;

    /**
     * status 0 未读 1 已读 
     */
    @TableField("status")
    private Integer status;
}
