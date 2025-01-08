package cn.finetool.account.event.customEvent;

import java.util.List;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 消息通知事件
 */
@Getter
public class MessageNoticeEvent extends ApplicationEvent {

    /**
     * 消息内容
     */
    private final String message;

    /**
     * 接收者ID
     */
    private final String acceptId;

    /**
     * 关联事项ID
     */
    private final String affairId;

    /**
     * 是否系统发送
     */
    private final boolean isSysSend;

    /**
     * 非必须参数，系统发送时不需要
     */
    private String senderId;

    /**
     * 是否发送给多个用户
     */
    private boolean isSendToMany;

    private List<String> acceptIds;

    /**
     * <h6>source 的作用<h6/>
     * <p>source 用于传递事件源对象，可以是任意对象，在事件监听器中可以获取到该对象。</p>
     * <p>标识事件来源：帮助监听器了解是哪个组件或服务触发的事件<p/>
     * <p>提供上下文信息<p/>
     * <p>设计上的一致性：ApplicationEvent 的一个必须参数<p/>
     */
    /**
     * 非系统发送，需要senderId
     *
     * @param source
     * @param acceptId
     * @param message
     * @param affairId
     * @param isSysSend
     */
    public MessageNoticeEvent(Object source, String acceptId, String message, String affairId,
                              boolean isSysSend) {
        super(source);
        this.acceptId = acceptId;
        this.message = message;
        this.affairId = affairId;
        this.isSysSend = isSysSend;
    }

    public MessageNoticeEvent(Object source, String acceptId, String message, String affairId,
                              boolean isSysSend, String senderId) {
        super(source);
        this.acceptId = acceptId;
        this.message = message;
        this.affairId = affairId;
        this.isSysSend = isSysSend;
        this.senderId = senderId;
    }

    public MessageNoticeEvent(Object source, String acceptId, String message, String affairId,
                              boolean isSysSend, boolean isSendToMany) {
        super(source);
        this.acceptId = acceptId;
        this.message = message;
        this.affairId = affairId;
        this.isSysSend = isSysSend;
        this.isSendToMany = isSendToMany;
    }

    public MessageNoticeEvent(Object source, String acceptId, String message, String affairId,
                              boolean isSysSend, boolean isSendToMany, List<String> acceptIds) {
        super(source);
        this.acceptId = acceptId;
        this.message = message;
        this.affairId = affairId;
        this.isSysSend = isSysSend;
        this.isSendToMany = isSendToMany;
        this.acceptIds = acceptIds;
    }

    public MessageNoticeEvent(Object source, String acceptId, String message, String affairId,
                              boolean isSysSend, String senderId, boolean isSendToMany, List<String> acceptIds) {
        super(source);
        this.acceptId = acceptId;
        this.message = message;
        this.affairId = affairId;
        this.isSysSend = isSysSend;
        this.senderId = senderId;
        this.isSendToMany = isSendToMany;
        this.acceptIds = acceptIds;
    }

}
