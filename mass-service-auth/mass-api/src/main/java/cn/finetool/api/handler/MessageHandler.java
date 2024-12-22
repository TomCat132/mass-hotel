package cn.finetool.api.handler;

import cn.finetool.api.mapper.MessageBoxMapper;
import cn.finetool.common.po.MessageBox;
import cn.finetool.common.util.SnowflakeIdWorker;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MessageHandler {
    
    public static final SnowflakeIdWorker ID_WORKER = new SnowflakeIdWorker(10, 0);
    @Resource
    private MessageBoxMapper messageBoxMapper;

    /**
     * 发送消息给单个用户
     * @param senderId: 发送者id
     * @param acceptId: 接收者id
     * @param messageContent: 消息内容
     * @param affairId: 事项ID
     */
    public void sendMessageToUser(String senderId, String acceptId, String messageContent, String affairId) {
        MessageBox messageBox = createMessageBox(senderId, acceptId, messageContent, affairId);
        messageBoxMapper.insert(messageBox);
    }

    /**
     * 发送消息给多个用户
     * @param senderId: 发送者id
     * @param acceptIds: 接收者id列表
     * @param messageContent: 消息内容
     * @param affairId: 事项ID
     */
    public void sendMessageToMultipleUsers(String senderId, List<String> acceptIds, String messageContent, String affairId) {
        for (String acceptId : acceptIds) {
            MessageBox messageBox = createMessageBox(senderId, acceptId, messageContent, affairId);
            messageBoxMapper.insert(messageBox);
        }
    }
    
    public static MessageBox createMessageBox(String senderId, String acceptId, String messageContent, String affairId){
        MessageBox messageBox = new MessageBox();
        messageBox.setMessageId(String.valueOf(ID_WORKER.nextId()));
        messageBox.setSenderId(senderId);
        messageBox.setMessageContent(messageContent);
        messageBox.setAffairId(affairId);
        messageBox.setAcceptId(acceptId);
        messageBox.setSenderTime(LocalDateTime.now());
        messageBox.setIsDelete(0);
        messageBox.setStatus(0);
        return messageBox;
    }
}
