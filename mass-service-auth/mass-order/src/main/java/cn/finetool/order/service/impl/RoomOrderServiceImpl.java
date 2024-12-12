package cn.finetool.order.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.api.service.HotelAPIService;
import cn.finetool.api.service.UserAPIService;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.dto.CreateOrderDto;
import cn.finetool.common.dto.OrderPayDto;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.enums.PayType;
import cn.finetool.common.enums.Status;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.OrderStatus;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.common.vo.OrderVO;
import cn.finetool.order.mapper.RoomOrderMapper;
import cn.finetool.order.service.OrderStatusService;
import cn.finetool.order.service.RoomOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoomOrderServiceImpl extends ServiceImpl<RoomOrderMapper, RoomOrder> implements RoomOrderService {
    
    

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private OrderStatusService orderStatusService;

    @Resource
    private RoomOrderService roomOrderService;

    @Resource
    private UserAPIService userAPIService;

    @Resource
    private RoomOrderMapper roomOrderMapper;

    SnowflakeIdWorker ID_WORKER = new SnowflakeIdWorker(3, 0);


    @Override
    public void createRoomOrderInfo(CreateOrderDto createOrderDto) {

        roomOrderService.save(createOrderDto.getRoomOrder());
        orderStatusService.save(createOrderDto.getOrderStatus());
        String orderId = createOrderDto.getRoomOrder().getOrderId();
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
            return Response.error("余额不足");
        }
        //扣除账户余额
        userAPIService.decreaseUserAccount(userId,orderPayDto.getUserPayAmount());

        //更新订单状态
        orderStatusService.changeOrderStatus(orderPayDto.getOrderId(), Status.ORDER_SUCCESS.getCode(), PayType.ACCOUNT_PAY.getCode());

        //删除缓存
        redisTemplate.delete(RedisCache.ROOM_RESERVED_ORDER_IS_TIMEOUT + orderPayDto.getOrderId());
        return Response.success("支付成功");
    }

    @Override
    public List<OrderVO> getRoomOrderList(String userId) {
        return roomOrderMapper.getRoomOrderList(userId);
    }

    @Override
    public Response queryOrder(String orderId) {

        RoomOrder roomOrder = roomOrderService.getOne(new LambdaQueryWrapper<RoomOrder>()
                .eq(RoomOrder::getOrderId, orderId));
        OrderStatus orderStatus = orderStatusService.getOne(new LambdaQueryWrapper<OrderStatus>()
                .eq(OrderStatus::getOrderId,orderId));
        Map<String,Object> orderInfo = new HashMap<>();
        orderInfo.put("roomOrder",roomOrder);
        orderInfo.put("orderStatus",orderStatus);
        return Response.success(orderInfo);
    }

    public RoomOrder queryRoomOrderInfo(String orderId) {
        return roomOrderService.getOne(new LambdaQueryWrapper<RoomOrder>()
                .eq(RoomOrder::getOrderId,orderId));
    }
}
