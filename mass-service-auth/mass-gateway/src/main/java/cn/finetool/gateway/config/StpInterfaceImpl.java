package cn.finetool.gateway.config;

import cn.dev33.satoken.stp.StpInterface;



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


    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {


        log.info("获取权限列表");


        return new ArrayList<>();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        log.info("获取角色列表");


        return new ArrayList<>();
    }
}
