package cn.finetool.account.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.account.mapper.UserMapper;
import cn.finetool.account.service.UserRolesService;
import cn.finetool.account.service.UserService;
import cn.finetool.api.service.OrderAPIService;
import cn.finetool.common.configuration.ChannelManager;
import cn.finetool.common.constant.MqQueue;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.enums.RoleType;
import cn.finetool.common.enums.Status;
import cn.finetool.common.exception.BusinessRuntimeException;

import cn.finetool.common.po.User;

import cn.finetool.common.util.CommonsUtils;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserService userService;

    @Resource
    private UserRolesService userRolesService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private OrderAPIService orderAPIService;

    @Value("${spring.rabbitmq.host}")
    private String RabbitMqHost;

    @Override
    public Response register(User user) {
        if (StringUtils.isAnyEmpty(user.getUsername(),user.getPhone(),user.getPassword())){
            throw new BusinessRuntimeException(BusinessErrors.PARAM_CANNOT_EMPTY,"用户名、手机号、密码不能为空");
        }

        User userByPhone = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, user.getPhone()));
        if (userByPhone != null){
            throw new BusinessRuntimeException(BusinessErrors.DATA_DUPLICATION,"手机号已注册");
        }
        User userByUsername = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername()));
        if (userByUsername != null){
            throw new BusinessRuntimeException(BusinessErrors.DATA_DUPLICATION,"用户名已注册");
        }
        String salty = User.generateSalty();
        user.setPassword(CommonsUtils.encodeMD5(user.getPassword() + salty));
        user.setRegistrationTime(LocalDateTime.now());
        user.setUserId(CommonsUtils.getWorkerID());
        user.setSalty(salty);

        save(user);
        // 默认为 用户 角色
        userRolesService.saveUserRoles(RoleType.USER.getCode(),user.getUserId());

        return Response.success(user);
    }

    @Override
    public Response login(User user) {
        if (StringUtils.isAnyEmpty(user.getPhone(),user.getPassword())){
            throw new BusinessRuntimeException(BusinessErrors.PARAM_CANNOT_EMPTY,"手机号、密码不能为空");
        }
        User DBUser = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, user.getPhone()));
        if (DBUser == null){
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_EXIST,"用户不存在");
        }
        if (!CommonsUtils.encodeMD5(user.getPassword() + DBUser.getSalty()).equals(DBUser.getPassword())){
            throw new BusinessRuntimeException(BusinessErrors.AUTHENTICATION_ERROR,"用户名或密码错误");
        }
        StpUtil.login(DBUser.getUserId());

        userService.update()
                .set("last_login_time", LocalDateTime.now())
                .eq("user_id", DBUser.getUserId())
                .update();

        return Response.success("登录成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response payOrder(String orderId) {

        // 支付成功后，修改订单状态
        orderAPIService.updateOrderStatus(orderId, Status.ORDER_SUCCESS.getCode());
        // 手动消费消息



        redisTemplate.delete(RedisCache.RECHARGE_ORDER_ORDER_TAG + orderId);


        return Response.success("支付成功");
    }

    @Override
    public Response<User> getUserInfo() {
        User userInfo = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserId, StpUtil.getLoginIdAsString()));
        userInfo.setPassword(null);
        userInfo.setSalty(null);
        return Response.success(userInfo);
    }




}
