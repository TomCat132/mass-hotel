package cn.finetool.account.handler;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.account.mapper.RoleMapper;
import cn.finetool.account.mapper.UserMapper;
import cn.finetool.account.mapper.UserMerchantMapper;
import cn.finetool.account.mapper.UserRolesMapper;
import cn.finetool.account.service.AccountService;
import cn.finetool.api.mapper.MessageBoxMapper;
import cn.finetool.api.service.AccountAPIService;
import cn.finetool.common.enums.Status;
import cn.finetool.common.po.MessageBox;
import cn.finetool.common.po.Role;
import cn.finetool.common.po.User;
import cn.finetool.common.po.UserMerchant;
import cn.finetool.common.po.UserRoles;
import cn.finetool.common.vo.UserVO;
import cn.finetool.common.util.IpUtil;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static cn.finetool.common.util.Response.success;

@Component
public class AccountHandler implements AccountService {
    
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

    @Override
    public String queryMerchantOfUser(String userId) {
        UserMerchant userMerchant = userMerchantMapper.selectOne(new QueryWrapper<UserMerchant>()
                .eq("user_id", userId));
        if (Objects.isNull(userMerchant)){
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
        if (CollectionUtils.isNotEmpty(messageBoxList)){
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
                        .in("id", roleIds))
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
                    return userVO;
                })
                .collect(Collectors.toList());
        return success(userVOList);
    }


}
