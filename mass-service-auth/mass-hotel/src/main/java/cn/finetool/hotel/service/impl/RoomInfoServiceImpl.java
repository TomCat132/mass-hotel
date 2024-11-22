package cn.finetool.hotel.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.api.service.OrderAPIService;
import cn.finetool.common.Do.MessageDo;
import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqRoutingKey;
import cn.finetool.common.constant.MqTTL;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.dto.RoomBookingDto;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.enums.CodeSign;
import cn.finetool.common.enums.OrderType;
import cn.finetool.common.enums.Status;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.*;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.hotel.mapper.RoomInfoMapper;
import cn.finetool.hotel.service.RoomBookingService;
import cn.finetool.hotel.service.RoomDateService;
import cn.finetool.hotel.service.RoomInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo> implements RoomInfoService {

    private static final SnowflakeIdWorker ID_WORKER = new SnowflakeIdWorker(7,0);

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
            if (lock.tryLock(2000, 2000, TimeUnit.MILLISECONDS)) {
                Response.error(400, "房间已被预订，请选择其他房间~");
            }
            if (canUseRoomCount > 0) {
                // 可用房间列表
                Integer canUseRoomInfoId = roomInfoMapper.selectList(new LambdaQueryWrapper<RoomInfo>()
                        .eq(RoomInfo::getRoomId, roomBookingDto.getRoomId())
                        .eq(RoomInfo::getStatus, 1))
                        .stream()
                        .map( roomInfo -> {
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
                        .eq("ri_id",canUseRoomInfoId)
                        .update();
                // 生成订单号
                String roomOrderId = String.valueOf(CodeSign.HotelOrderPrefix.getCode() + ID_WORKER.nextId());
                String userId = StpUtil.getLoginIdAsString();
                // 保存预定房间信息 （tb_room_booking）
                RoomBooking roomBooking = new RoomBooking();
                Integer roomDateId = roomDateService.getOne(new LambdaQueryWrapper<RoomDate>()
                                .eq(RoomDate::getRiId, canUseRoomInfoId))
                                .getId();
                roomBooking.setRoomDateId(roomDateId);
                roomBooking.setOrderId(roomOrderId);
                roomBooking.setCreatedTime(LocalDateTime.now());
                roomBookingService.save(roomBooking);

                // 保存订单信息 （tb_room_order , tb_order_status）
                RoomOrder roomOrder = new RoomOrder();
                roomOrder.setOrderId(roomOrderId);
                roomOrder.setRoomId(roomBookingDto.getRoomId());
                roomOrder.setRoomDateId(roomDateId);
                roomOrder.setUserPayAmount(roomBookingDto.getUserPayAmount());
                roomOrder.setCreateTime(LocalDateTime.now());
                roomOrder.setUserId(userId);

                // 冷热数据分离
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setOrderId(roomOrderId);
                orderStatus.setOrderStatus(Status.ORDER_WAIT.getCode());

                orderAPIService.createRoomOrder(roomOrder,orderStatus);

                // 发送过期消息
                MessageDo messageDo = new MessageDo();
                Map<String,Object> messageMap = new HashMap<>();
                messageMap.put("orderId",roomOrderId);
                messageMap.put("roomDateId",roomDateId);
                messageMap.put("checkInDate",roomOrder.getCheckInDate());
                messageMap.put("checkOutDate",roomOrder.getCheckInDate());
                messageDo.setMessageMap(messageMap);

                // 发送消息实现订单 防止超时取消
                rabbitTemplate.convertAndSend(MqExchange.ROOM_DATE_RESERVE_ORDER_EXCHANGE,
                        MqRoutingKey.ORDER_ROUTING_KEY,messageDo, message -> {
                            message.getMessageProperties().getHeaders().put("x-delay", MqTTL.FIVE_MINUTES);
                            return message;
                        });

                return Response.success(roomOrderId);
            }
        } catch (Exception e) {
            lock.unlock();
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

        return Response.error(400, "房间已被预订，请选择其他房间~");
    }




}
