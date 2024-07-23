package cn.finetool.account.service.impl;

import cn.finetool.account.mapper.UserRolesMapper;
import cn.finetool.account.service.UserRolesService;
import cn.finetool.common.po.UserRoles;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserRolesServiceImpl extends ServiceImpl<UserRolesMapper, UserRoles> implements UserRolesService {

    @Resource
    private UserRolesService userRolesService;

    @Override
    public boolean saveUserRoles(Integer roleId, String userId) {
        // 先检查是否含有相同 角色
        UserRoles one = userRolesService.getOne(new LambdaQueryWrapper<UserRoles>()
                .eq(UserRoles::getUserId, userId)
                .eq(UserRoles::getRoleId, roleId));
        if (one == null) {
            UserRoles userRoles = new UserRoles();
            userRoles.setRoleId(roleId);
            userRoles.setUserId(userId);
            save(userRoles);
            return true;
        }
        return false;
    }
}
