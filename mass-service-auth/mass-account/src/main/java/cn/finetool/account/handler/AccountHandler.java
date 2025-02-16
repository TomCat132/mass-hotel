package cn.finetool.account.handler;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.account.mapper.EvaluationMapper;
import cn.finetool.account.mapper.RoleMapper;
import cn.finetool.account.mapper.UserMapper;
import cn.finetool.account.mapper.UserMerchantMapper;
import cn.finetool.account.mapper.UserRolesMapper;
import cn.finetool.account.service.AccountService;
import cn.finetool.account.socket.ChatSocket;
import cn.finetool.api.mapper.MessageBoxMapper;
import cn.finetool.api.service.HotelAPIService;
import cn.finetool.api.service.OrderAPIService;
import cn.finetool.api.service.OssAPIService;
import cn.finetool.common.dto.EvaluationDto;
import cn.finetool.common.dto.UserDto;
import cn.finetool.common.enums.Status;
import cn.finetool.common.enums.SysEnum;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.Evaluation;
import cn.finetool.common.po.FileUrl;
import cn.finetool.common.po.MessageBox;
import cn.finetool.common.po.Role;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.po.User;
import cn.finetool.common.po.UserMerchant;
import cn.finetool.common.po.UserRoles;
import cn.finetool.common.util.CommonsUtils;
import cn.finetool.common.util.FunUtil;
import cn.finetool.common.util.JsonUtil;
import cn.finetool.common.util.MemberUtil;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.common.util.Strings;
import cn.finetool.common.util.TimeUtil;
import cn.finetool.common.vo.EvaluationVO;
import cn.finetool.common.vo.RoleVO;
import cn.finetool.common.vo.RoomOrderBaseInfo;
import cn.finetool.common.vo.UpGradeVO;
import cn.finetool.common.vo.UserVO;
import cn.finetool.common.util.IpUtil;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static cn.finetool.common.util.Response.success;

@Component
public class AccountHandler implements AccountService {

    public static final SnowflakeIdWorker ID_WORKER = new SnowflakeIdWorker(0, 0);
    @Resource
    private UserMerchantMapper userMerchantMapper;
    @Resource
    private MessageBoxMapper messageBoxMapper;
    @Resource
    private UserRolesMapper userRolesMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserMapper userMapper;
    @Resource
    private EvaluationMapper evaluationMapper;
    @Resource
    private OrderAPIService orderAPIService;
    @Resource
    private OssAPIService ossAPIService;
    @Resource
    private HotelAPIService hotelAPIService;

    @Override
    public String queryMerchantOfUser(String userId) {
        UserMerchant userMerchant = userMerchantMapper.selectOne(new QueryWrapper<UserMerchant>()
                .eq("user_id", userId));
        if (Objects.isNull(userMerchant)) {
            return null;
        }
        return userMerchant.getMerchantId();
    }

    @Override
    public List<String> findMerchantEmployee(String merchantId) {
        List<UserMerchant> merchantEmployeeList = userMerchantMapper.selectList(new QueryWrapper<UserMerchant>()
                .eq("merchant_id", merchantId));
        return merchantEmployeeList.stream().map(UserMerchant::getUserId).collect(Collectors.toList());
    }

    @Override
    public Response getUserMessageBoxList() {
        List<MessageBox> messageBoxList = messageBoxMapper.selectList(new QueryWrapper<MessageBox>()
                .eq("accept_id", StpUtil.getLoginIdAsString())
                .orderByDesc("sender_time"));
        if (CollectionUtils.isNotEmpty(messageBoxList)) {
            return success(messageBoxList);
        }
        return success(Collections.emptyList());
    }

    @Override
    public Response getUnreadMessageCount() {
        return success(messageBoxMapper.selectList(new QueryWrapper<MessageBox>()
                .eq("accept_id", StpUtil.getLoginIdAsString())
                .eq("status", Status.MESSAGE_UNREAD.code())).size());
    }

