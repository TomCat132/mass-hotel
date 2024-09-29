package cn.finetool.account.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.account.mapper.UserMapper;
import cn.finetool.account.service.RoleService;
import cn.finetool.account.service.UserRolesService;
import cn.finetool.account.service.UserService;
import cn.finetool.api.service.ActivityAPIService;
import cn.finetool.api.service.OrderAPIService;
import cn.finetool.api.service.OssAPIService;
import cn.finetool.api.service.RechargePlanAPIService;
import cn.finetool.common.dto.PasswordDto;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.enums.OrderType;
import cn.finetool.common.enums.RoleType;
import cn.finetool.common.exception.BusinessRuntimeException;

import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.po.Role;
import cn.finetool.common.po.User;

import cn.finetool.common.po.UserRoles;
import cn.finetool.common.util.CommonsUtils;
import cn.finetool.common.util.Response;
import cn.finetool.common.vo.OrderVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserService userService;

    @Resource
    private UserRolesService userRolesService;

    @Resource
    private RoleService roleService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private OrderAPIService orderAPIService;

    @Resource
    private OssAPIService ossAPIService;

    @Autowired
    private UserMapper userMapper;

    @Resource
    private ActivityAPIService activityAPIService;

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
    public Response<User> getUserInfo() {
        User userInfo = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserId, StpUtil.getLoginIdAsString()));
        userInfo.setPassword(null);
        userInfo.setSalty(null);
        return Response.success(userInfo);
    }

    @Override
    public Response logout() {
        try {
            StpUtil.logout();
            return Response.success("已退出");
        }catch (BusinessRuntimeException e){
            throw new BusinessRuntimeException(BusinessErrors.TOKEN_IS_INVALID,"未登录");
        }
    }

    @Override
    public Response editAvatar(MultipartFile file, HttpServletRequest request) {

        try {
            byte[] fileBytes = file.getBytes();
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            String fileUrl = ossAPIService.uploadFileToMinio(fileBytes,fileName,contentType);

            userService.update()
                    .set("avatar_key",fileUrl)
                    .eq("user_id",StpUtil.getLoginIdAsString())
                    .update();
            return Response.success("修改成功");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
    }

    @Override
    public Response editPassword(PasswordDto passwordDto) {

        if (StringUtils.isAnyEmpty(passwordDto.getOldPassword(),passwordDto.getNewPassword(),passwordDto.getConfirmPassword())){
            throw new BusinessRuntimeException(BusinessErrors.PARAM_CANNOT_EMPTY,"数据不能为空~");
        }
        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())){
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_MATCH,"两次新密码输入不一致~");
        }

        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserId, StpUtil.getLoginIdAsString()));
        if (!CommonsUtils.encodeMD5(passwordDto.getOldPassword() + user.getSalty()).equals(user.getPassword())){
            throw new BusinessRuntimeException(BusinessErrors.AUTHENTICATION_ERROR,"旧密码输入错误");
        }

        if (CommonsUtils.encodeMD5(passwordDto.getNewPassword() + user.getSalty()).equals(user.getPassword())){
            throw new BusinessRuntimeException(BusinessErrors.DATA_DUPLICATION,"新密码不能与旧密码相同");
        }


        userService.update()
                .set("password", CommonsUtils.encodeMD5(passwordDto.getNewPassword() + user.getSalty()))
                .eq("user_id", StpUtil.getLoginIdAsString())
                .update();

        StpUtil.logout();;

        return Response.success("密码修改成功，请重新登陆");
    }

    @Override
    public void updateUserInfo(String userId, BigDecimal totalAmount) {
        userMapper.updateUserInfo(userId, totalAmount,totalAmount.intValue(),totalAmount.intValue());
    }

    @Override
    public Response getOrderList() {
        List<OrderVo> orderList = new ArrayList<>();

        //查询充值订单
        List<RechargeOrder> rechargeOrderList = orderAPIService.getRechargeOrderList(StpUtil.getLoginIdAsString());

        if (rechargeOrderList != null){
            List<OrderVo> rechargeOrderSet = rechargeOrderList.stream().map(rechargeOrder -> {
                OrderVo orderVo = new OrderVo();
                orderVo.setOrderId(rechargeOrder.getOrderId());
                orderVo.setOrderType(OrderType.RECHARGE_ORDER.getValue());
                orderVo.setOrderStatus(rechargeOrder.getOrderStatus());
                orderVo.setUserPayAmount(rechargeOrder.getUserPayAmount());
                orderVo.setCreateTime(rechargeOrder.getCreateTime());
                return orderVo;
            }).toList();
            orderList.addAll(rechargeOrderSet);
        }


        return Response.success(orderList);
    }

    @Override
    public Response adminLogin(User user) {

        //1.校验用户名密码
        if (StringUtils.isAnyEmpty(user.getPhone(),user.getPassword())){
            throw new BusinessRuntimeException(BusinessErrors.PARAM_CANNOT_EMPTY);
        }

        User accountInfo = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, user.getPhone()));
        if (!CommonsUtils.encodeMD5(user.getPassword() + accountInfo.getSalty())
                .equals(accountInfo.getPassword())){
            throw new BusinessRuntimeException(BusinessErrors.AUTHENTICATION_ERROR);
        }
        //2.校验是否为管理员
        List<UserRoles> list = userRolesService.list(new LambdaQueryWrapper<UserRoles>()
                .eq(UserRoles::getUserId, accountInfo.getUserId()));
        List<Integer> roleIdList = list.stream().map(UserRoles::getRoleId).toList();
        //3. 批量查询
        List<String> roleList = roleService.listByIds(roleIdList).stream().map(Role::getRoleKey).toList();

        if (!roleList.contains(RoleType.ADMIN.getKey()) && !roleList.contains(RoleType.SUPER_ADMIN.getKey())){
            throw new BusinessRuntimeException(BusinessErrors.PERMISSION_DENIED);
        }
        //4. 校验完成，保存用户角色权限等信息
        StpUtil.login(accountInfo.getUserId());
        StpUtil.getTokenSession().set(SaSession.ROLE_LIST,roleList);

        return Response.success("登录成功");
    }

    @Override
    public Response getVoucher(String voucherId) {
        return activityAPIService.getVoucher(voucherId,StpUtil.getLoginIdAsString());
    }


}
