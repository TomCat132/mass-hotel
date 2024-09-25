package cn.finetool.order.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.common.Do.MessageDo;
import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqRoutingKey;
import cn.finetool.common.constant.MqTTL;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.dto.RechargeDto;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.enums.PayType;
import cn.finetool.common.enums.Status;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.util.CommonsUtils;
import cn.finetool.common.util.Response;
import cn.finetool.common.vo.OrderVo;
import cn.finetool.order.mapper.RechargeOrderMapper;
import cn.finetool.order.service.RechargeOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RechargeOrderServiceImpl extends ServiceImpl<RechargeOrderMapper, RechargeOrder> implements RechargeOrderService {

    @Resource
    private RechargeOrderService rechargeOrderService;

    @Resource
    private RechargeOrderMapper rechargeOrderMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Response createOrder(RechargeDto rechargeDto) {
        String token = StpUtil.getTokenValue();

        log.info("token:{}", token);
        String loginId = (String) StpUtil.getLoginId();

        // 先判断是否有未支付的订单
        RechargeOrder orderInfo = rechargeOrderService.getOne(new LambdaQueryWrapper<RechargeOrder>()
                .eq(RechargeOrder::getUserId,loginId)
                .eq(RechargeOrder::getOrderStatus, Status.ORDER_WAIT.getCode()));
        if (orderInfo != null){
            throw new BusinessRuntimeException(BusinessErrors.ORDER_CREATE_REQUEST_FAIL,"您有未支付的订单，请先支付或取消订单");
        }

        RechargeOrder rechargeOrder = new RechargeOrder();
        rechargeOrder.setOrderId(CommonsUtils.getWorkerID());
        rechargeOrder.setOrderStatus(Status.ORDER_WAIT.getCode());
        rechargeOrder.setCreateTime(LocalDateTime.now());
        rechargeOrder.setUserId(loginId);
        if (rechargeDto.getPlanId() != null){
            rechargeOrder.setPlanId(rechargeDto.getPlanId());
        }
        rechargeOrder.setUserPayAmount(rechargeDto.getUserPayAmount());
        rechargeOrder.setTotalAmount(rechargeDto.getUserPayAmount().add(rechargeDto.getBonusAmount()));
        save(rechargeOrder);


        //  发送消息通知
        MessageDo messageDo = new MessageDo();
        Map<String,Object> map = new HashMap<>();
        map.put("orderId",rechargeOrder.getOrderId());
        messageDo.setMessageMap(map);

        rabbitTemplate.convertAndSend(MqExchange.ORDER_EXCHANGE,
                MqRoutingKey.ORDER_ROUTING_KEY, messageDo,
                message -> {
                     message.getMessageProperties().getHeaders().put("x-delay",MqTTL.FIVE_MINUTES);
                     return message;
                });

        log.info("订单创建成功，订单号：{}", rechargeOrder.getOrderId());

        // 缓存标记,决定之后是否需要取消订单
        redisTemplate.opsForValue().set(RedisCache.RECHARGE_ORDER_IS_TIMEOUT + rechargeOrder.getOrderId(),"");

        return Response.success(rechargeOrder.getOrderId());
    }

    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {

        rechargeOrderMapper.updateOrderStatus(orderId,orderStatus,LocalDateTime.now());
    }

    @Override
    public Response queryOrder(String orderId) {

        RechargeOrder rechargeOrder = getOrderById(orderId);

        return Response.success(rechargeOrder);
    }

    @Override
    public void handleRechargeOrder(String orderId) {
        LocalDateTime payTime = LocalDateTime.now();
        rechargeOrderMapper.handleRechargeOrder
                (orderId, payTime,
                PayType.ALI_PAY.getCode(),
                Status.ORDER_SUCCESS.getCode());
    }

    @Override
    public List<OrderVo> getRechargeOrderList(String userId) {
        return rechargeOrderMapper.getRechargeOrderList(userId);
    }

    public RechargeOrder getOrderById(String orderId){
         return rechargeOrderMapper.queryOrder(orderId);
    }

    public static void main(String[] args) {

    }
    
}
