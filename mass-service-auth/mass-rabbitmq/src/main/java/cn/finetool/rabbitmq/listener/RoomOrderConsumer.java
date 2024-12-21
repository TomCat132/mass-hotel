package cn.finetool.rabbitmq.listener;

import cn.finetool.api.service.HotelAPIService;
import cn.finetool.api.service.OrderAPIService;
import cn.finetool.common.constant.MqQueue;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.enums.Status;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@Component
public class RoomOrderConsumer {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private OrderAPIService orderAPIService;
    @Resource
    private HotelAPIService hotelAPIService;

    @RabbitListener(queues = MqQueue.ROOM_RESERVE_ORDER_QUEUE)
    public void roomOrderConsumer(Map<String, Object> message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        String orderId = (String) message.get("orderId");

        LocalDate checkInDate = (LocalDate) message.get("checkInDate");

        LocalDate checkOutDate = (LocalDate) message.get("checkOutDate");

        Integer roomDateId = (Integer) message.get("roomDateId");

        Boolean timeoutOrder = redisTemplate.hasKey(RedisCache.ROOM_RESERVED_ORDER_IS_TIMEOUT + orderId);

        if (Boolean.TRUE.equals(timeoutOrder)) {
            log.info("订单：{} 未超时", orderId);
        } else {
            log.info("订单：{} 超时未支付，取消订单", orderId);
            // 更新订单状态 恢复房间状态
            orderAPIService.changeOrderStatus(orderId, Status.ORDER_CANCEL.getCode(), null);

            hotelAPIService.updateRoomDateStatus(roomDateId, checkInDate, checkOutDate, Status.ROOM_DATE_CAN_USE.getCode());

            redisTemplate.delete(RedisCache.ROOM_RESERVED_ORDER_IS_TIMEOUT + orderId);

            try {
                channel.basicAck(tag, false);
            } catch (IOException e) {
                // 消息消费失败，重试
                try {
                    channel.basicNack(tag, false, true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
