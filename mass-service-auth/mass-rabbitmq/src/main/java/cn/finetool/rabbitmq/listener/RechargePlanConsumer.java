package cn.finetool.rabbitmq.listener;

import cn.finetool.api.service.RechargePlanAPIService;
import cn.finetool.common.Do.MessageDo;
import cn.finetool.common.constant.MqQueue;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.exception.BusinessRuntimeException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@EnableRetry
public class RechargePlanConsumer {

    @Resource
    private RechargePlanAPIService rechargePlanAPIService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /** ============ 充值方案过期消费者 ========== */
    @RabbitListener(queues = MqQueue.RECHARGE_PLAN_DLX_QUEUE)
    @Retryable(value = BusinessRuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public void rechargePlanConsumer(String messageDo, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        MessageDo messageInfo = OBJECT_MAPPER.readValue(messageDo, MessageDo.class);
        Integer planId = (Integer) messageInfo.getMessageMap().get("planId");
        log.info("开始消费充值方案过期消息,planId:{}", planId);
        // 更改充值方案状态
        boolean success = rechargePlanAPIService.updateRechargePlanStatus(planId);
        // 删除缓存
        redisTemplate.delete(RedisCache.VALID_RECHARGE_PLAN_LIST);
        if (!success){
            throw new BusinessRuntimeException(BusinessErrors.DATA_STATUS_ERROR,"更改充值方案状态失败");
        }
        try {
            channel.basicAck(tag,false);
        } catch (IOException e) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_STATUS_ERROR,"更改充值方案状态失败");
        }
    }
}
