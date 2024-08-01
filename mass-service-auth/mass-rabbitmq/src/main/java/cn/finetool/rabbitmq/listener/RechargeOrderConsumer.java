package cn.finetool.rabbitmq.listener;


import cn.finetool.api.service.OrderAPIService;
import cn.finetool.common.constant.MqQueue;
import cn.finetool.common.constant.RedisCache;

import cn.finetool.common.enums.Status;
import cn.finetool.rabbitmq.domain.MessageDo;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class RechargeOrderConsumer {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    OrderAPIService orderAPIService;

    /** ========== 充值订单超时消费者 ========== */
    @RabbitListener(queues = MqQueue.RECHARGE_ORDER_QUEUE)
    public void rechargeOrderTimeoutConsumer(MessageDo messageDo, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag){

       String orderId = (String) messageDo.getMessageMap().get("orderId");

       Boolean timeoutOrder = redisTemplate.hasKey(RedisCache.RECHARGE_ORDER_IS_TIMEOUT + orderId);
       if (Boolean.FALSE.equals(timeoutOrder)){
            log.info("订单：{} 未超时，不做处理", orderId);
       } else{
            log.info("订单：{} 超时未支付，取消订单", orderId);
            //更新订单状态
            orderAPIService.updateOrderStatus(orderId, Status.ORDER_CANCEL.getCode());
            // 清除标记
            redisTemplate.delete(RedisCache.RECHARGE_ORDER_IS_TIMEOUT + orderId);
       } try {
                channel.basicAck(tag,false);
            } catch (IOException e) {
                // 消息重试
                try {
                    channel.basicNack(tag, false, true);
                } catch (IOException ex) {
                    log.error("消息重试失败：{}", ex.getMessage());
                    // TODO:记录日志

                }
            }
    }


}
