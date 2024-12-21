package cn.finetool.rabbitmq.listener;

import cn.finetool.api.service.ActivityAPIService;
import cn.finetool.common.constant.MqQueue;
import cn.finetool.common.enums.Status;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VoucherConsumer {

    public static final Logger LOGGER = LoggerFactory.getLogger(VoucherConsumer.class);
    @Resource
    private ActivityAPIService activityAPIService;

    @RabbitListener(queues = MqQueue.VOUCHER_UP_QUEUE)
    public void VoucherUpConsumer(Map<String, Object> message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        String voucherId = (String) message.get("voucherId");
        Integer voucherType = (Integer) message.get("voucherType");
        // 根据类型执行不同的业务逻辑
        // 修改状态为 UP 状态
        activityAPIService.updateVoucherStatus(voucherType, voucherId, Status.VOUCHER_UP.code());
        LOGGER.info("活动券:{}, 状态修改为:{}", voucherId, Status.VOUCHER_UP.desc());
        channel.basicAck(tag, false);
    }

    @RabbitListener(queues = MqQueue.VOUCHER_DOWN_QUEUE)
    public void VoucherDownConsumer(Map<String, Object> message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        String voucherId = (String) message.get("voucherId");
        Integer voucherType = (Integer) message.get("voucherType");
        // 根据类型执行不同的业务逻辑
        activityAPIService.updateVoucherStatus(voucherType, voucherId, Status.VOUCHER_DOWN.code());
        channel.basicAck(tag, false);
        LOGGER.info("活动券:{}, 状态修改为:{}", voucherId, Status.VOUCHER_DOWN.desc());
    }
}
