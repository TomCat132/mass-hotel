package cn.finetool.account.service.impl;

import cn.finetool.account.mapper.RoleMapper;
import cn.finetool.account.mapper.UserRolesMapper;
import cn.finetool.account.service.RoleService;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.exception.BusinessRuntimeException;

import cn.finetool.common.po.Role;
import cn.finetool.common.po.UserRoles;

import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private RoleService roleService;

    @Resource
    private UserRolesMapper userRolesMapper;

    @Resource
    private RoleMapper roleMapper;

    @Override
    public Response addRole(Role role) {

        Role existedRole = roleService.getOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleName, role.getRoleName())
                .or()
                .eq(Role::getRoleKey, role.getRoleKey()));
        if (existedRole!= null) {
            throw new BusinessRuntimeException(BusinessErrors.DATA_DUPLICATION, "角色名称或角色标识重复");
        }
        save(role);
        return Response.success(role);
    }

    @Override
    public List<String> queryAccountRoles(Object loginId, String loginType) {
        List<String> roleList = new ArrayList<>();

        List<UserRoles> userRolesList = userRolesMapper.selectList(new LambdaQueryWrapper<UserRoles>()
                .eq(UserRoles::getUserId, loginId));

        userRolesList.forEach(userRoles -> {
            Role roleInfo = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                    .eq(Role::getRoleId, userRoles.getRoleId()));
            roleList.add(roleInfo.getRoleKey());
        });
        return roleList;
    }
}
