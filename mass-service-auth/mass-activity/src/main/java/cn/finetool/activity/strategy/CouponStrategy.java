package cn.finetool.activity.strategy;

import cn.finetool.activity.service.CouponService;
import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqRoutingKey;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.VoucherCoupon;
import cn.finetool.common.util.MqUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CouponStrategy extends SaveVoucherStrategy {

    public static final Logger LOGGER = LoggerFactory.getLogger(CouponStrategy.class);
    @Resource
    private CouponService couponService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void save(VoucherDto voucherDto) throws JsonProcessingException {
        voucherDto.getVoucherCoupon().setVoucherId(voucherDto.getVoucherId());
        VoucherCoupon voucherCoupon = voucherDto.getVoucherCoupon();
        // 优惠券有效开始时间不能早于当前时间
        LocalDateTime nowTime = LocalDateTime.now();
        if (voucherCoupon.getBeginTime().isBefore(nowTime)) {
            throw new BusinessRuntimeException("优惠券有效开始时间不能早于当前时间");
        }
        // 优惠券有效结束时间不能早于有效开始时间
        if (voucherCoupon.getEndTime().isBefore(voucherCoupon.getBeginTime())) {
            throw new BusinessRuntimeException("优惠券有效结束时间不能早于有效开始时间");
        }
        // 有效时间至少为一周
        if (Duration.between(voucherCoupon.getBeginTime(), voucherCoupon.getEndTime()).toDays() < 7){
            throw new BusinessRuntimeException("优惠券有效时间至少为一周");
        }
        couponService.save(voucherDto.getVoucherCoupon());
        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put("voucherId", voucherDto.getVoucherId());
        messageBody.put("voucherType", voucherDto.getVoucherType());
        // 发送消息到MQ
        // 计算时间差  开始时间 - 当前时间 = 结果（ms）
        long delayUpTime = Duration.between(nowTime, voucherCoupon.getBeginTime()).toMillis();
        // 结束时间 - 开始时间 = Down时间
        MqUtils.sendMessage(rabbitTemplate,
                MqExchange.VOUCHER_UP_EXCHANGE,
                MqRoutingKey.VOUCHER_UP_ROUTING_KEY,
                messageBody,
                message -> {
                    message.getMessageProperties().setContentType("application/json");
                    message.getMessageProperties().getHeaders().put("x-delay", delayUpTime);
                    return message;
                });
        LOGGER.info("优惠券:{}上架时间间隔:{}ms", voucherCoupon.getVoucherId(), delayUpTime);
        long delayDownTime = Duration.between(voucherCoupon.getBeginTime(), voucherCoupon.getEndTime()).toMillis();
        MqUtils.sendMessage(rabbitTemplate,
                MqExchange.VOUCHER_DOWN_EXCHANGE,
                MqRoutingKey.VOUCHER_DOWN_ROUTING_KEY,
                messageBody,
                message -> {
                    message.getMessageProperties().setContentType("application/json");
                    message.getMessageProperties().getHeaders().put("x-delay", delayDownTime);
                    return message;
                });
        LOGGER.info("优惠券:{}下架时间间隔:{}ms", voucherCoupon.getVoucherId(), delayDownTime);

    }

    @Override
    public void changeStatus(String voucherId, Integer status) {
        couponService.update()
                .set("status", status)
                .eq("voucher_id", voucherId)
                .update();
    }

    @Override
    public void decreaseVoucherStock(String voucherId) {
        couponService.update()
                .setSql("count = count - 1")
                .eq("voucher_id", voucherId)
                .update();
    }
}
