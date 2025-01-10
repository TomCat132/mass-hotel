package cn.finetool.account.socket;


import cn.finetool.account.mapper.UserMapper;
import cn.finetool.common.configuration.AppContext;
import cn.finetool.common.enums.Status;
import cn.finetool.common.enums.SystemTag;
import cn.finetool.common.po.User;
import cn.finetool.common.util.JsonUtil;
import cn.finetool.common.util.Strings;
import cn.finetool.common.util.TimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * OnOpen 表示有浏览器链接过来的时候被调用
 * OnClose 表示浏览器发出关闭请求的时候被调用
 * OnMessage 表示浏览器发消息的时候被调用
 * OnError 表示有错误发生，比如网络断开了等等
 */
@Slf4j
@ServerEndpoint("/admin/{userId}")
@Data
public class ChatSocket {
    
    /**
     * 系统 管理员 总在线人数
     */
    public static int OnlineCount = 0;
    
    private static final UserMapper userMapper = AppContext.getBean(UserMapper.class);
    /**
     * 以用户的Id作为key，WebSocket为对象保存起来
     */
    public static Map<String, ChatSocket> Clients = new ConcurrentHashMap<>();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatSocket.class);

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 会话S Session
     */
    private Session session;
    
    /**
     * 建立连接
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) throws JsonProcessingException {
        OnlineCount++;
        this.userId = userId;
        this.session = session;
        Clients.put(userId, this);
        // 更改用户状态
        userMapper.update(new UpdateWrapper<User>()
                .set("status", Status.ACCOUNT_ONLINE.code())
                .eq("user_id", userId));
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("user_id", userId));
        String messageContent = String.format("管理员:%s 已上线", user.getUsername());
        ImmutableMap<String, ? extends Serializable> message = ImmutableMap.of("message",messageContent,
                "time", TimeUtil.now().toString(),
                "senderId", SystemTag.SYSTEM_BROADCAST.desc()
                );
        Map<String, ChatSocket> allAdminSession = getAllAdminSession();
        allAdminSession.remove(userId);
        List<String> acceptIds = allAdminSession.keySet().stream().toList();
        broadcastMessage(JsonUtil.toJsonString(message), acceptIds);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.info("服务端发生了错误, error message:{}", error.getMessage());
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose() {
        OnlineCount++;
        Clients.remove(userId);
        LOGGER.info("管理员:{} 已下线", userId);
    }

    /**
     * 收到客户端的消息
     *
     * @param message 消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        if (Strings.equals(message, "heartbeat")){
            //心跳检测
            Clients.get(userId).session.getAsyncRemote().sendText("ok");
            return;
        }

        LOGGER.info("来自客户端消息:" + message + "客户端的ID:" + session.getId());
    }

    /**
     * 广播消息
     * @param message
     */
    private void broadcastMessage(String message, List<String> acceptIds){
        for (Map.Entry<String, ChatSocket> entry: Clients.entrySet()) {
            Session adminSession = entry.getValue().session;
            try {
                adminSession.getBasicRemote().sendText(message);
            } catch (Exception e) {
                LOGGER.error("广播消息失败");
            }
        }
    }

    /**
     * 获取所有管理员的session
     * @return
     */
    public  Map<String, ChatSocket> getAllAdminSession(){
        //复制一份，防止修改原数据
        return new HashMap<>(Clients);
    }
    
    
    public Session session(){
        return this.session;
    }
    
    public String userId(){
        return this.userId;
    }

}
