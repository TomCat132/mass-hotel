package cn.finetool.rabbitmq.listener;

import cn.finetool.api.service.ActivityAPIService;
import cn.finetool.common.constant.MqQueue;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.enums.Status;
import cn.finetool.common.util.JsonUtil;
import cn.finetool.common.util.MapUtils;
import cn.finetool.common.util.Strings;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
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
public class PointMallConsumer {

    public static final Logger LOGGER = LoggerFactory.getLogger(PointMallConsumer.class);
    @Resource
    private ActivityAPIService activityAPIService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @RabbitListener(queues = MqQueue.POINT_MALL_PRODUCT_QUEUE)
    public void pointMallConsumer(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag){

        try {
            Map map = JsonUtil.fromJsonString(message, Map.class);
            String id = MapUtils.getString(map, "id");
            String type = MapUtils.getString(map, "type");
            if (Strings.equals(type, "up")){
                this.pointProductUp(id);
            } else if (Strings.equals(type, "down")) {
                this.pointProductDown(id);
            }
            channel.basicAck(tag, false);
        } catch (Exception e) {
            LOGGER.error("PointMallConsumer error, message:{}, tag:{}", message, tag, e);
        }
    }

    /**
     * 积分商品下架
     * @param id 主键
     */
    private void pointProductDown(String id) {
        Object sign = redisTemplate.opsForValue().get(RedisCache.POINT_PRODUCT_UP_SIGN + id);
        if (Objects.nonNull(sign)){
            activityAPIService.updateStatusById(id, Status.POINT_EXCHANGE_CAN_EXCHANGE.code());
        }
    }

    /**
     * 积分商品上架
     * @param id
     */
    private void pointProductUp(String id) {
        Object sign = redisTemplate.opsForValue().get(RedisCache.POINT_PRODUCT_DOWN_SIGN + id);
        if (Objects.nonNull(sign)){
            activityAPIService.updateStatusById(id, Status.POINT_EXCHANGE_END.code());
        }
    }
    
    
}
