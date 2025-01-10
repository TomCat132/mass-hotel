package cn.finetool.hotel.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.api.service.AccountAPIService;
import cn.finetool.api.service.OrderAPIService;
import cn.finetool.common.constant.MqExchange;
import cn.finetool.common.constant.MqRoutingKey;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.enums.Status;
import cn.finetool.common.enums.SysEnum;
import cn.finetool.common.po.Evaluation;
import cn.finetool.common.po.Hotel;
import cn.finetool.common.po.Room;
import cn.finetool.common.po.RoomBooking;
import cn.finetool.common.po.RoomDate;
import cn.finetool.common.po.RoomInfo;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.threadpool.DynamicThreadPool;
import cn.finetool.common.util.MqUtils;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.common.util.TimeUtil;
import cn.finetool.common.vo.CheckRoomInfoVO;
import cn.finetool.hotel.handler.impl.HotelAdminHandler;
import cn.finetool.hotel.mapper.HotelMapper;
import cn.finetool.hotel.mapper.RoomBookingMapper;
import cn.finetool.hotel.mapper.RoomDateMapper;
import cn.finetool.hotel.mapper.RoomInfoMapper;
import cn.finetool.hotel.mapper.RoomMapper;
import cn.finetool.hotel.service.RoomBookingService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static cn.finetool.hotel.HotelApplication.ID_WORKER;

