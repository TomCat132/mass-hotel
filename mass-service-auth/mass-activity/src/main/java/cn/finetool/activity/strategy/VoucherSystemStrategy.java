package cn.finetool.activity.strategy;

import cn.finetool.activity.mapper.VoucherMapper;
import cn.finetool.activity.mapper.VoucherSystemMapper;
import cn.finetool.api.handler.MessageHandler;
import cn.finetool.api.service.AccountAPIService;
import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqRoutingKey;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.Voucher;
import cn.finetool.common.po.VoucherSystem;
import cn.finetool.common.util.JsonUtil;
import cn.finetool.common.util.MqUtils;
import cn.finetool.common.util.Strings;
import cn.finetool.common.util.TimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
    private VoucherMapper voucherMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private MessageHandler messageHandler;
    @Resource
    private AccountAPIService accountAPIService;

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

    @Override
    public boolean decreaseVoucherStock(String voucherId, String userId) {
        //先判断是有限还是无限
        if (Strings.equals(-1, voucherSystemMapper.selectOne(new QueryWrapper<VoucherSystem>()
                .eq("voucher_id", voucherId)).getCount())) {
            sendMessage(voucherId, userId, true);
            return true;
        }
        RLock lock = redissonClient.getLock(RedisCache.SYSTEM_LOCK + voucherId);

        try {
            boolean isLocked = lock.tryLock(10, 10, TimeUnit.SECONDS);
            if (isLocked) {
                int success = voucherSystemMapper.update(new UpdateWrapper<VoucherSystem>()
                        .setSql("stock_num = stock_num - 1")
                        .eq("voucher_id", voucherId));
                if (success > 0) {
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
            VoucherSystem system = voucherSystemMapper.selectOne(new QueryWrapper<VoucherSystem>()
                    .eq("voucher_id", voucherId));
            String voucherSubTitle = system.getVoucherSubTitle();
            String[] split = voucherSubTitle.replace("\\n", "\n").split("\n");

            String messageContent1 = "恭喜您，您领取的【" + system.getVoucherTitle() + "】优惠券已发放成功！"
                    + "&使用规则:【" + split[0] + "】" + "【" + split[1] + "】"
                    + "，&有效期至：" + system.getEndTime().toString();
            messageHandler.sendMessage(userId, messageContent1, voucherId);

            String messageContent2 = "用户：【" + userId + "】" + "领取了" + system.getVoucherTitle();
            Voucher voucherInfo = voucherMapper.selectOne(new QueryWrapper<Voucher>()
                    .eq("voucher_id", voucherId));
            List<String> acceptIds = accountAPIService.findMerchantEmployee(voucherInfo.getMerchantId());
            messageHandler.sendMessage(acceptIds, messageContent2, voucherId);
        } else {
            // 领取失败
            VoucherSystem system = voucherSystemMapper.selectOne(new QueryWrapper<VoucherSystem>()
                    .eq("voucher_id", voucherId));
            String messageContent = system.getVoucherTitle() + "已被抢光";
            messageHandler.sendMessage(userId, messageContent, voucherId);
        }
    }
    
}
