package cn.finetool.recharge.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class RabbitTemplateConfig {



    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true);

        //推送到server回调 -》生产者确认机制
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("ConfirmCallback===========");
            log.info("correlationData:{}",correlationData);
            log.info("ack:{}",ack);
            log.info("cause:{}",cause);

            //消息返回给生产者, 路由不到队列时返回给发送者  先returnCallback,再 confirmCallback
            rabbitTemplate.setReturnsCallback((returnedMessage) -> {
                log.info("消息发送失败");
                log.info("Returned message: {}", returnedMessage);
                // 在这里处理消息发送失败的情况
                log.info("Message:{}",returnedMessage.getMessage());
                log.info("Exchange:{}",returnedMessage.getExchange());
                log.info("RoutingKey:{}",returnedMessage.getRoutingKey());
                log.info("ReplyText:{}",returnedMessage.getReplyText());
            });
        });
        return rabbitTemplate;
    }

}
