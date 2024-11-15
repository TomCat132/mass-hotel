package cn.finetool.order.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.api.service.HotelAPIService;
import cn.finetool.common.Do.MessageDo;
import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqRoutingKey;
import cn.finetool.common.constant.MqTTL;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.dto.RoomBookingDto;
import cn.finetool.common.enums.BusinessErrors;
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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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

    SnowflakeIdWorker ID_WORKER = new SnowflakeIdWorker(3, 0);


    @Override
    public Response createRoomOrder(RoomBookingDto roomBookingDto) {

        LocalDate checkOutDate = roomBookingDto.getCheckOutDate().minusDays(1);

        List<Integer> roomDateIdList =  hotelAPIService.queryResidualRoomInfo(roomBookingDto.getRoomId(),
                roomBookingDto.getCheckInDate(),checkOutDate);
        if (roomDateIdList.isEmpty()){
            throw new BusinessRuntimeException(BusinessErrors.RUNTIME_ERROR,"房间已售罄");
        }
        RLock roomLock = redissonClient.getLock(RedisCache.ROOM_ORDER_LOCK + roomBookingDto.getRoomId());

        try {
            boolean isGetLock = roomLock.tryLock(2000,1000, TimeUnit.MICROSECONDS);
            if (isGetLock){
                // 获取锁成功
                String orderId = String.valueOf(ID_WORKER.nextId());
                String userId = StpUtil.getLoginIdAsString();

                // 冷热数据分离: 订单状态表信息保存
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setUserId(userId);
                orderStatus.setOrderId(orderId);
                orderStatusService.save(orderStatus);

                // 随机轮询一个房间
                Integer roomDateId = roomDateIdList.stream()
                        .skip(new Random().nextInt(roomDateIdList.size()))
                        .findFirst().get();

                MessageDo messageDo = new MessageDo();
                Map<String,Object> messageMap = new HashMap<>();
                messageMap.put("orderId",orderId);
                messageMap.put("roomDateId",roomDateId);
                messageMap.put("checkInDate",roomBookingDto.getCheckInDate());
                messageMap.put("checkOutDate",checkOutDate);
                messageDo.setMessageMap(messageMap);

                // 创建订单
                RoomOrder roomOrder = new RoomOrder();
                roomOrder.setOrderId(orderId);
                roomOrder.setRoomId(roomOrder.getRoomId());
                roomOrder.setCheckInDate(roomBookingDto.getCheckInDate());
                roomOrder.setCheckOutDate(roomBookingDto.getCheckOutDate());
                roomOrder.setRoomDateId(roomDateId);
                roomOrder.setUserPayAmount(roomBookingDto.getUserPayAmount());
                roomOrder.setCreateTime(LocalDateTime.now());
                save(roomOrder);

                // 发送消息实现订单 防止超时取消
                rabbitTemplate.convertAndSend(MqExchange.ROOM_DATE_RESERVE_ORDER_EXCHANGE,
                        MqRoutingKey.ORDER_ROUTING_KEY,messageDo, message -> {
                            message.getMessageProperties().getHeaders().put("x-delay", MqTTL.FIVE_MINUTES);
                            return message;
                        });

                // 修改房间状态
                hotelAPIService.updateRoomDateStatus(roomDateId,roomBookingDto.getCheckInDate(),
                        checkOutDate, Status.ROOM_DATE_RESERVED.getCode());

                // 订单未处理标记
                redisTemplate.opsForValue().set(RedisCache.ROOM_RESERVED_ORDER_IS_TIMEOUT + orderId,"");

                return Response.success(orderId);
            } else{
                // 获取锁失败
                throw new BusinessRuntimeException(BusinessErrors.RUNTIME_ERROR,"系统繁忙，请稍后重试");
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            roomLock.unlock();
        }
    }

    public RoomOrder queryRoomOrderInfo(String orderId) {
        return roomOrderService.getOne(new LambdaQueryWrapper<RoomOrder>()
                .eq(RoomOrder::getOrderId,orderId));
    }
}
