package cn.finetool.account.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.account.mapper.RoleMapper;
import cn.finetool.account.mapper.SystemMapper;
import cn.finetool.account.mapper.UserMapper;
import cn.finetool.account.service.RoleService;
import cn.finetool.account.service.UserRolesService;
import cn.finetool.account.service.UserService;
import cn.finetool.api.service.ActivityAPIService;
import cn.finetool.api.service.OrderAPIService;
import cn.finetool.api.service.OssAPIService;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.dto.PasswordDto;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.enums.CodeSign;
import cn.finetool.common.enums.RoleType;
import cn.finetool.common.exception.BusinessRuntimeException;

import cn.finetool.common.po.*;

import cn.finetool.common.util.CommonsUtils;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.common.vo.OrderVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final SnowflakeIdWorker IdWorker = new SnowflakeIdWorker(0, 0);

    @Resource
    private UserService userService;

    @Resource
    private UserRolesService userRolesService;

    @Resource
    private RoleService roleService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private OrderAPIService orderAPIService;

    @Resource
    private OssAPIService ossAPIService;

    @Autowired
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private SystemMapper systemMapper;

    @Resource
    private ActivityAPIService activityAPIService;

    @Override
    public Response register(User user) {
        if (StringUtils.isAnyEmpty(user.getUsername(), user.getPhone(), user.getPassword())) {
            throw new BusinessRuntimeException(BusinessErrors.PARAM_CANNOT_EMPTY, "用户名、手机号、密码不能为空");
        }

        User userByPhone = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, user.getPhone()));
        if (userByPhone != null) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_DUPLICATION, "手机号已注册");
        }
        User userByUsername = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername()));
        if (userByUsername != null) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_DUPLICATION, "用户名已注册");
        }
        String salty = User.generateSalty();
        user.setPassword(CommonsUtils.encodeMD5(user.getPassword() + salty));
        user.setRegistrationTime(LocalDateTime.now());
        // 设置用户ID 前缀标志 1010
        user.setUserId(CodeSign.UserPrefix.getCode() + String.valueOf(IdWorker.nextId()));
        user.setSalty(salty);

        save(user);
        // 默认为 用户 角色
        userRolesService.saveUserRoles(RoleType.USER.getCode(), user.getUserId());

        return Response.success(user);
    }

    @Override
    public Response login(User user) {
        if (StringUtils.isAnyEmpty(user.getPhone(), user.getPassword())) {
            throw new BusinessRuntimeException(BusinessErrors.PARAM_CANNOT_EMPTY, "手机号、密码不能为空");
        }
        User DBUser = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, user.getPhone()));
        if (DBUser == null) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_EXIST, "用户不存在");
        }
        if (!CommonsUtils.encodeMD5(user.getPassword() + DBUser.getSalty()).equals(DBUser.getPassword())) {
            throw new BusinessRuntimeException(BusinessErrors.AUTHENTICATION_ERROR, "用户名或密码错误");
        }
        StpUtil.login(DBUser.getUserId());

        userService.update()
                .set("last_login_time", LocalDateTime.now())
                .eq("user_id", DBUser.getUserId())
                .update();

        // 保存用户角色权限信息
        List<String> roleList = new ArrayList<>();

        List<UserRoles> userRolesList = userRolesService.list(new LambdaQueryWrapper<UserRoles>()
                .eq(UserRoles::getUserId, DBUser.getUserId()));

        userRolesList.forEach(userRoles -> {
            Role roleInfo = roleService.getOne(new LambdaQueryWrapper<Role>()
                    .eq(Role::getRoleId, userRoles.getRoleId()));
            roleList.add(roleInfo.getRoleKey());
        });

        //
        StpUtil.getTokenSession().set(SaSession.ROLE_LIST, roleList);

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
        } catch (BusinessRuntimeException e) {
            throw new BusinessRuntimeException(BusinessErrors.TOKEN_IS_INVALID, "未登录");
        }
    }

    @Override
    public Response editAvatar(MultipartFile file, HttpServletRequest request) {

        try {
            byte[] fileBytes = file.getBytes();
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            String fileUrl = ossAPIService.uploadFileToMinio(fileBytes, fileName, contentType);

            userService.update()
                    .set("avatar_key", fileUrl)
                    .eq("user_id", StpUtil.getLoginIdAsString())
                    .update();
            return Response.success("修改成功");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Response editPassword(PasswordDto passwordDto) {

        if (StringUtils.isAnyEmpty(passwordDto.getOldPassword(), passwordDto.getNewPassword(), passwordDto.getConfirmPassword())) {
            throw new BusinessRuntimeException(BusinessErrors.PARAM_CANNOT_EMPTY, "数据不能为空~");
        }
        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_NOT_MATCH, "两次新密码输入不一致~");
        }

        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserId, StpUtil.getLoginIdAsString()));
        if (!CommonsUtils.encodeMD5(passwordDto.getOldPassword() + user.getSalty()).equals(user.getPassword())) {
            throw new BusinessRuntimeException(BusinessErrors.AUTHENTICATION_ERROR, "旧密码输入错误");
        }

        if (CommonsUtils.encodeMD5(passwordDto.getNewPassword() + user.getSalty()).equals(user.getPassword())) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_DUPLICATION, "新密码不能与旧密码相同");
        }


        userService.update()
                .set("password", CommonsUtils.encodeMD5(passwordDto.getNewPassword() + user.getSalty()))
                .eq("user_id", StpUtil.getLoginIdAsString())
                .update();

        StpUtil.logout();
        ;

        return Response.success("密码修改成功，请重新登陆");
    }

    @Override
    public void updateUserInfo(String userId, BigDecimal totalAmount) {
        userMapper.updateUserInfo(userId, totalAmount, totalAmount.intValue(), totalAmount.intValue());
    }

    @Override
    public Response getOrderList() {
        List<OrderVO> orderList = new ArrayList<>();

        //查询充值订单
        List<OrderVO> rechargeOrderList = orderAPIService.getRechargeOrderList(StpUtil.getLoginIdAsString());
        if (Objects.nonNull(rechargeOrderList)) {
            orderList.addAll(rechargeOrderList.stream().peek(orderVo -> orderVo.setOrderType(CodeSign.RechargeOrderPrefix.getCode())).toList());
        }
        //查询房间预定订单
        List<OrderVO> roomOrderList = orderAPIService.getRoomOrderList(StpUtil.getLoginIdAsString());
        if (Objects.nonNull(roomOrderList)) {
            orderList.addAll(roomOrderList.stream().peek(orderVo -> orderVo.setOrderType(CodeSign.HotelOrderPrefix.getCode())).toList());
        }
        // 根据订单状态、订单时间排序，优先级：0：未支付 再按时间 降序
        orderList.sort(Comparator.comparing(OrderVO::getOrderStatus).reversed().thenComparing(OrderVO::getCreateTime).reversed());
        return Response.success(orderList);
    }

    @Override
    public Response adminLogin(User user) {

        //校验用户名密码
        if (StringUtils.isAnyEmpty(user.getPhone(), user.getPassword())) {
            throw new BusinessRuntimeException(BusinessErrors.PARAM_CANNOT_EMPTY);
        }

        User accountInfo = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, user.getPhone()));
        if (Objects.isNull(accountInfo)) {
            throw new BusinessRuntimeException(BusinessErrors.ACCOUNT_NOT_EXIST);
        }
        if (!CommonsUtils.encodeMD5(user.getPassword() + accountInfo.getSalty())
                .equals(accountInfo.getPassword())) {
            throw new BusinessRuntimeException(BusinessErrors.AUTHENTICATION_ERROR);
        }
        //校验是否为管理员
        List<UserRoles> list = userRolesService.list(new LambdaQueryWrapper<UserRoles>()
                .eq(UserRoles::getUserId, accountInfo.getUserId()));
        List<Integer> roleIdList = list.stream().map(UserRoles::getRoleId).toList();
        //批量查询
        List<String> roleList = roleService.listByIds(roleIdList).stream().map(Role::getRoleKey).toList();
        //判断是否为管理员/超级管理员/系统管理员
        //如果是，则保存用户角色权限等信息；否则，返回权限不足
        if (!roleList.contains(RoleType.ADMIN.getKey()) && !roleList.contains(RoleType.SUPER_ADMIN.getKey())
                && !roleList.contains(RoleType.SYS_ADMIN.getKey())) {
            return Response.error("权限不足");
        }
        //校验完成，保存用户角色权限等信息
        StpUtil.login(accountInfo.getUserId());
        StpUtil.getTokenSession().set(SaSession.ROLE_LIST, roleList);

        //保存用户所在酒店Id
        Integer hotelId = systemMapper.getHotelId(accountInfo.getUserId());
        redisTemplate.opsForValue().set(RedisCache.USER_HOTEL_BINDING + user.getUserId(), hotelId);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("userId", accountInfo.getUserId());
        resultMap.put("hotelId", hotelId);
        resultMap.put("userRole", roleList);

        return Response.success(resultMap);
    }

    @Override
    public Response getVoucher(String voucherId) {
        return activityAPIService.getVoucher(voucherId, StpUtil.getLoginIdAsString());
    }

    @Override
    public BigDecimal getUserAccount(String userId) {
        return userMapper.getUserAccount(userId);
    }

    @Override
    public void decreaseUserAccount(String userId, BigDecimal userPayAmount) {
        userMapper.decreaseUserAccount(userId, userPayAmount);
    }

    @Override
    public Response deleteOrderById(String orderId) {
        orderAPIService.deleteOrder(orderId);
        return Response.success("订单删除成功");
    }

    @Override
    public Response checkPwd(String oldPwd) {
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("user_id", StpUtil.getLoginIdAsString()));
        if (!CommonsUtils.encodeMD5(oldPwd + user.getSalty()).equals(user.getPassword())) {
            return Response.error("false");
        }

        return Response.success("true");
    }


}
