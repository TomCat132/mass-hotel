package cn.finetool.activity.strategy;

import cn.finetool.activity.mapper.VoucherSystemMapper;
import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqRoutingKey;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.VoucherCoupon;
import cn.finetool.common.po.VoucherSystem;
import cn.finetool.common.util.JsonUtil;
import cn.finetool.common.util.MqUtils;
import cn.finetool.common.util.TimeUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class VoucherSystemStrategy extends SaveVoucherStrategy {

    public static final Logger LOGGER = LoggerFactory.getLogger(VoucherSystemStrategy.class);
    @Resource
    private VoucherSystemMapper voucherSystemMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void save(VoucherDto voucherDto) throws JsonProcessingException {
        if (Objects.nonNull(voucherDto.getVoucherSystem())) {
            voucherDto.getVoucherSystem().setVoucherId(voucherDto.getVoucherId());
            VoucherSystem voucherSystem = voucherDto.getVoucherSystem();
            LocalDateTime nowTime = TimeUtil.now();
            if (voucherSystem.getBeginTime().isBefore(nowTime)) {
                throw new BusinessRuntimeException("优惠券有效开始时间不能早于当前时间");
            }
            // 优惠券有效结束时间不能早于有效开始时间
            if (voucherSystem.getEndTime().isBefore(voucherSystem.getBeginTime())) {
                throw new BusinessRuntimeException("优惠券有效结束时间不能早于有效开始时间");
            }
            voucherSystemMapper.insert(voucherSystem);
            Map<String, Object> messageBody = new HashMap<>();
            messageBody.put("voucherId", voucherDto.getVoucherId());
            messageBody.put("voucherType", voucherDto.getVoucherType());
            messageBody.put("voucherTitle", voucherDto.getVoucherSystem().getVoucherTitle());
            // 发送消息到MQ
            // 计算时间差  开始时间 - 当前时间 = 结果（ms）
            long delayUpTime = Duration.between(nowTime, voucherSystem.getBeginTime()).toMillis();
            // 结束时间 - 开始时间 = Down时间
            MqUtils.sendMessage(rabbitTemplate,
                    MqExchange.VOUCHER_UP_EXCHANGE,
                    MqRoutingKey.VOUCHER_UP_ROUTING_KEY,
                    JsonUtil.toJsonString(messageBody),
                    message -> {
                        message.getMessageProperties().setContentType("application/json");
                        message.getMessageProperties().getHeaders().put("x-delay", delayUpTime);
                        return message;
                    });
            LOGGER.info("系统券:{}上架时间间隔:{}ms", voucherSystem.getVoucherId(), delayUpTime);
            long delayDownTime = Duration.between(voucherSystem.getBeginTime(), voucherSystem.getEndTime()).toMillis();
            MqUtils.sendMessage(rabbitTemplate,
                    MqExchange.VOUCHER_DOWN_EXCHANGE,
                    MqRoutingKey.VOUCHER_DOWN_ROUTING_KEY,
                    JsonUtil.toJsonString(messageBody),
                    message -> {
                        message.getMessageProperties().setContentType("application/json");
                        message.getMessageProperties().getHeaders().put("x-delay", delayDownTime);
                        return message;
                    });
            LOGGER.info("系统券:{}下架时间间隔:{}ms", voucherSystem.getVoucherId(), delayDownTime);
        }
    }

    @Override
    public void changeStatus(String voucherId, Integer status) {
        voucherSystemMapper.update(new UpdateWrapper<VoucherSystem>()
                .set("status", status)
                .eq("voucher_id", voucherId));
    }
    
    
}
