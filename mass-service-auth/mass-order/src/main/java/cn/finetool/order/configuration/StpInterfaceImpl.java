package cn.finetool.order.configuration;


import cn.dev33.satoken.stp.StpInterface;
import cn.finetool.api.service.RoleAPIService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限加载接口实现类
 */
@Slf4j
@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private RoleAPIService roleAPIService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {

        log.info("获取权限列表");
        List<String> permisstionList = new ArrayList<>();
        permisstionList.add("user:read");

        return permisstionList;
    }


    @Override
    public List<String> getRoleList(Object loginId, String loginType) {

        log.info("获取角色列表");
        List<String> roleList = new ArrayList<>();

        roleList = roleAPIService.queryAccountRoles(loginId, loginType);
        log.info("本次登录用户所拥有的角色:{}",roleList.toArray());
        return roleList;
    }
}
