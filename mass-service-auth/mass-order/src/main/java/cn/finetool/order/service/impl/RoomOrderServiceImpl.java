package cn.finetool.order.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.api.service.HotelAPIService;
import cn.finetool.api.service.UserAPIService;
import cn.finetool.common.Do.MessageDo;
import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqRoutingKey;
import cn.finetool.common.constant.MqTTL;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.dto.OrderPayDto;
import cn.finetool.common.dto.RoomBookingDto;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.enums.OrderType;
import cn.finetool.common.enums.PayType;
import cn.finetool.common.enums.Status;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.OrderStatus;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.order.mapper.RoomOrderMapper;
import cn.finetool.order.service.OrderStatusService;
import cn.finetool.order.service.RoomOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.bouncycastle.util.Strings;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class RoomOrderServiceImpl extends ServiceImpl<RoomOrderMapper, RoomOrder> implements RoomOrderService {

    @Resource
    HotelAPIService hotelAPIService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private OrderStatusService orderStatusService;

    @Resource
    private RoomOrderService roomOrderService;

    @Resource
    private UserAPIService userAPIService;

    SnowflakeIdWorker ID_WORKER = new SnowflakeIdWorker(3, 0);


    @Override
    public void createRoomOrderInfo(RoomOrder roomOrder, OrderStatus orderStatus) {
        save(roomOrder);
        orderStatusService.save(orderStatus);
        String orderId = roomOrder.getOrderId();
        // 订单未处理标记
        redisTemplate.opsForValue().set(RedisCache.ROOM_RESERVED_ORDER_IS_TIMEOUT + orderId,"");


    }

    @Override
    public Response accountPayRoomOrder(OrderPayDto orderPayDto) {
        //参数校验，确认是账户支付
        if (!orderPayDto.getPayType().equals(PayType.ACCOUNT_PAY.getCode())){
            throw new BusinessRuntimeException(BusinessErrors.AUTHENTICATION_ERROR);
        }
        //查询账户余额
        String userId = StpUtil.getLoginIdAsString();
        BigDecimal account = userAPIService.getUserAccount(userId);
        //判断余额是否足够
        if (account.compareTo(orderPayDto.getUserPayAmount()) < 0){
            throw new BusinessRuntimeException(BusinessErrors.ACCOUNT_NOT_ENOUGH);
        }
        //扣除账户余额
        userAPIService.decreaseUserAccount(userId,orderPayDto.getUserPayAmount());

        //更新订单状态
        orderStatusService.changeOrderStatus(orderPayDto.getOrderId(), Status.ORDER_SUCCESS.getCode(), PayType.ACCOUNT_PAY.getCode());

        //删除缓存
        redisTemplate.delete(RedisCache.ROOM_RESERVED_ORDER_IS_TIMEOUT + orderPayDto.getOrderId());
        return Response.success("支付成功");
    }

    public RoomOrder queryRoomOrderInfo(String orderId) {
        return roomOrderService.getOne(new LambdaQueryWrapper<RoomOrder>()
                .eq(RoomOrder::getOrderId,orderId));
    }
}
