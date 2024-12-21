package cn.finetool.hotel.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.api.service.OrderAPIService;
import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqRoutingKey;
import cn.finetool.common.constant.MqTTL;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.dto.CreateOrderDto;
import cn.finetool.common.dto.RoomBookingDto;
import cn.finetool.common.enums.CodeSign;
import cn.finetool.common.enums.Status;
import cn.finetool.common.po.*;
import cn.finetool.common.util.MqUtils;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.hotel.mapper.RoomInfoMapper;
import cn.finetool.hotel.service.RoomBookingService;
import cn.finetool.hotel.service.RoomDateService;
import cn.finetool.hotel.service.RoomInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo> implements RoomInfoService {

    private static final SnowflakeIdWorker ID_WORKER = new SnowflakeIdWorker(7, 0);

    private static final Logger log = Logger.getLogger(RoomInfoServiceImpl.class.getName());

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RoomInfoMapper roomInfoMapper;

    @Resource
    private RoomDateService roomDateService;

    @Resource
    private RoomBookingService roomBookingService;

    @Resource
    private OrderAPIService orderAPIService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public Response addRoomInfo(RoomInfo roomInfo) {
        // TODO: 简单的添加功能，尚未考虑细节
        save(roomInfo);
        return Response.success("添加成功");
    }

    @Override
    public Response reserveRoom(RoomBookingDto roomBookingDto) {
        LocalDate checkOutDate = roomBookingDto.getCheckOutDate().minusDays(1);
        //1. 获取可用房间数量
        Integer canUseRoomCount = roomInfoMapper.queryCanUseRoomCount(roomBookingDto.getRoomId(),
                roomBookingDto.getCheckInDate(),
                checkOutDate);
        //2. 锁定房间（分布式锁）
        RLock lock = redissonClient.getLock(RedisCache.RESERVE_ROOM + roomBookingDto.getRoomId());
        try {
            if (lock.tryLock(2000, TimeUnit.MILLISECONDS)) {
                Response.error(400, "房间已被预订，请选择其他房间~");
            }
            if (canUseRoomCount > 0) {
                // 可用房间列表
                Integer canUserRoomDateId = roomInfoMapper.selectList(new LambdaQueryWrapper<RoomInfo>()
                                .eq(RoomInfo::getRoomId, roomBookingDto.getRoomId())
                                .eq(RoomInfo::getStatus, 1))
                        .stream()
                        .map(roomInfo -> {
                            // 找到第一个 room_date_id 主键
                            List<RoomDate> list = roomDateService.list(new LambdaQueryWrapper<RoomDate>()
                                    .eq(RoomDate::getRiId, roomInfo.getId())
                                    .eq(RoomDate::getStatus, Status.ROOM_DATE_CAN_USE.getCode())
                                    .and(filter -> filter
                                            .ge(RoomDate::getDate, roomBookingDto.getCheckInDate())
                                            .le(RoomDate::getDate, checkOutDate)
                                    ));

                            return list.stream()
                                    .map(RoomDate::getId)
                                    .findFirst();
                        })
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                        .orElse(null);


                // 随机锁定一个房间
                roomDateService.update()
                        .set("status", Status.ROOM_DATE_RESERVED.getCode())
                        .eq("id", canUserRoomDateId)
                        .update();
                // 生成订单号
                String roomOrderId = CodeSign.HotelOrderPrefix.getCode() + String.valueOf(+ID_WORKER.nextId());
                String userId = StpUtil.getLoginIdAsString();
                // 保存预定房间信息 （tb_room_booking）
                RoomBooking roomBooking = new RoomBooking();
                roomBooking.setRoomDateId(canUserRoomDateId);
                roomBooking.setOrderId(roomOrderId);
                roomBooking.setCreatedTime(LocalDateTime.now());
                roomBooking.setStatus(Status.ROOMBOOKING_RESERVED.getCode());
                roomBookingService.save(roomBooking);

                // 保存订单信息 （tb_room_order , tb_order_status）
                RoomOrder roomOrder = new RoomOrder();
                roomOrder.setOrderId(roomOrderId);
                roomOrder.setRoomId(roomBookingDto.getRoomId());
                roomOrder.setRoomDateId(canUserRoomDateId);
                roomOrder.setUserPayAmount(roomBookingDto.getUserPayAmount());
                roomOrder.setCreateTime(LocalDateTime.now());
                roomOrder.setCheckInDate(roomBookingDto.getCheckInDate());
                roomOrder.setCheckOutDate(roomBookingDto.getCheckOutDate());
                roomOrder.setUserId(userId);

                // 冷热数据分离
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setOrderId(roomOrderId);
                orderStatus.setOrderStatus(Status.ORDER_WAIT.getCode());
                orderStatus.setUserId(userId);

                CreateOrderDto orderDto = new CreateOrderDto();
                orderDto.setRoomOrder(roomOrder);
                orderDto.setOrderStatus(orderStatus);
                orderAPIService.createRoomOrder(orderDto);

                // 发送过期消息
                Map<String, Object> messageBody = new HashMap<>();
                messageBody.put("orderId", roomOrderId);
                messageBody.put("roomDateId", canUserRoomDateId);
                messageBody.put("checkInDate", roomOrder.getCheckInDate());
                messageBody.put("checkOutDate", roomOrder.getCheckInDate());
                // 发送消息实现订单 防止超时取消
                MqUtils.sendMessage(rabbitTemplate, 
                        MqExchange.ROOM_DATE_RESERVE_ORDER_EXCHANGE,
                        MqRoutingKey.ORDER_ROUTING_KEY,
                        messageBody, 
                        message -> {
                            message.getMessageProperties().getHeaders().put("x-delay", MqTTL.FIVE_MINUTES);
                            return message;
                        });
                log.info("预定房间成功，订单号:" + roomOrderId);
                return Response.success(roomOrderId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        throw new RuntimeException("预定房间失败，房间已被预订，请选择其他房间~");
    }


}
