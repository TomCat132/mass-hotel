package cn.finetool.account.service;

import cn.finetool.common.po.Role;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RoleService extends IService<Role> {
    Response addRole(Role role);

    List<String> queryAccountRoles(Object loginId, String loginType);
}
