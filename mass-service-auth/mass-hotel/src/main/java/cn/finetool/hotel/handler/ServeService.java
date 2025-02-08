package cn.finetool.hotel.handler;

import cn.finetool.common.po.UserRequest;
import cn.finetool.common.util.Response;
import org.springframework.stereotype.Component;

@Component
public interface ServeService {
    
    /** ======== 用户请求服务 ======== */
    Response createUserRequest(UserRequest userRequest);
    /** ======== 处理请求服务 ======== */
    Response startHandleRequest(String requestId);
    /** ======== 获取未处理的服务请求列表======== */
    Response getNotHandleRequestList(String merchantId);
    /** ======== 客户端: 首页获取用户请求聊天列表 ======== */
    Response getRequestChatList(String userId);
    /** ======== 聊天室:获取客户聊天列表======== */
    Response getGuestChatList(String conductorId);
    /** ======== 聊天室:获取历史聊天记录(单个聊天室)======== */
    Response chatMessageList(String chatId, String userId);
    /** ======== 聊天室: 结束用户请求聊天服务 ======== */
    Response endChatWithUserRequest(String chatId);
    /** ======== 管理员: 获取所有用户请求列表 ======== */
    Response getAllUserRequestList(String merchantId, Integer status, String keyword);
}
