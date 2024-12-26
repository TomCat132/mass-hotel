package cn.finetool.order.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.api.handler.MessageHandler;
import cn.finetool.api.service.AccountAPIService;
import cn.finetool.api.service.ActivityAPIService;
import cn.finetool.api.service.HotelAPIService;
import cn.finetool.api.service.UserAPIService;
import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqRoutingKey;
import cn.finetool.common.constant.MqTTL;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.dto.CreateOrderDto;
import cn.finetool.common.dto.OrderPayDto;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.enums.PayType;
import cn.finetool.common.enums.Status;
import cn.finetool.common.enums.SysEnum;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.OrderStatus;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.util.MqUtils;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.common.util.Strings;
import cn.finetool.common.vo.OrderVO;
import cn.finetool.order.mapper.RoomOrderMapper;
import cn.finetool.order.service.OrderService;
import cn.finetool.order.service.OrderStatusService;
import cn.finetool.order.service.RoomOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.finetool.common.util.Response.success;

@Service
public class RoomOrderServiceImpl extends ServiceImpl<RoomOrderMapper, RoomOrder> implements RoomOrderService {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(RoomOrderServiceImpl.class);
    public static final SnowflakeIdWorker ID_WORKER = new SnowflakeIdWorker(3, 0);
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
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private OrderService orderService;
    @Resource
    private HotelAPIService hotelAPIService;
    @Resource
    private MessageHandler messageHandler;
    @Resource
    private AccountAPIService accountAPIService;
    @Resource
    private ActivityAPIService activityAPIService;


    @Override
    public void createRoomOrderInfo(CreateOrderDto createOrderDto) {

        roomOrderService.save(createOrderDto.getRoomOrder());
        orderStatusService.save(createOrderDto.getOrderStatus());
        String orderId = createOrderDto.getRoomOrder().getOrderId();
        // 订单未处理标记
        redisTemplate.opsForValue().set(RedisCache.ROOM_RESERVED_ORDER_IS_TIMEOUT + orderId,"", MqTTL.FIVE_MINUTES);
    }

    @Override
    public Response accountPayRoomOrder(OrderPayDto orderPayDto) throws JsonProcessingException {
        //TODO:再次校验支付金额
//        checkPayAmount(orderPayDto);
        //参数校验，确认是账户支付
        if (!orderPayDto.getPayType().equals(PayType.ACCOUNT_PAY.getCode())){
            throw new BusinessRuntimeException(BusinessErrors.AUTHENTICATION_ERROR);
        }
        //查询账户余额
        String userId = StpUtil.getLoginIdAsString();
        BigDecimal account = userAPIService.getUserAccount(userId);
        //判断余额是否足够
        if (account.compareTo(orderPayDto.getUserPayAmount()) < 0){
            throw new BusinessRuntimeException("余额不足");
        }
        //扣除账户余额
        userAPIService.decreaseUserAccount(userId,orderPayDto.getUserPayAmount());
        //优惠券状态状态更新
        if (Objects.nonNull(orderPayDto.getVoucherId())){
            activityAPIService.usedVoucher(orderPayDto.getVoucherId(), Status.VOUCHER_USED.code());
        }
        //更新房间预定订单订单状态
        orderStatusService.changeOrderStatus(orderPayDto.getOrderId(), Status.ORDER_SUCCESS.getCode(), PayType.ACCOUNT_PAY.getCode());
        //删除缓存
        redisTemplate.delete(RedisCache.ROOM_RESERVED_ORDER_IS_TIMEOUT + orderPayDto.getOrderId());
        //发送消息到MQ,1小时后提醒用户订单未办理入住手续
        Map<String,Object> messageBody = new HashMap<>();
        messageBody.put("orderId",orderPayDto.getOrderId());
        messageBody.put("acceptId", StpUtil.getLoginIdAsString());
        //根据订单号查询订单所属的 merchantId
        String merchantId = orderService.findMerchantIdByOrderId(orderPayDto.getOrderId());
        messageBody.put("merchantId",merchantId);
        MqUtils.sendMessage(rabbitTemplate,
                MqExchange.ROOM_BOOKING_TIMEOUT_EXCHANGE,
                MqRoutingKey.ROOM_BOOKING_TIMEOUT_ROUTING_KEY,
                messageBody,
                message -> {
                    message.getMessageProperties().getHeaders().put("x-delay", MqTTL.ONE_HOUR);
                    return message;
                });
        redisTemplate.opsForValue().set(RedisCache.ROOM_BOOKING_TIMEOUT_REMIND + orderPayDto.getOrderId(),"");
        // 系统消息提醒
        String messageContent1 = "您预定的房间订单:" + orderPayDto.getOrderId() + "已成功支付";
        messageHandler.sendMessage(userId, messageContent1, orderPayDto.getOrderId());
        List<String> acceptIds = accountAPIService.findMerchantEmployee(merchantId);
        String messageContent2 = "您有新的房间订单:" + orderPayDto.getOrderId() + "，请及时处理";
        messageHandler.sendMessage(acceptIds, messageContent2, orderPayDto.getOrderId());
        return success("支付成功");
    }

    /**
     * 校验支付金额
     * @param orderPayDto
     */
    private void checkPayAmount(OrderPayDto orderPayDto) {
        String orderId = orderPayDto.getOrderId();
        //截取前4位
        String orderType = orderId.substring(0,4);
        if (Strings.equals(orderType, SysEnum.HotelOrderPrefix.code())){
            // 查询订单是否存在
            RoomOrder roomOrder = roomOrderService.getOne(new QueryWrapper<RoomOrder>()
                    .eq("order_id", orderPayDto.getOrderId()));
            if (Objects.nonNull(roomOrder)){
                // TODO: 折扣价格
                BigDecimal confirmPrice = hotelAPIService.caculatePayAmount(orderPayDto);
                if (confirmPrice.compareTo(orderPayDto.getUserPayAmount())== 0){
                    throw new BusinessRuntimeException("订单异常");
                }
            }
            throw new BusinessRuntimeException("订单不存在");
        }
        throw new BusinessRuntimeException("订单类型错误");
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
        return success(orderInfo);
    }

    public RoomOrder queryRoomOrderInfo(String orderId) {
        return roomOrderService.getOne(new LambdaQueryWrapper<RoomOrder>()
                .eq(RoomOrder::getOrderId,orderId));
    }
}
