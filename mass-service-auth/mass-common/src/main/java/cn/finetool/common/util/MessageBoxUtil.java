package cn.finetool.common.util;

import cn.finetool.common.enums.SystemTag;
import cn.finetool.common.po.MessageBox;
import com.google.common.collect.ImmutableMap;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class MessageBoxUtil {

    public static final SnowflakeIdWorker ID_WORKER = new SnowflakeIdWorker(10, 0);

    /**
     * 创建消息盒子对象
     *
     * @param senderId
     * @param acceptId
     * @param messageContent
     * @param affairId
     * @return
     */
    public static MessageBox createMessageBox(String senderId, String acceptId, String messageContent, String affairId) {
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

    /**
     * 系统消息提醒
     * @param acceptId
     * @param isToUser
     * @param messageContent1
     * @param isToMerchant
     * @param acceptIdList
     * @param messageContent2
     * @param affairId
     * @return
     */
    public static ImmutableMap<Object, Object> buildMessageParams(
            Boolean isToUser,
            String acceptId, 
            String messageContent1,
            Boolean isToMerchant,
            List<String> acceptIdList,
            String messageContent2,
            String affairId) {
        ImmutableMap.Builder<Object, Object> builder = ImmutableMap.builder();
        if (isToUser) {
            builder.put("senderId", SystemTag.SYSTEM_SENDER.desc());
            builder.put("acceptId", acceptId);
            builder.put("messageContent1", messageContent1);
            builder.put("affairId", affairId);
        }
        if (isToMerchant) {
            builder.put("acceptIdList", acceptIdList);
            builder.put("messageContent2", messageContent2);
        }
        return ImmutableMap.copyOf(builder.build());
    }


    /**
     * 创建消息盒子参数
     * @param senderId
     * @param acceptId
     * @param isToUser
     * @param messageContent1
     * @param isToMerchant
     * @param acceptIdList
     * @param messageContent2
     * @param affairId
     * @return
     */
    public static ImmutableMap<Object, Object> buildMessageParams(
            String senderId,
            String acceptId, Boolean isToUser,
            String messageContent1,
            Boolean isToMerchant,
            List<String> acceptIdList,
            String messageContent2,
            String affairId) {
        ImmutableMap.Builder<Object, Object> builder = ImmutableMap.builder();
        if (isToUser) {
            builder.put("senderId", senderId);
            builder.put("acceptId", acceptId);
            builder.put("messageContent1", messageContent1);
            builder.put("affairId", affairId);
        }
        if (isToMerchant) {
            builder.put("acceptIdList", acceptIdList);
            builder.put("messageContent2", messageContent2);
        }
        return ImmutableMap.copyOf(builder.build());
    }


}
