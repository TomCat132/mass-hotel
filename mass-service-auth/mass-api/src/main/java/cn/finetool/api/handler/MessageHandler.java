package cn.finetool.api.handler;

import cn.finetool.api.mapper.MessageBoxMapper;
import cn.finetool.common.enums.SystemTag;
import cn.finetool.common.po.MessageBox;
import cn.finetool.common.util.MessageBoxUtil;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MessageHandler {
    
   
    @Resource
    private MessageBoxMapper messageBoxMapper;

    /**
     * 发送消息给单个用户
     * @param senderId: 发送者id
     * @param acceptId: 接收者id
     * @param messageContent: 消息内容
     * @param affairId: 事项ID
     */
    public void sendMessage(String senderId, String acceptId, String messageContent, String affairId) {
        MessageBox messageBox = MessageBoxUtil.createMessageBox(senderId, acceptId, messageContent, affairId);
        messageBoxMapper.insert(messageBox);
    }

    /**
     * 系统消息提醒
     * @param acceptId
     * @param messageContent
     * @param affairId
     */
    public void sendMessage(String acceptId, String messageContent, String affairId){
        MessageBox messageBox = MessageBoxUtil.createMessageBox(SystemTag.SYSTEM_SENDER.desc(), acceptId, messageContent, affairId);
        messageBoxMapper.insert(messageBox);
    }

    /**
     * 发送消息给多个用户
     * @param senderId: 发送者id
     * @param acceptIds: 接收者id列表
     * @param messageContent: 消息内容
     * @param affairId: 事项ID
     */
    public void sendMessage(String senderId, List<String> acceptIds, String messageContent, String affairId) {
        for (String acceptId : acceptIds) {
            MessageBox messageBox = MessageBoxUtil.createMessageBox(senderId, acceptId, messageContent, affairId);
            messageBoxMapper.insert(messageBox);
        }
    }

    public void sendMessage(List<String> acceptIds, String messageContent, String affairId) {
        for (String acceptId : acceptIds) {
            MessageBox messageBox = MessageBoxUtil.createMessageBox(SystemTag.SYSTEM_SENDER.desc(), acceptId, messageContent, affairId);
            messageBoxMapper.insert(messageBox);
        }
    }




}
