package cn.finetool.common.event.customEvent;

import org.springframework.context.ApplicationEvent;

public class ChatEvent extends ApplicationEvent {
    
    public ChatEvent(Object source) {
        super(source);
    }
}
