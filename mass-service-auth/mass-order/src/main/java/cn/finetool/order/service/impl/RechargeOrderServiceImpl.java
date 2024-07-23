package cn.finetool.order.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.common.Do.MessageDo;
import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqRoutingKey;
import cn.finetool.common.constant.MqTTL;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.enums.Status;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.util.CommonsUtils;
import cn.finetool.common.util.Response;
import cn.finetool.order.mapper.RechargeOrderMapper;
import cn.finetool.order.service.RechargeOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class RechargeOrderServiceImpl extends ServiceImpl<RechargeOrderMapper, RechargeOrder> implements RechargeOrderService {

    @Resource
    private RechargeOrderService rechargeOrderService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public Response createOrder(RechargeOrder rechargeOrder) {
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

        rechargeOrder.setOrderId(CommonsUtils.getWorkerID());

        rechargeOrder.setOrderStatus(Status.ORDER_WAIT.getCode());
        rechargeOrder.setCreateTime(LocalDateTime.now());
        rechargeOrder.setUserId(loginId);
        save(rechargeOrder);

        //  发送消息通知
        MessageDo messageDo = new MessageDo();
        Map<String,Object> map = new HashMap<>();
        map.put("orderId",rechargeOrder.getOrderId());
        messageDo.setMessageMap(map);

        rabbitTemplate.convertAndSend(MqExchange.ORDER_EXCHANGE,
                MqRoutingKey.ORDER_ROUTING_KEY, messageDo,
                message -> {
                     message.getMessageProperties().getHeaders().put("x-delay",10);
                     return message;
                });

        return Response.success(rechargeOrder.getOrderId());
    }

    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        rechargeOrderService.update()
                .set("order_status",orderStatus)
                .eq("order_id",orderId)
                .update();
    }
}
