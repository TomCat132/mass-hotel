package cn.finetool.account.service;

import cn.finetool.common.po.UserRoles;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserRolesService extends IService<UserRoles> {

    /** ======  添加用户角色 ====== */
    boolean saveUserRoles(Integer roleId, String userId);
}
