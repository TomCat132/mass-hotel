package cn.finetool.rabbitmq.listener;

import cn.finetool.api.handler.MessageHandler;
import cn.finetool.api.service.AccountAPIService;
import cn.finetool.api.service.HotelAPIService;
import cn.finetool.api.service.OrderAPIService;
import cn.finetool.common.constant.MqQueue;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.enums.Status;
import cn.finetool.common.enums.SystemTag;
import cn.finetool.common.po.RoomBooking;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.util.JsonUtil;
import cn.finetool.common.util.Strings;
import com.rabbitmq.client.Channel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

@Component
public class RoomOrderConsumer {

    public static final Logger LOGGER = LoggerFactory.getLogger(RoomOrderConsumer.class);
    @Resource
    private  RedisTemplate<String, Object> redisTemplate;
    @Resource
    private OrderAPIService orderAPIService;
    @Resource
    private HotelAPIService hotelAPIService;
    @Resource
    private MessageHandler messageHandler;
    @Autowired
    private AccountAPIService accountAPIService;

    @PostConstruct
    public void init() {
        Object o = redisTemplate.opsForValue().get(RedisCache.ROOM_RESERVED_ORDER_IS_TIMEOUT + "10141325968225481027584");
        if (Objects.isNull(o)){
            System.out.println("订单存在:true");
        }
        System.out.println("订单不存在:false");
    }
    /**
     * 房间预定订单超时未支付，取消订单
     *
     * @param messageBody: 消息体
     * @param channel      : 消息通道
     * @param tag:         消息标签
     */
    @SneakyThrows
    @RabbitListener(queues = MqQueue.ROOM_RESERVE_ORDER_QUEUE, concurrency = "10")
    public void roomOrderConsumer(String messageBody, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        Map<String, Object> message = JsonUtil.fromJsonString(messageBody, Map.class);

        String orderId = (String) message.get("orderId");

        //传过来的是时间 yyyy-MM-dd 字符串,获取到之后穿换位LocalDate
        LocalDate checkInDate = LocalDate.parse((String) message.get("checkInDate"), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate checkOutDate = LocalDate.parse((String) message.get("checkOutDate"), DateTimeFormatter.ISO_LOCAL_DATE);
        Integer roomDateId = (Integer) message.get("roomDateId");

        Object orderTag = redisTemplate.opsForValue().get(RedisCache.ROOM_RESERVED_ORDER_IS_TIMEOUT + orderId);
        if (Objects.nonNull(orderTag)) {
            LOGGER.info("订单：{} 未超时", orderId);
            Thread.sleep(10000);
            channel.basicAck(tag, false);
        } else {
            LOGGER.info("订单：{} 超时未支付，取消订单", orderId);
            // 更新订单状态 
            orderAPIService.changeOrderStatus(orderId, Status.ORDER_CANCEL.getCode(), null);
            // 恢复房间状态
            hotelAPIService.updateRoomDateStatus(roomDateId, checkInDate, checkOutDate, Status.ROOM_DATE_CAN_USE.getCode());
            // 更改房间预定信息状态
            hotelAPIService.updateRoomBookingStatus(orderId, Status.ROOMBOOKING_CANCEL.code());
            // 删除redis标记
            redisTemplate.delete(RedisCache.ROOM_RESERVED_ORDER_IS_TIMEOUT + orderId);
            try {
                //TODO: 发送消息 To: 用户
//                String messageContent = "订单:【" + orderId + "】 &已超时未支付，已取消订单";
//                messageHandler.sendMessage();

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

    /**
     * 超时1小时未办理入住，提醒用户及商户
     *
     * @param messageBody：消息体
     * @param channel：消息通道
     * @param tag             ： 消息标签
     * @throws IOException
     */
    @RabbitListener(queues = MqQueue.ROOM_BOOKING_TIMEOUT_QUEUE)
    public void roomBookingTimeoutConsumer(String messageBody, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        Map<String, Object> message = JsonUtil.fromJsonString(messageBody, Map.class);

        String orderId = (String) message.get("orderId");
        String merchantId = (String) message.get("merchantId");
        String acceptId = (String) message.get("acceptId");
        Object obj = redisTemplate.opsForValue().get(RedisCache.ROOM_BOOKING_TIMEOUT_REMIND + orderId);
        if (Objects.isNull(obj)) {
            LOGGER.info("房间预定订单:{} ,已完成入住办理", orderId);
            channel.basicAck(tag, false);
        } else {
            // 发送消息超时提醒 To: 用户 
            messageHandler.sendMessage(SystemTag.SYSTEM_SENDER.desc(),
                    acceptId,
                    "您预定的房间尚未办理入住, 请及时办理哦~",
                    orderId);
            // 发送消息超时提醒 To: 商户
            // 查询商户的所有员工编号
            List<String> acceptIds = accountAPIService.findMerchantEmployee(merchantId);
            messageHandler.sendMessage(SystemTag.SYSTEM_SENDER.code(),
                    acceptIds,
                    "房间预定订单:" + orderId + " 长时间未办理入住, 可前往提醒",
                    orderId);
            channel.basicAck(tag, false);
        }
    }

    /**
     * 订单结束（提前1小时） 提醒
     *
     * @param messageBody
     * @param channel
     * @param tag
     */
    @RabbitListener(queues = MqQueue.ROOM_ORDER_ENDING_REMIND_QUEUE)
    public void roomOrderEndingRemindConsumer(String messageBody, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        Map<String, Object> message = JsonUtil.fromJsonString(messageBody, Map.class);
        String merchantId = (String) message.get("merchantId");
        String orderId = (String) message.get("orderId");
        RoomBooking roomBooking = JsonUtil.fromJsonString((String) message.get("roomBooking"), RoomBooking.class)  ;
        // 检查订单状态是否已经结束
        if (Strings.equals(roomBooking.getStatus(), Status.ROOMBOOKING_CHECK_OUT.code())) {
            LOGGER.info("订单：{} 已结束", orderId);
            channel.basicAck(tag, false);
        }
        if (Strings.equals(roomBooking.getStatus(), Status.ROOMBOOKING_CHECK_IN.code())) {
            // 发送消息提醒 To: 商户
            List<String> acceptIds = accountAPIService.findMerchantEmployee(merchantId);
            String messageContent1 = "订单:【" + orderId + "】 即将超时，请及时提醒用户";
            messageHandler.sendMessage(acceptIds, messageContent1, orderId);
            // 发送消息提醒 To: 用户
            String messageContent2 = "您预定的房间即将超时，请及时办理入住";
            RoomOrder roomOrder = orderAPIService.queryOrderInfo(orderId);
            messageHandler.sendMessage(roomOrder.getUserId(), messageContent2, orderId);
        }
    }
}