    @Override
    public Response tagAllMessageRead() {
        messageBoxMapper.update(new UpdateWrapper<MessageBox>()
                .set("status", Status.MESSAGE_READ.code())
                .eq("accept_id", StpUtil.getLoginIdAsString()));
        return success("所有消息已被标记为已读");
    }

    @Override
    public String findMerchantIdByUserId(String workerId) {
        return userMerchantMapper.selectOne(new QueryWrapper<UserMerchant>()
                .eq("user_id", workerId)).getMerchantId();
    }

    @Override
    public Response getUserLocationInfo() {
        Map<String, String> locationInfo = IpUtil.getLocationInfo();
        return success(locationInfo);
    }

    @Override
    public String findMerchantIdOfUserId(String userId) {
        UserMerchant merhcantOfUser = userMerchantMapper.selectOne(new QueryWrapper<UserMerchant>()
                .eq("user_id", userId));
        return merhcantOfUser.getMerchantId();
    }

    @Override
    public Response getMerchantEmployeeList(String merchantId) {
        List<UserMerchant> employeeList = userMerchantMapper.selectList(new QueryWrapper<UserMerchant>()
                .eq("merchant_id", merchantId));
        List<String> userIds = employeeList.stream().map(UserMerchant::getUserId).toList();
        List<UserRoles> uRRelationList = userRolesMapper.selectList(new QueryWrapper<UserRoles>()
                .in("user_id", userIds));
        List<Integer> roleIds = uRRelationList.stream().map(UserRoles::getRoleId).toList();
        Map<Integer, String> idOmKeyMap = roleMapper.selectList(new QueryWrapper<Role>()
                        .in("role_id", roleIds))
                .stream()
                .collect(Collectors.toMap(Role::getRoleId, Role::getRoleKey));
        //转成map user_id , role_key
        Map<String, String> resultMap = uRRelationList.stream()
                .collect(Collectors.toMap(UserRoles::getUserId, userRoles ->
                        idOmKeyMap.get(userRoles.getRoleId())));
        Map<String, User> userInfoMap = userMapper.selectList(new QueryWrapper<User>()
                        .in("user_id", userIds))
                .stream()
                .collect(Collectors.toMap(User::getUserId, User -> User));

        List<UserVO> userVOList = employeeList.stream()
                .map(employee -> {
                    UserVO userVO = new UserVO();
                    userVO.setUserId(employee.getUserId());
                    userVO.setStatus(employee.getStatus());
                    //查询账号角色 role_name, role_key
                    userVO.setRole(resultMap.get(employee.getUserId()));
                    //查询账号信息
                    User userInfo = userInfoMap.get(employee.getUserId());
                    userVO.setUsername(userInfo.getUsername());
                    userVO.setPhone(userInfo.getPhone());
                    userVO.setEmail(userInfo.getEmail());
                    userVO.setIsOnLine(Strings.equals(userInfo.getStatus(), Status.ACCOUNT_ONLINE.code()));
                    return userVO;
                })
                .collect(Collectors.toList());
        return success(userVOList);
    }

