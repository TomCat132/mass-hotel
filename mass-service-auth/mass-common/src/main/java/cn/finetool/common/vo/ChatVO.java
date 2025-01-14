package cn.finetool.common.vo;

import cn.finetool.common.po.User;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ChatVO implements java.io.Serializable {

    /**
     * 用户名
     */
    private String hotelName;
    /**
     * 用户信息
     */
    private User userInfo;
    /**
     * 最新一条消息
     */
    private String newMessage;
    /**
     * 最新一条消息时间
     */
    private LocalDateTime newMessageTime;
    /**
     * 未读消息数量
     */
    private Integer newMessageUnreadCount;

    /**
     * 聊天室编号
     */
    private String chatId;
}
