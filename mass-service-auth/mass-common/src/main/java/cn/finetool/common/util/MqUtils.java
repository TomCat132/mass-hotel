package cn.finetool.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class MqUtils {
    
    private MqUtils(){
        
    }

    /**
     * 发送消息
     * @param rabbitTemplate 配置了的rabbitTemplate
     * @param exchange 交换机
     * @param routingKey 绑定钥匙（交换机 -> key -> 队列）
     * @param messageBody  sendMessage的消息体
     * @param messagePostProcessor 消息后处理器
     * @throws JsonProcessingException json序列化异常
     */
    public static void sendMessage(RabbitTemplate rabbitTemplate,
                                   String exchange,
                                   String routingKey,
                                   Map<String, Object> messageBody,
                                   MessagePostProcessor messagePostProcessor) throws JsonProcessingException {
        rabbitTemplate.convertAndSend(exchange, routingKey, JsonUtil.toJsonString(messageBody), messagePostProcessor);
    }

    /**
     * 发送消息
     * @param rabbitTemplate 配置了的rabbitTemplate
     * @param exchange 交换机
     * @param routingKey 绑定钥匙（交换机 -> key -> 队列）
     * @param messageBody  sendMessage的消息体
     * @param messagePostProcessor 消息后处理器
     * @throws JsonProcessingException json序列化异常
     */
    public static void sendMessage(RabbitTemplate rabbitTemplate,
                                   String exchange,
                                   String routingKey,
                                   String messageBody,
                                   MessagePostProcessor messagePostProcessor) throws JsonProcessingException {
        rabbitTemplate.convertAndSend(exchange, routingKey, messageBody, messagePostProcessor);
    }
}