    @Override
    public Response accountCold(String userId) {
        // 冻结账号
        userMerchantMapper.update(new UpdateWrapper<UserMerchant>()
                .set("status", Status.ACCOUNT_CLOD.code())
                .eq("user_id", userId));
        // 如果账号在线，则踢下线
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("user_id", userId));
        if (Strings.equals(user.getStatus(), Status.ACCOUNT_ONLINE.code())) {
            StpUtil.kickout(userId);
            userMapper.update(new UpdateWrapper<User>()
                    .set("status", Status.ACCOUNT_OFFLINE.code())
                    .eq("user_id", userId));
        }
        // TODO:记录日志
        return success("账号已冻结");
    }

    @Override
    public Response accountUnCold(String userId) {
        // 解冻账号
        userMerchantMapper.update(new UpdateWrapper<UserMerchant>()
                .set("status", Status.ACCOUNT_NORMAL.code())
                .eq("user_id", userId));
        // TODO:记录日志
        return success("账号已解冻");
    }

    @Override
    public Response setPermission(String userId, String permission) {
        // 查询权限id
        Role roleKey = roleMapper.selectOne(new QueryWrapper<Role>()
                .eq("role_key", permission));
        userRolesMapper.update(new UpdateWrapper<UserRoles>()
                .set("role_id", roleKey.getRoleId())
                .eq("user_id", userId));
        // TODO:记录日志
        return success("权限已变更");
    }

    @Override
    public Response deleteResignedEmployee(String userId) {
        // 校验是否为离职员工
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("user_id", userId));
        if (!Strings.equals(user.getStatus(), Status.ACCOUNT_RESIGNED.code())) {
            throw new BusinessRuntimeException("该员工未离职，无法删除");
        }
        // 校验是否是自己账号
        if (Strings.equals(StpUtil.getLoginIdAsString(), userId)) {
            throw new BusinessRuntimeException("无权限操作本人账号");
        }
        // 删除账号
        userMapper.delete(new QueryWrapper<User>()
                .eq("user_id", userId));
        // 删除关联关系
        userMerchantMapper.delete(new QueryWrapper<UserMerchant>()
                .eq("user_id", userId));
        userRolesMapper.delete(new QueryWrapper<UserRoles>()
                .eq("user_id", userId));
        // TODO:记录日志
        return success("员工账号已删除");
    }

    @Override
    public Response newEmployeeInfo(UserDto userDto) {
        // 手机号唯一
        String phone = userDto.getPhone();
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("phone", phone));
        if (Objects.nonNull(user)) {
            throw new BusinessRuntimeException("手机号已存在");
        }
        // 发放账号
        generateAccount(userDto);
        return success("账号已发放");
    }

    @Override
    public void saveEvaluation(Evaluation evaluation) {
        evaluationMapper.insert(evaluation);
    }

    @Override
    public Response evaluateAfterEnd(EvaluationDto evaluationDto, List<MultipartFile> avatarList) {
        // 评级、内容、时间
        UpdateWrapper<Evaluation> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("content", evaluationDto.getContent())
                .set("create_time", TimeUtil.now())
                .eq("evaluation_id", evaluationDto.getEvaluationId());
        // 如果 content 不为空，则更新 content 字段
        if (evaluationDto.getContent() != null && !evaluationDto.getContent().isEmpty()) {
            updateWrapper.set("star", evaluationDto.getStar());
            // 更新订单评价状态
            orderAPIService.updateEvaluateStatus(evaluationDto.getOrderId(), Status.EVALUATION_YES.code());
        }
        // 执行更新操作
        evaluationMapper.update(null, updateWrapper);
        // 上传评价图
        if (CollectionUtils.isNotEmpty(avatarList)) {
            ossAPIService.batchUploadImage(avatarList, evaluationDto.getEvaluationId());
        }
        return success("感谢您的评价，我们会努力提供更好的服务");
    }

    @Override
    public Response getEvaluateByOrderId(String orderId) {
        Evaluation evaluation = evaluationMapper.selectOne(new QueryWrapper<Evaluation>()
                .eq("order_id", orderId));
        return success(evaluation);
    }

    @Override
    public Response getEvaluateListByRelationId(String relationId) {
        List<Evaluation> relationList = evaluationMapper.selectList(new QueryWrapper<Evaluation>()
                .eq("relation_id", relationId));
        //批量查图片再进行分组避免循环内查DB
        List<String> uniqueIds = relationList.stream()
                .map(Evaluation::getEvaluationId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<FileUrl> imageListByUniqueIds = ossAPIService.findImageListByUniqueIds(uniqueIds);
        // key: EvaluationId, value: List<FileUrl>
        Map<String, List<FileUrl>> fileUrlMap = FunUtil.groupBy(imageListByUniqueIds, FileUrl::getUniqueId);

        //批量查询用户相关信息
        List<String> userIds = relationList.stream()
                .map(Evaluation::getUserId)
                .collect(Collectors.toList());
        List<User> userInfoList = userMapper.selectList(new QueryWrapper<User>()
                .in("user_id", userIds));
        Map<String, User> userInfoMap = FunUtil.toMap(userInfoList, User::getUserId);
        List<EvaluationVO> evaluationVOList = relationList.stream()
                .map(evaluation -> {
                    //填充数据
                    EvaluationVO evaluationVO = new EvaluationVO(evaluation);
                    //获取评价图片列表
                    List<String> imageList = fileUrlMap.getOrDefault(evaluation.getEvaluationId(),
                                    Collections.emptyList())
                            .stream()
                            .map(FileUrl::getUrlImage)
                            .collect(Collectors.toList());
                    evaluationVO.setEvaluationAvatarList(imageList);
                    //获取用户图片
                    User userInfo = userInfoMap.getOrDefault(evaluation.getUserId(), new User());
                    evaluationVO.setUserAvatar(ossAPIService.findImageByUrl(userInfo.getAvatarKey()));
                    evaluationVO.setUsername(userInfo.getUsername());
                    return evaluationVO;
                })
                .collect(Collectors.toList());
        return CollectionUtils.isEmpty(evaluationVOList) ? success(Collections.emptyList()) : success(evaluationVOList);
    }

    @Override
    public User findUserInfoUserId(String userId) {
        return userMapper.selectOne(new QueryWrapper<User>()
                .eq("user_id", userId));
    }

    @Override
    public void noticeUser(String chatId, String requestId, String customerId, String conductorId) throws JsonProcessingException {
        // 发送消息
        Map<String, Object> message = new HashMap<>();
        message.put("chatId", chatId);
        message.put("senderId", conductorId);
        message.put("senderTime", TimeUtil.nowStr());
        message.put("message", "请问有什么需要帮助的吗？");
        ChatSocket.sendMessageTo(JsonUtil.toJsonString(message), customerId);
    }

    @Override
    public Response getUserInfoList(Integer page, Integer size, String keyword) {
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1) size = 10;
        List<UserVO> userVOList = new ArrayList<>();
        IPage<User> pageInfo = new Page<>(page, size);
        QueryWrapper<User> querywrapper = new QueryWrapper<>();
        if (Strings.isNotBlank(keyword)){
            querywrapper.like("username", keyword);
        }
        List<User> userList = userMapper.selectPage(pageInfo, querywrapper).getRecords();

        for (User user : userList) {
            UserVO userVO = new UserVO();
            UserRoles userRoles = userRolesMapper.selectOne(new QueryWrapper<UserRoles>()
                    .eq("user_id", user.getUserId()));
            Role roleInfo = roleMapper.selectOne(new QueryWrapper<Role>()
                    .eq("role_id", userRoles.getRoleId()));
            switch (roleInfo.getRoleKey()) {
                case "admin":
                    userVO.setRole("商户普通管理员");
                    break;
                case "super_admin":
                    userVO.setRole("商户超级管理员");
                    break;
                case "user":
                    userVO.setRole("普通用户");
                    break;
                case "sys_admin":
                    userVO.setRole("系统管理员");
                default:
                    break;
            }
            userVO.setUserId(user.getUserId());
            userVO.setUsername(user.getUsername());
            userVO.setPhone(user.getPhone());
            userVO.setIsOnLine(Strings.equals(Status.ACCOUNT_ONLINE.code(), user.getStatus()));
            userVO.setRegisterTime(user.getRegistrationTime());
            userVOList.add(userVO);
        }
        return success(userVOList);
    }

    @Override
    public Response getPolicyList() {
        List<Role> userRoleList = roleMapper.selectList(null);
        if (CollectionUtils.isNotEmpty(userRoleList)) {

            List<RoleVO> roleVoList = userRoleList.stream().map(role -> {
                RoleVO roleVO = new RoleVO();
                roleVO.setRoleId(role.getRoleId());
                roleVO.setRoleName(role.getRoleName());
                roleVO.setRoleKey(role.getRoleKey());
                int count = userRolesMapper.selectList(new QueryWrapper<UserRoles>()
                        .eq("role_id", role.getRoleId())).size();
                roleVO.setRoleCount(count);
                return roleVO;
            }).collect(Collectors.toList());
            return success(roleVoList);
        }

        return success(Collections.emptyList());
    }

    @Override
    public Response addPolicy(Role role) {
        Role roleInfo = roleMapper.selectOne(new QueryWrapper<Role>()
                .eq("role_key", role.getRoleKey())
                .or()
                .eq("role_name", role.getRoleName()));
        if (Objects.nonNull(roleInfo)){
            throw new BusinessRuntimeException("权限名称或权限标识已存在,请修改后保存");
        }
        roleMapper.insert(role);
        return success("权限添加成功");
    }

    @Override
    public Response policySetting(String userId, Integer roleId) {
        UserRoles userRoles = userRolesMapper.selectOne(new QueryWrapper<UserRoles>()
                .eq("user_id", userId)
                .eq("role_id", roleId));
        if (Objects.nonNull(userRoles)){
            throw new BusinessRuntimeException("请设置不同角色权限");
        }
        userRolesMapper.update(new UpdateWrapper<UserRoles>()
                .set("role_id", roleId)
                .eq("user_id", userId));
        return success("权限设置成功");
    }

    @Override
    public Response deletePolicy(Integer roleId) {
        List<UserRoles> userRolesList = userRolesMapper.selectList(new QueryWrapper<UserRoles>()
                .eq("role_id", roleId));
        if (CollectionUtils.isNotEmpty(userRolesList)){
            throw new BusinessRuntimeException("删除该角色时请确保已无用户设置改角色");
        }
        roleMapper.deleteById(roleId);
        return success("删除成功");
    }

    @Override
    public void changeAccountBalance(String userId, BigDecimal userPayAmount, boolean isAdd) {
        if (Strings.equals(true, isAdd)){
            userMapper.increaseUserAccount(userId, userPayAmount);
        } else {
            userMapper.decreaseUserAccount(userId, userPayAmount);
        }
    }

    @Override
    public Response getEvaluationList(String merchantId, String time, Integer status, String keyword) {
        List<Integer> statusOrder = Arrays.asList(Status.EVALUATION_NO.code(),
                Status.EVALUATION_YES.code());
        // 创建查询包装器并添加查询条件
        QueryWrapper<Evaluation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("merchant_id", merchantId);
        // 构造FIELD函数的参数字符串
        queryWrapper
                .orderByAsc("create_time");
        if (Strings.isNotBlank(time)){
            queryWrapper.like("create_time", TimeUtil.parseToLocalDate(time));
        }
        if (Strings.isNotBlank(keyword)){
            queryWrapper.like("content", keyword);
        }
        List<Evaluation> evaluations = evaluationMapper.selectList(queryWrapper);
        List<EvaluationVO> evaluationVOList = evaluations.stream()
                .map(evaluation -> {
                    EvaluationVO evaluationVO = new EvaluationVO(evaluation);
                    User user = userMapper.selectOne(new QueryWrapper<User>()
                            .eq("user_id", evaluation.getUserId()));
                    evaluationVO.setUsername(user.getUsername());

                    //查看订单是否已评价
                    String orderId = evaluationVO.getOrderId();
                    // 截取前4位
                    String prefix = orderId.substring(0, 4);
                    if (Strings.equals(prefix, SysEnum.ROOM_ORDER_PREFIX.code())){
                        // 房间订单
                        RoomOrder roomOrder = orderAPIService.queryOrderInfo(orderId);
                        evaluationVO.setStatus(roomOrder.getIsEvaluate());
                    }
                    
                    return evaluationVO;
                })
                .collect(Collectors.toList());
        if (Objects.nonNull(status)){
            evaluationVOList = evaluationVOList.stream()
                    .filter(evaluationVO -> statusOrder.contains(evaluationVO.getStatus()))
                    .collect(Collectors.toList());
        }
        return success(evaluationVOList);
    }

    @Override
    public void grantConsumeVoucher(String userId, Integer consumeCount) {
        userMapper.grantConsumeVoucher(userId, consumeCount);
    }

    @Override
    public void grantPoints(String userId, Integer rewardPoints) {
        userMapper.grantPoints(userId, rewardPoints);
    }

    @Override
    public Response getAccountInfo() {
        // 获取账号信息
        String userId = StpUtil.getLoginIdAsString();
        return success(findUpGradeInfo(userId));
    }

    @Override
    public Response getConvenientInfo() {
        // 查询最新的服务订单
        String userId = StpUtil.getLoginIdAsString();
        // 查询入住订单及其关联订单数据
        RoomOrderBaseInfo roomOrderBaseInfo = orderAPIService.findOrderBaseInfoByUserId(userId);
        // TODO:如果是已完成的订单，要查询评论数据
        return success(roomOrderBaseInfo);
    }

    @Override
    public void usePoints(String userId, Integer needPoints) {
        userMapper.usePoints(userId, needPoints);
    }


    private UpGradeVO findUpGradeInfo(String userId) {
        UpGradeVO upGradeVO = new UpGradeVO();
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("user_id", userId));
        // 当前会员等级
        Integer memberLevel = user.getMemberLevel();
        upGradeVO.setCurrentLevel(memberLevel);
        // 下一等级
        if (!Strings.equals(memberLevel, Status.MEMBER_DIAMOND.code())){
            int nextLevel = memberLevel + 1;
            upGradeVO.setNextLevel(nextLevel);
            // 下一级等级所需积分 1、2、3
            int upgradePoints = MemberUtil.getRelationPoints(nextLevel);
            upGradeVO.setUpgradePoints(upgradePoints);
            // 计算升级到下一级所需积分
            Integer needPoints = upgradePoints - user.getPoints();
            upGradeVO.setNeedPoints(needPoints);
            // 计算当前积分升级所占百分比  累计充值金额/升级所需积分
            double rechargePercent = (double) user.getPoints() * 100 / upgradePoints;
            upGradeVO.setRechargePercent(rechargePercent);
        } else {
            // 已经是最高级会员，无法升级
            upGradeVO.setNextLevel(Status.MEMBER_DIAMOND.code());
            upGradeVO.setUpgradePoints(0);
            upGradeVO.setNeedPoints(0);
            upGradeVO.setRechargePercent(100.0);
        }
        // 账户余额
        upGradeVO.setAccount(user.getAccount());
        // 累计充值金额
        upGradeVO.setPoints(user.getPoints());
        return upGradeVO;
    }

    private void generateAccount(UserDto userDto) {
        // 生成账号
        User user = new User();
        user.setUserId(SysEnum.USER_PREFIX.code() + ID_WORKER.nextId());
        user.setUsername(userDto.getUsername());
        user.setPhone(userDto.getPhone());
        String salty = User.generateSalty();
        // 设置默认密码
        user.setPassword(CommonsUtils.encodeMD5(User.defaultPassword() + salty));
        user.setRegistrationTime(TimeUtil.now());
        user.setSalty(salty);
        user.setStatus(Status.ACCOUNT_OFFLINE.code());
        userMapper.insert(user);
        // 关联商户
        UserMerchant userMerchant = new UserMerchant();
        userMerchant.setUserId(user.getUserId());
        userMerchant.setMerchantId(userDto.getMerchantId());
        userMerchant.setStatus(Status.ACCOUNT_NORMAL.code());
        userMerchantMapper.insert(userMerchant);
        // 关联角色
        UserRoles userRoles = new UserRoles();
        Role roleKey = roleMapper.selectOne(new QueryWrapper<Role>()
                .eq("role_key", userDto.getRoleKey()));
        userRoles.setRoleId(roleKey.getRoleId());
        userRoles.setUserId(user.getUserId());
    }

    /**
     * 代理持久化消息
     *
     * @param message
     * @param receiverId
     */
    public void proxySaveWebSocketMessage(String message, String receiverId) {
        hotelAPIService.saveWebSocketMessage(message, receiverId);
    }
}
