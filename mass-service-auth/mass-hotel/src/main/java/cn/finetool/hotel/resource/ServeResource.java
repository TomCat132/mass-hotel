package cn.finetool.hotel.resource;

import cn.finetool.common.configuration.AppContext;
import cn.finetool.common.event.customEvent.ChatEvent;
import cn.finetool.common.event.customEvent.ChatRoomEstablishEvent;
import cn.finetool.common.po.UserRequest;
import cn.finetool.common.util.Response;
import cn.finetool.hotel.handler.ServeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/serve")
@Api(tags = "服务资源接口")
public class ServeResource {

    private final ServeService serveService = AppContext.getBean(ServeService.class);

    @PostMapping("/create-request")
    @ApiOperation(value = "用户请求服务", notes = "用户请求服务")
    public Response userCreateRequest(@RequestBody UserRequest userRequest) {
        return serveService.createUserRequest(userRequest);
    }

    @GetMapping("/get-request-list")
    @ApiOperation(value = "获取未处理的用户请求列表", notes = "PMS：首页获取未处理的用户请求列表")
    public Response getNotHandleRequestList(@RequestParam("merchantId") String merchantId) {
        return serveService.getNotHandleRequestList(merchantId);
    }

    @PostMapping("/handle-request")
    @ApiOperation(value = "开始处理用户请求", notes = "处理用户请求")
    public Response startHandleRequest(@RequestParam("requestId") String requestId) {
        return serveService.startHandleRequest(requestId);
    }

    @GetMapping("/get-request-chat-list")
    @ApiOperation(value = "用户获取请求呼叫聊天列表", notes = "客户端: 首页用户获取请求呼叫聊天列表")
    public Response getRequestChatList(@RequestParam("userId") String userId) {
        return serveService.getRequestChatList(userId);
    }

    @GetMapping("/get-guest-chat-list")
    @ApiOperation(value = "获取客户聊天列表", notes = "聊天室:获取客户聊天列表")
    public Response getGuestChatList(@RequestParam("conductorId") String conductorId) {
        return serveService.getGuestChatList(conductorId);
    }

    @GetMapping("/get-chat-message-list")
    @ApiOperation(value = "查询历史聊天记录", notes = "聊天室:查询历史聊天记录")
    public Response chatMessageList(@RequestParam("chatId") String chatId,
                                    @RequestParam(value = "userId", required = false) String userId) {
        return serveService.chatMessageList(chatId, userId);
    }

    @PostMapping("/end_chat-with-user-request")
    @ApiOperation(value = "结束用户请求聊天服务", notes = "结束用户请求聊天服务")
    public Response endChatWithUserRequest(@RequestParam("chatId") String chatId) {
        return serveService.endChatWithUserRequest(chatId);
    }

    @GetMapping("/get-all-request-list")
    @ApiOperation(value = "获取所有用户请求列表", notes = "PMS：获取所有用户请求列表")
    public Response userRequestList(@RequestParam("merchantId") String merchantId,
                                    @RequestParam(value = "status", required = false) Integer status,
                                    @RequestParam(value = "keyword", required = false) String keyword) {
        return serveService.getAllUserRequestList(merchantId, status, keyword);
    }
}
