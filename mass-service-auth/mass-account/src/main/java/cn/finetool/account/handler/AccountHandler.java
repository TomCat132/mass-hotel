package cn.finetool.account.handler;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.account.mapper.EvaluationMapper;
import cn.finetool.account.mapper.RoleMapper;
import cn.finetool.account.mapper.UserMapper;
import cn.finetool.account.mapper.UserMerchantMapper;
import cn.finetool.account.mapper.UserRolesMapper;
import cn.finetool.account.service.AccountService;
import cn.finetool.api.mapper.MessageBoxMapper;
import cn.finetool.api.service.OrderAPIService;
import cn.finetool.api.service.OssAPIService;
import cn.finetool.common.dto.EvaluationDto;
import cn.finetool.common.dto.UserDto;
import cn.finetool.common.enums.Status;
import cn.finetool.common.enums.SysEnum;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.Evaluation;
import cn.finetool.common.po.MessageBox;
import cn.finetool.common.po.Role;
import cn.finetool.common.po.User;
import cn.finetool.common.po.UserMerchant;
import cn.finetool.common.po.UserRoles;
import cn.finetool.common.util.CommonsUtils;
import cn.finetool.common.util.SnowflakeIdWorker;
import cn.finetool.common.util.Strings;
import cn.finetool.common.util.TimeUtil;
import cn.finetool.common.vo.UserVO;
import cn.finetool.common.util.IpUtil;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
        if (CollectionUtils.isNotEmpty(avatarList)){
            ossAPIService.batchUploadImage(avatarList, evaluationDto.getEvaluationId());
        }
        return success("感谢您的评价，我们会努力提供更好的服务");
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


}
