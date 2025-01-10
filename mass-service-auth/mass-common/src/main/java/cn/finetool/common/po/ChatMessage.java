package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天内容记录表
 */
@Data
@TableName("tb_chat_message")
public class ChatMessage implements Serializable {

    @TableId("message_id")
    private String messageId;

    /**
     * 聊天记录编号
     */
    @TableField("chat_id")
    private String chatId;

    /**
     * 发送者ID
     */
    @TableField("sender_id")
    private String senderId;

    /**
     * 接收者ID
     */
    @TableField("receiver_id")
    private String receiverId;

    /**
     * 消息文本正文
     */
    @TableField("message")
    private String message;

    /**
     * 是否已读（0：未读 1：已读）
     */
    @TableField("read_state")
    private Integer readState;

    /**
     * 消息发送时间
     */
    @TableField("sender_time")
    private LocalDateTime senderTime;

    /**
     * 已读时间
     */
    @TableField("read_time")
    private LocalDateTime readTime;

    /**
     * 扩展字段（暂不使用）
     */
    @TableField("extr1")
    private String extr1;

    /**
     * 扩展字段（暂不使用）
     */
    @TableField("extr2")
    private String extr2;
}