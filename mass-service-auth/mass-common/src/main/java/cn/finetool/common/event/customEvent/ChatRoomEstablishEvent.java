package cn.finetool.common.event.customEvent;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ChatRoomEstablishEvent extends ApplicationEvent {

    /**
     * 客户编号
     */
    private final String customId;

    /**
     * 服务人员编号
     */
    private final String conductorId;

    /**
     * 聊天室编号
     */
    private final String chatId;
    
    public ChatRoomEstablishEvent(Object source, String customId, String conductorId, String chatId) {
        super(source);
        this.customId = customId;
        this.conductorId = conductorId;
        this.chatId = chatId;
    }
}
