package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天记录表
 */
@Data
@TableName("tb_chat_record")
public class ChatRecord implements Serializable {

    @TableId("chat_id")
    private String chatId;

    /**
     * 请求编号
     */
    @TableField("request_id")
    private String requestId;

    /**
     * 数据创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 扩展字段(暂不使用)
     */
    @TableField("extr1")
    private String extr1;

    /**
     * 扩展字段(暂不使用)
     */
    @TableField("extr2")
    private String extr2;
}