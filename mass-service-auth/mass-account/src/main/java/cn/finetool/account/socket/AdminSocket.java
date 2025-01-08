package cn.finetool.account.socket;


import cn.finetool.account.mapper.UserMapper;
import cn.finetool.account.service.impl.UserServiceImpl;
import cn.finetool.api.handler.MessageHandler;
import cn.finetool.common.configuration.AppContext;
import cn.finetool.common.enums.Status;
import cn.finetool.common.po.User;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
public class AdminSocket {

    private static final UserMapper userMapper = AppContext.getBean(UserMapper.class);
    private static final UserServiceImpl userService = AppContext.getBean(UserServiceImpl.class);

    
    private static final Logger Logger = LoggerFactory.getLogger(AdminSocket.class);
    /**
     * 以用户的Id作为key，WebSocket为对象保存起来
     */
    private static Map<String, Session> AdminUserPool = new ConcurrentHashMap<>();
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
    public void onOpen(@PathParam("userId") String userId, Session session) {
        this.userId = userId;
        this.session = session;
        AdminUserPool.put(userId, session);
        // 更改用户状态
        userMapper.update(new UpdateWrapper<User>()
                .set("status", Status.ACCOUNT_ONLINE.code())
                .eq("user_id", userId));
        Logger.info("管理员:{} 已上线", userId);
//        AppContext.getApplicationContext().publishEvent(new MessageHandler(userId, "管理员已上线"));
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
        AdminUserPool.remove(userId);
        Logger.info("管理员:{} 已下线", userId);
    }

    /**
     * 收到客户端的消息
     *
     * @param message 消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        
    }

    public void sendMessageTo(String message, String receiver) {
      
    }


}
