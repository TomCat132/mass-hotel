package cn.finetool.rabbitmq.listener;

import cn.finetool.api.handler.MessageHandler;
import cn.finetool.api.service.AccountAPIService;
import cn.finetool.api.service.ActivityAPIService;
import cn.finetool.common.constant.MqQueue;
import cn.finetool.common.enums.Status;
import cn.finetool.common.enums.VoucherType;
import cn.finetool.common.util.JsonUtil;
import cn.finetool.common.util.Strings;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.List;
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
    @Resource
    private MessageHandler messageHandler;
    @Resource
    private AccountAPIService accountAPIService;

    @RabbitListener(queues = MqQueue.VOUCHER_UP_QUEUE)
    public void VoucherUpConsumer(String messageBody, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        Map message = JsonUtil.fromJsonString(messageBody, Map.class);
        String voucherId = (String) message.get("voucherId");
        Integer voucherType = (Integer) message.get("voucherType");
        String merchantId = (String) message.get("merchantId");
        String voucherTitle = (String) message.get("voucherTitle");
        activityAPIService.updateVoucherStatus(voucherType, voucherId, Status.VOUCHER_UP.code());
        LOGGER.info("活动券:{}, 状态修改为:{}", voucherId, Status.VOUCHER_UP.desc());
        // 商户发放的消费券,提醒商户
        if (Strings.isNotBlank(merchantId)){
            // 根据类型执行不同的业务逻辑
            // 修改状态为 UP 状态
            // 系统消息提醒: 商户
            String messageContent = "本店活动券 【" + voucherTitle + "】已上架，" + "&活动券类型: ";
            messageContent += "【" + VoucherType.toDesc(voucherType) + "】";
            messageContent += "&优惠券编号:【" + voucherId + "】";
            List<String> acceptIds = accountAPIService.findMerchantEmployee(merchantId);
            messageHandler.sendMessage(acceptIds, messageContent, voucherId);
        } else {
            //TODO 平台发放的系统券, 提醒所有用户 
        }
        // TODO:系统消息提醒：所有用户（广告，需花钱） 
        channel.basicAck(tag, false);
    }

    @RabbitListener(queues = MqQueue.VOUCHER_DOWN_QUEUE)
    public void VoucherDownConsumer(String messageBody, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        Map message = JsonUtil.fromJsonString(messageBody, Map.class);
        String voucherId = (String) message.get("voucherId");
        Integer voucherType = (Integer) message.get("voucherType");
        // 根据类型执行不同的业务逻辑
        activityAPIService.updateVoucherStatus(voucherType, voucherId, Status.VOUCHER_DOWN.code());
        channel.basicAck(tag, false);
        LOGGER.info("活动券:{}, 状态修改为:{}", voucherId, Status.VOUCHER_DOWN.desc());
    }
}
