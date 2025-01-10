package cn.finetool.activity.strategy;

import cn.finetool.activity.service.CouponService;
import cn.finetool.activity.service.VoucherService;
import cn.finetool.api.handler.MessageHandler;
import cn.finetool.api.service.AccountAPIService;
import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqRoutingKey;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.enums.Status;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.Voucher;
import cn.finetool.common.po.VoucherCoupon;
import cn.finetool.common.util.MqUtils;
import cn.finetool.common.util.Strings;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CouponStrategy extends SaveVoucherStrategy {

    public static final Logger LOGGER = LoggerFactory.getLogger(CouponStrategy.class);
    @Resource
    private CouponService couponService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private MessageHandler messageHandler;
    @Resource
    private VoucherService voucherService;
    @Resource
    private AccountAPIService accountAPIService;

    @PostConstruct
    public void init() {
        if (redissonClient != null) {
            LOGGER.info("=====RedissonClient is initialized successfully.");
        } else {
            LOGGER.error("Failed to initialize RedissonClient.");
        }
    }

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
        if (Duration.between(voucherCoupon.getBeginTime(), voucherCoupon.getEndTime()).toDays() < 7) {
            throw new BusinessRuntimeException("优惠券有效时间至少为一周");
        }
        couponService.save(voucherDto.getVoucherCoupon());
        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put("voucherId", voucherDto.getVoucherId());
        messageBody.put("voucherType", voucherDto.getVoucherType());
        messageBody.put("voucherTitle", voucherDto.getVoucherCoupon().getVoucherTitle());
        messageBody.put("merchantId", voucherDto.getMerchantId());
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
    public boolean decreaseVoucherStock(String voucherId, String userId) {
        //先判断是有限还是无限
        if (Strings.equals(-1, couponService.getById(voucherId).getCount())) {
            sendMessage(voucherId, userId, true);
            return true;
        }
        RLock lock = redissonClient.getLock(RedisCache.COUPON_LOCK + voucherId);

        try {
            boolean isLocked = lock.tryLock(10, 10, TimeUnit.SECONDS);
            if (isLocked) {
                boolean success = couponService.update()
                        .setSql("count = count - 1")
                        .eq("voucher_id", voucherId)
                        .update();
                if (success) {
                    // 发送消息到消息盒子
                    sendMessage(voucherId, userId, true);
                    return true;
                } else {
                    lock.unlock();
                    // 发送消息到消息盒子
                    sendMessage(voucherId, userId, false);
                    throw new BusinessRuntimeException("优惠券已发放完");
                }
            } else {
                throw new BusinessRuntimeException("当前人数过多,请重试~");
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void sendMessage(String voucherId, String userId, boolean success) {
        if (success) {
            VoucherCoupon coupon = couponService.getById(voucherId);
            String voucherSubTitle = coupon.getVoucherSubTitle();
            String[] split = voucherSubTitle.replace("\\n", "\n").split("\n");

            String messageContent1 = "恭喜您，您领取的【" + coupon.getVoucherTitle() + "】优惠券已发放成功！"
                    + "&使用规则:【" + split[0] + "】" + "【" + split[1] + "】"
                    + "，&有效期至：" + coupon.getEndTime().toString();
            messageHandler.sendMessage(userId, messageContent1, voucherId);

            String messageContent2 = "用户：【" + userId + "】" + "领取了" + coupon.getVoucherTitle();
            Voucher voucherInfo = voucherService.getOne(new QueryWrapper<Voucher>()
                    .eq("voucher_id", voucherId));
            List<String> acceptIds = accountAPIService.findMerchantEmployee(voucherInfo.getMerchantId());
            messageHandler.sendMessage(acceptIds, messageContent2, voucherId);
        } else {
            // 领取失败
            VoucherCoupon coupon = couponService.getById(voucherId);
            String messageContent = coupon.getVoucherTitle() + "已被抢光";
            messageHandler.sendMessage(userId, messageContent, voucherId);
        }
    }

    @Override
    public void deleteVoucher(String voucherId) {
        VoucherCoupon coupon = couponService.getOne(new QueryWrapper<VoucherCoupon>()
                .eq("voucher_id", voucherId));
        // 上架过程中不能删除！！！
        if (Strings.equals(coupon.getStatus(), Status.VOUCHER_UP.code())){
            throw new BusinessRuntimeException("优惠券上架过程中不能删除！");
            // 如果优惠券带发布状态（创建未上架）
        } else if (Strings.equals(coupon.getStatus(), Status.VOUCHER_PREPARE.code())){
            // 删除上架标记
            
        }
        couponService.update()
                .set("flag", Status.IS_DELETED.code())
                .eq("voucher_id", voucherId)
                .update();
    }
}
