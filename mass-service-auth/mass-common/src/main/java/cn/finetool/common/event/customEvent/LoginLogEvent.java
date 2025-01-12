package cn.finetool.common.event.customEvent;

import org.springframework.context.ApplicationEvent;

public class LoginLogEvent extends ApplicationEvent {
    
    private final String userId;
    private final String ip;
    private final String operationSystem;
    
    public LoginLogEvent(Object source, String userId, String ip, String operatingSystem) {
        super(source);
        this.userId = userId;
        this.ip = ip;
        this.operationSystem = operatingSystem;
    }

    public String getUserId() {
        return userId;
    }

    public String getIp() {
        return ip;
    }
    
    public String getOperationSystem() {
        return operationSystem;
    }
}