@Service
public class RoomBookingServiceImpl extends ServiceImpl<RoomBookingMapper, RoomBooking> implements RoomBookingService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomBookingServiceImpl.class);
    @Resource
    private DynamicThreadPool dynamicThreadPool;
    @Resource
    private RoomBookingMapper roomBookingMapper;
    @Resource
    private RoomInfoMapper roomInfoMapper;
    @Resource
    private RoomMapper roomMapper;
    @Resource
    private RoomDateMapper roomDateMapper;
    @Resource
    private HotelMapper hotelMapper;
    @Resource
    private HotelAdminHandler hotelAdminHandler;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private AccountAPIService accountAPIService;
    @Resource
    private OrderAPIService orderAPIService;

    @Override
    public Response queryRoomBooking(Integer queryType, String queryValue) {
        // 通过userId 获取缓存的 hotelId
        String userId = StpUtil.getLoginIdAsString();
        String hotelId = (String) redisTemplate.opsForValue().get(RedisCache.USER_MERCHANT_BINDING + userId);
        List<RoomBooking> roomBookingList = new ArrayList<>();
        // 查询时绑定hotelId,避免越权
        roomBookingList = roomBookingMapper.queryRoomBookingList(queryType, queryValue, hotelId);
        if (roomBookingList.isEmpty()) {
            return Response.error("未查询到订单信息");
        }
        return Response.success(roomBookingList);
    }

    @Override
    public Response startHandleCheckIn(Integer id) {
       
        RoomBooking roomBooking = roomBookingMapper.selectOne(new QueryWrapper<RoomBooking>()
                .eq("id", id));
        if (Objects.isNull(roomBooking)){

        }
        // 删除 超时提醒标记
        redisTemplate.delete(RedisCache.ROOM_BOOKING_TIMEOUT_REMIND + roomBooking.getOrderId());
        // 更新 status : 已预定 -》 办理中
        roomBookingMapper.changeStatus(id, Status.ROOMBOOKING_DOING.getCode());


        // TODO: 开始办理入住
        return Response.success("开始办理入住");
    }

    @Override
    public Response checkRoomDateInfo(Integer id) {
        // 查数据
        RoomBooking roomBooking = roomBookingMapper.selectById(id);
        RoomDate roomDate = roomDateMapper.selectById(roomBooking.getRoomDateId());
        RoomInfo roomInfo = roomInfoMapper.selectById(roomDate.getRiId());
        Room room = roomMapper.selectOne(new QueryWrapper<Room>()
                .eq("room_id", roomInfo.getRoomId()));
        CheckRoomInfoVO checkRoomInfoVO = new CheckRoomInfoVO();
        checkRoomInfoVO.setRoomBooking(roomBooking);
        checkRoomInfoVO.setRoom(room);
        checkRoomInfoVO.setRoomInfo(roomInfo);
        checkRoomInfoVO.setStatus(roomInfo.getStatus());
        return Response.success(checkRoomInfoVO);
    }

    @Override
    public Response receiveDeposit(String id) {
        roomBookingMapper.update(new UpdateWrapper<RoomBooking>()
                .eq("id", id)
                .set("security_deposit", true));
        return Response.success("已确认缴纳押金");
    }

    @Override
    public Response finishHandleCheckIn(Integer id, Integer type, String doorKey) throws JsonProcessingException {
        // 有一步手续未完成禁止确认入住
        LocalDateTime nowTime = TimeUtil.now();
        RoomBooking roomBooking = roomBookingMapper.selectById(id);
        // 检查是否缴纳押金
        if (!roomBooking.isSecurityDeposit()) {
            return Response.error("请先确认用户缴纳押金");
        }
        // 检查是否绑定门禁卡
        if (type == Status.ROOM_DOOR_TYPE_CARD.getCode()) {
            if (Strings.isBlank(roomBooking.getDoorKey())) {
                return Response.error("请先绑定门禁卡");
            }
        } else if (type == Status.ROOM_DOOR_TYPE_PASSWORD.getCode()) {
            if (Strings.isBlank(doorKey)) {
                return Response.error("请生成随机密码");
            }
            roomBookingMapper.update(new UpdateWrapper<RoomBooking>()
                    .set("door_key", doorKey)
                    .set("check_in_date", nowTime)
                    .eq("id", id));
        }
        // 更新预定房间状态和入住时间
        roomBookingMapper.update(new UpdateWrapper<RoomBooking>()
                .set("check_in_date", TimeUtil.now())
                .set("status", Status.ROOMBOOKING_CHECK_IN.code())
                .eq("id", id));
        // 异步线程池更新入住总次数
        dynamicThreadPool.submitTask(() -> {
            //TODO: 异步任务
            try {
                // 发送消息到MQ,根据离店时间前1小时提醒用户（离店或者延长入住时间）
                Map<String, Object> messageBody = new HashMap<>();
                String workerId = StpUtil.getLoginIdAsString();
                String merchantId = accountAPIService.findMerchantIdByUserId(workerId);
                messageBody.put("merchantId", merchantId);
                messageBody.put("workerId", workerId);
                messageBody.put("roomBooking", roomBooking.toString());
                // 计算时间
                RoomOrder roomOrder = orderAPIService.queryOrderInfo(roomBooking.getOrderId());
                // 离店日期+11点整
                LocalDateTime outTime = roomOrder.getCheckOutDate().atStartOfDay().plusHours(11);
                long delayUpTime = Duration.between(nowTime, outTime).toMillis();
                MqUtils.sendMessage(rabbitTemplate,
                        MqExchange.ROOM_ORDER_ENDING_REMIND_EXCHANGE,
                        MqRoutingKey.ROOM_ORDER_ENDING_REMIND_ROUTING_KEY,
                        messageBody,
                        message -> {
                            message.getMessageProperties().getHeaders().put("x-delay", delayUpTime);
                            return message;
                        });
                LOGGER.info("完成入住办理, 离店消息提醒倒计时: {}ms", delayUpTime);
                
                // 更新入住总次数
                hotelMapper.update(new UpdateWrapper<Hotel>()
                        .setSql("live_count = live_count + 1")
                        .eq("merchant_id", merchantId));
                        
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        // TODO: 通知用户可以进行入住了
        return Response.success("已完成入住办理");
    }

    @Override
    public Response bindingDoorKey(Integer id, String doorKey) {

        roomBookingMapper.update(new UpdateWrapper<RoomBooking>()
                .set("door_key", doorKey)
                .eq("id", id));
        return Response.success("门禁卡绑定成功");
    }

    @Override
    public Response unBindingDoorKey(Integer id) {

        roomBookingMapper.update(new UpdateWrapper<RoomBooking>()
                .set("door_key", "")
                .eq("id", id));

        return Response.success("已解除门禁卡绑定");
    }

    @Override
    public Response endCheckInRoomOrder(Integer id) {
        RoomBooking roomBooking = roomBookingMapper.selectById(id);
        // status : 退房待确认 -》 已退房
        roomBookingMapper.changeStatus(id, Status.ROOMBOOKING_CHECK_OUT.code());
        dynamicThreadPool.submitTask(()->{
            // 生成待评价数据
            LOGGER.info("开始生成待评价数据");
            Evaluation evaluation = new Evaluation();
            evaluation.setEvaluationId(SysEnum.EVALUATION_PREFIX.code() + ID_WORKER.nextId());
            evaluation.setOrderId(roomBooking.getOrderId());
            evaluation.setMerchantId(hotelAdminHandler.findMerchantIdByOrderId(roomBooking.getOrderId()));
            evaluation.setUserId(orderAPIService.findUserIdByOrderId(roomBooking.getOrderId()));
            // 查询房间ID:roomInfo_id 
            RoomDate roomDate = roomDateMapper.selectOne(new QueryWrapper<RoomDate>()
                    .eq("id", roomBooking.getRoomDateId()));
            RoomInfo roomInfo = roomInfoMapper.selectById(roomDate.getRiId());
            if (Objects.nonNull(roomInfo)){
                evaluation.setRelationId(roomInfo.getRoomId());
            }
            accountAPIService.saveEvaluation(evaluation);
        });
        return Response.success("已完成退房确认，请通知相关人员清洁房间");
    }

}
