package cn.finetool.account.event.listener;

import cn.finetool.account.event.customEvent.LoginLogEvent;
import cn.finetool.account.event.customEvent.MessageNoticeEvent;
import cn.finetool.account.mapper.LoginLogMapper;
import cn.finetool.api.handler.MessageHandler;
import cn.finetool.common.configuration.AppContext;
import cn.finetool.common.po.LoginLog;
import cn.finetool.common.util.IpUtil;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.common.util.TimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


/**
 * 
 */
@Component
public class SpringEventListener {
    
    private static final SnowflakeIdWorker IdWorker = new SnowflakeIdWorker(0, 0);
    @Resource
    private LoginLogMapper loginLogMapper;
    @Resource
    private MessageHandler messageHandler;
    
    /**
     * 监听登录事件
     * @param event: 登录事件
     */
    @Async
    @EventListener
    public void handleLoginLogEvent(LoginLogEvent event) throws JsonProcessingException {
        System.out.println("记录登录日志：" + event.toString());
        String ip = event.getIp();
        Map<String, String> locationInfo = IpUtil.getLocationInfo(ip);
        String country = locationInfo.get("country");
        String province = locationInfo.get("province");
        String city = locationInfo.get("city");
        String lat = locationInfo.get("lat");
        String lon = locationInfo.get("lon");
        String userId = event.getUserId();
        String operationSystem = event.getOperationSystem();
        LocalDateTime nowTime = TimeUtil.now();

        LoginLog loginLog = new LoginLog();
        String messageId = String.valueOf(IdWorker.nextId());
        loginLog.setId(messageId);
        loginLog.setUserId(userId);
        loginLog.setLoginIp(ip);
        loginLog.setCountry(country);
        loginLog.setLat(lat);
        loginLog.setLon(lon);
        loginLog.setProvince(province);
        loginLog.setCity(city);
        loginLog.setCreateTime(nowTime);
        loginLog.setSystem(operationSystem);
        loginLogMapper.insert(loginLog);

        //发送消息事件
        String messageContent = "登录成功&登录时间: " + TimeUtil.format(nowTime) + "&IP: [" + ip + "]&地区: "
                + country + province + city + "&操作系统: " + operationSystem;
        AppContext.getApplicationContext()
                .publishEvent(new MessageNoticeEvent(this, userId, messageContent, messageId, true));
    }

    /**
     * 监听消息单发送事件
     * <p>异步执行</p>
     * @param event   : 消息单发送事件
     */
    @Async
    @EventListener
    public void handleSingleMessageNoticeEvent(MessageNoticeEvent event){
        String acceptId = event.getAcceptId();
        String message = event.getMessage();
        String affairId = event.getAffairId();
        messageHandler.sendMessage(acceptId, message, affairId);
    } 
}
