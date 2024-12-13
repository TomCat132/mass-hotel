package cn.finetool.recharge.service.impl;

import cn.finetool.common.Do.MessageDo;
import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqRoutingKey;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.dto.PlanDto;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.enums.OrderType;
import cn.finetool.common.enums.RechargePlanType;
import cn.finetool.common.enums.Status;
import cn.finetool.common.exception.BusinessRuntimeException;

import cn.finetool.common.po.RechargePlans;

import cn.finetool.common.util.JsonUtil;
import cn.finetool.common.util.Response;
import cn.finetool.recharge.mapper.RechargePlanMapper;
import cn.finetool.recharge.service.RechargePlanService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class RechargePlanServiceImpl extends ServiceImpl<RechargePlanMapper, RechargePlans> implements RechargePlanService {

    @Resource
    private RechargePlanService rechargePlanService;

    @Resource
    private RechargePlanMapper rechargePlanMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response addChargePlan(RechargePlans rechargePlans) throws JsonProcessingException {

        if (rechargePlans == null){
            throw new BusinessRuntimeException(BusinessErrors.PARAM_CANNOT_EMPTY);
        }
        BigDecimal userPayAmount = rechargePlans.getUserPayAmount();
        BigDecimal bonusAmount = rechargePlans.getBonusAmount();
        BigDecimal totalAmount = userPayAmount.add(bonusAmount,new MathContext(2, RoundingMode.HALF_UP));
        rechargePlans.setTotalAmount(totalAmount);
        LocalDateTime nowTime = LocalDateTime.now();
        // 判断充值方案类型
        if (rechargePlans.getPlanType() == RechargePlanType.ACTIVITY.getValue()){
            LocalDateTime expirationDate = rechargePlans.getExpirationDate();
            if (expirationDate == null){
                throw new BusinessRuntimeException(BusinessErrors.PARAM_CANNOT_EMPTY, "活动充值方案必须设置有效期");
            }
            if (expirationDate.isBefore(nowTime)){
                throw new BusinessRuntimeException(BusinessErrors.DATA_STATUS_ERROR, "活动充值方案过期时间不能早于当前时间");
            }
            rechargePlanService.save(rechargePlans);

            MessageDo messageDo = new MessageDo();
            // 活动充值方案倒计时  单位 : ms
            long milliseconds = Duration.between(nowTime, expirationDate).toMillis();

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("planId", rechargePlans.getPlanId());
            messageMap.put("orderType", OrderType.RECHARGE_ORDER.getValue());
            messageDo.setMessageMap(messageMap);

            String messageContent = new ObjectMapper().writeValueAsString(messageDo);
            System.out.println("Message content: " + messageContent);
            System.out.println("Message size: " + messageContent.getBytes().length + " bytes");

            log.info("活动充值方案倒计时: " + milliseconds + " ms");
            rabbitTemplate.convertAndSend(MqExchange.RECHARGE_PLAN_EXCHANGE,
                    MqRoutingKey.RECHARGE_PLAN_ROUTING_KEY,JsonUtil.toJsonString(messageDo),
                    message -> {
                        log.info("设置消息过期时间: " + milliseconds + " ms");
                        message.getMessageProperties().setExpiration(String.valueOf(milliseconds));
                        message.getMessageProperties().setAppId(String.valueOf(rechargePlans.getPlanId()));

                        return message;
                    });
            return Response.success("活动充值方案添加成功");
        }


        rechargePlanService.save(rechargePlans);
        return Response.success("充值方案添加成功");
    }

    /**====== 更改充值方案状态 =====*/
    @Override
    public boolean updateRechargePlanStatus(Integer planId,Integer status) {
        try {
            rechargePlanMapper.update(new UpdateWrapper<RechargePlans>()
                    .set("status", status)
                    .eq("plan_id", planId));
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Response validRechargePlanList() {

        List<RechargePlans> rechargePlansList = (List<RechargePlans>) redisTemplate.opsForValue().get(RedisCache.VALID_RECHARGE_PLAN_LIST);

        if (CollectionUtils.isEmpty(rechargePlansList)){
          rechargePlansList = rechargePlanService.list(new LambdaQueryWrapper<RechargePlans>()
                    .eq(RechargePlans::getStatus, Status.RECHARGE_PLAN_UP.getCode())
                    .eq(RechargePlans::getIsDelete,Status.IS_DELETED.getCode()));
        }

        redisTemplate.opsForValue().set(RedisCache.VALID_RECHARGE_PLAN_LIST, rechargePlansList);

        return Response.success(rechargePlansList);
    }

    @Override
    public Response deleteById(Integer id) {
        rechargePlanService.update()
                .set("is_delete",Status.IS_DELETED.getCode())
                .eq("plan_id", id)
                .update();
        return Response.success(null);
    }

}
