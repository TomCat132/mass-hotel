package cn.finetool.rabbitmq.listener;


import cn.finetool.api.service.OrderAPIService;
import cn.finetool.common.constant.MqQueue;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.enums.Status;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class RechargeOrderConsumer {

    public static final Logger LOGGER = LoggerFactory.getLogger(RechargeOrderConsumer.class);
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    OrderAPIService orderAPIService;


    /**
     * ========== 充值订单超时消费者 ==========
     **/
    @RabbitListener(queues = MqQueue.RECHARGE_ORDER_QUEUE)
    public void RechargeOrderConsumer(Map<String, Object> message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        String orderId = (String) message.get("orderId");
        if (Objects.nonNull(orderId)) {
            Boolean timeoutOrder = redisTemplate.hasKey(RedisCache.RECHARGE_ORDER_IS_TIMEOUT + orderId);
            if (Boolean.FALSE.equals(timeoutOrder)) {
                LOGGER.info("订单：{} 未超时，不做处理", orderId);
            } else {
                LOGGER.info("订单：{} 超时未支付，取消订单", orderId);
                //更新订单状态
                orderAPIService.changeOrderStatus(orderId, Status.ORDER_CLOSE.getCode(), null);
                // 清除标记
                redisTemplate.delete(RedisCache.RECHARGE_ORDER_IS_TIMEOUT + orderId);
            }
            try {
                channel.basicAck(tag, false);
            } catch (IOException e) {
                // 消息重试
                try {
                    channel.basicNack(tag, false, true);
                } catch (IOException ex) {
                    LOGGER.error("消息重试失败：{}", ex.getMessage());
                    // TODO:记录日志

                }
            }
        }
    }


}
