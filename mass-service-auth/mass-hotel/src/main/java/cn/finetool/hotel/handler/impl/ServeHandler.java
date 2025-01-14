package cn.finetool.hotel.handler.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.api.service.AccountAPIService;
import cn.finetool.api.service.OrderAPIService;
import cn.finetool.api.service.OssAPIService;
import cn.finetool.common.configuration.AppContext;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.enums.Status;
import cn.finetool.common.enums.SysEnum;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.ChatMessage;
import cn.finetool.common.po.ChatRecord;
import cn.finetool.common.po.Hotel;
import cn.finetool.common.po.User;
import cn.finetool.common.po.UserRequest;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.Strings;
import cn.finetool.common.util.TimeUtil;
import cn.finetool.common.vo.ChatVO;
import cn.finetool.common.vo.RequestVO;
import cn.finetool.hotel.handler.ServeService;
import cn.finetool.hotel.mapper.ChatMessageMapper;
import cn.finetool.hotel.mapper.ChatRecordMapper;
import cn.finetool.hotel.mapper.HotelMapper;
import cn.finetool.hotel.mapper.UserRequestMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.ImmutableMap;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import static cn.finetool.common.util.Response.success;
import static cn.finetool.hotel.HotelApplication.ID_WORKER;

@Service
@DependsOn("appContext")
class ServeHandler implements ServeService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServeHandler.class);
    private final UserRequestMapper requestMapper = AppContext.getBean(UserRequestMapper.class);
    private final ChatRecordMapper chatRecordMapper = AppContext.getBean(ChatRecordMapper.class);
    private final ChatMessageMapper chatMessageMapper = AppContext.getBean(ChatMessageMapper.class);
    private final HotelMapper hotelMapper = AppContext.getBean(HotelMapper.class);
    private final RedissonClient redissonClient = AppContext.getBean(RedissonClient.class);
    private final RabbitTemplate rabbitTemplate = AppContext.getBean(RabbitTemplate.class);
    @Resource
    private AccountAPIService accountAPIService;
    @Resource
    private OrderAPIService orderAPIService;
    @Resource
    private OssAPIService ossAPIService;


    @Override
    public Response createUserRequest(UserRequest userRequest) {
        String userId = StpUtil.getLoginIdAsString();

        String relationId = userRequest.getRelationId();
        //截取前4位
        String prefix = relationId.substring(0, 4);
        // 关联ID为酒店房间订单号 room_order_id
        if (Strings.equals(SysEnum.ROOM_ORDER_PREFIX.code(), prefix)) {
            userRequest.setMerchantId(orderAPIService.findMerchantIdByOrderId(relationId));
            //如果存在未处理的请求，则不允许再次提交
            UserRequest request = requestMapper.selectOne(new QueryWrapper<UserRequest>()
                    .eq("user_id", userId)
                    .eq("status", Status.REQUEST_NOT.code())
                    .eq("merchant_id", userRequest.getMerchantId()));
            if (Objects.nonNull(request)) {
                throw new BusinessRuntimeException("请求已呼叫成功,我们会尽快回复您~");
            }
        }

        userRequest.setRequestId(SysEnum.USER_REQUEST_PREFIX.code() + ID_WORKER.nextId());
        userRequest.setRequestTime(TimeUtil.now());
        userRequest.setUserId(userId);
        requestMapper.insert(userRequest);
        //TODO:消息通知
        return success("已呼叫, 工作人员会尽快回复您~请耐心等待");
    }

    @Override
    public Response startHandleRequest(String requestId) {
        // 竞争执行，但是只能有一个人进行处理
        RLock handleLock = redissonClient.getLock(RedisCache.REQUEST_LOCK + requestId);
        try {
            boolean isLocked = handleLock.tryLock(10, 10000, TimeUnit.MILLISECONDS);
            if (isLocked) {
                String conductorId = StpUtil.getLoginIdAsString();
                //处理用户请求
                requestMapper.update(new UpdateWrapper<UserRequest>()
                        .set("status", Status.REQUEST_DOING.code())
                        .set("conductor_id", conductorId)
                        .eq("request_id", requestId));
                //TODO:生成聊天服务编号
                ChatRecord chatRecord = new ChatRecord();
                chatRecord.setChatId(SysEnum.CHAT_RECORD_PREFIX.code() + ID_WORKER.nextId());
                chatRecord.setCreateTime(TimeUtil.now());
                chatRecord.setRequestId(requestId);
                chatRecordMapper.insert(chatRecord);
                //TODO:消息通知,创建聊天室
                notifyUsers(chatRecord.getChatId(), conductorId, requestId);
                return success(ImmutableMap.of("chatId", chatRecord.getChatId()));
            } else {
                handleLock.unlock();
                throw new BusinessRuntimeException("该请求正在处理中");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 避免死锁
            handleLock.unlock();
        }
    }

    /**
     * 通知用户和工作人员 聊天室创建成功
     *
     * @param chatId
     * @param conductorId
     * @param requestId
     */
    private void notifyUsers(String chatId, String conductorId, String requestId) {
        UserRequest userRequest = requestMapper.selectById(requestId);
        String customerId = userRequest.getUserId();
        accountAPIService.noticeUser(chatId, requestId, customerId, conductorId);
    }

    @Override
    public Response getNotHandleRequestList(String merchantId) {
        List<UserRequest> userRequests = requestMapper.selectList(new QueryWrapper<UserRequest>()
                .eq("merchant_id", merchantId)
                .eq("status", Status.REQUEST_NOT.code()));
        if (CollectionUtils.isNotEmpty(userRequests)) {

            List<RequestVO> requestVOList = userRequests.stream()
                    .map(userRequest -> {
                        RequestVO requestVO = new RequestVO(userRequest);
                        User usernameByUserId = accountAPIService.findUserInfoByUserId(userRequest.getUserId());
                        requestVO.setUsername(usernameByUserId.getUsername());
                        return requestVO;
                    })
                    .sorted(Comparator.comparing(RequestVO::getRequestTime).reversed())
                    .toList();
            return success(requestVOList);
        }
        return success(Collections.emptyList());
    }

    @Override
    public Response getRequestChatList(String userId) {
        // 获取所有请求的聊天列表
        List<UserRequest> userRequestList = requestMapper.selectList(new QueryWrapper<UserRequest>()
                .eq("user_id", userId));
        List<ChatVO> chatVOList = userRequestList.stream()
                .map(userRequest -> {
                    ChatVO chatVO = new ChatVO();
                    // 查询呼叫请求所属商户信息
                    Hotel hotelInfo = hotelMapper.selectOne(new QueryWrapper<Hotel>()
                            .eq("merchant_id", userRequest.getMerchantId()));
                    chatVO.setHotelName(hotelInfo.getHotelName());
                    // 查询处理人信息
                    User userInfo = accountAPIService.findUserInfoByUserId(userRequest.getConductorId());
                    chatVO.setUserInfo(userInfo);
                    ChatRecord chatRecord = chatRecordMapper.selectOne(new QueryWrapper<ChatRecord>()
                            .eq("request_id", userRequest.getRequestId()));
                    //查询最新的一条消息，查询未读消息
                    ChatMessage chatMessage = chatMessageMapper.findNewMessage(chatRecord.getChatId());
                    if (Objects.nonNull(chatMessage)) {
                        chatVO.setNewMessage(chatMessage.getMessage());
                        chatVO.setNewMessageTime(chatMessage.getSenderTime());
                    }
                    //查询未读消息数量
                    int size = chatMessageMapper.selectList(new QueryWrapper<ChatMessage>()
                                    .eq("receiver_id", userId)
                                    .eq("is_read", Status.MESSAGE_UNREAD.code())
                                    .orderByDesc("sender_time")
                                    .last("LIMIT 99"))
                            .size();
                    chatVO.setNewMessageUnreadCount(size);
                    return chatVO;
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(chatVOList)) {
            return success(chatVOList);
        }
        return success(Collections.emptyList());
    }

    @Override
    public Response getGuestChatList(String conductorId) {
        List<UserRequest> userRequestList = requestMapper.selectList(new QueryWrapper<UserRequest>()
                .eq("conductor_id", conductorId));
        List<ChatVO> chatVOList = userRequestList.stream()
                .map(userRequest -> {
                    ChatVO chatVO = new ChatVO();
                    //查询客户信息
                    User userInfo = accountAPIService.findUserInfoByUserId(userRequest.getUserId());
                    chatVO.setUserInfo(userInfo);
                    ChatRecord chatRecord = chatRecordMapper.selectOne(new QueryWrapper<ChatRecord>()
                            .eq("request_id", userRequest.getRequestId()));
                    chatVO.setChatId(chatRecord.getChatId());
                    //查询最新的一条消息，查询未读消息
                    ChatMessage chatMessage = chatMessageMapper.findNewMessage(chatRecord.getChatId());
                    if (Objects.nonNull(chatMessage)) {
                        chatVO.setNewMessage(chatMessage.getMessage());
                        chatVO.setNewMessageTime(chatMessage.getSenderTime());
                    }
                    //查询未读消息数量
                    int size = chatMessageMapper.selectList(new QueryWrapper<ChatMessage>()
                                    .eq("receiver_id", conductorId)
                                    .eq("read_state", Status.MESSAGE_UNREAD.code())
                                    .orderByDesc("sender_time")
                                    .last("LIMIT 99"))
                            .size();
                    chatVO.setNewMessageUnreadCount(size);

                    return chatVO;
                })
                .sorted(Comparator.comparing(ChatVO::getNewMessageTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(chatVOList)) {
            return success(chatVOList);
        }
        return success(Collections.emptyList());
    }
    
    @Override
    public Response chatMessageList(String chatId, String userId) {
        List<ChatMessage> messageList = chatMessageMapper.selectList(new QueryWrapper<ChatMessage>()
                .eq("chat_id", chatId)
                .orderByAsc("sender_time"));
        chatMessageMapper.update(new UpdateWrapper<ChatMessage>()
                .eq("receiver_id", userId)
                .set("read_state", Status.MESSAGE_READ.code()));
        if (CollectionUtils.isNotEmpty(messageList)){
            return success(messageList);
        }
        return success(Collections.emptyList());
    }

}
