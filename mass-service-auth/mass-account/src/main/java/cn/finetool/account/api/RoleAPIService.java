package cn.finetool.account.api;

import cn.finetool.account.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/role/api")
public class RoleAPIService {

    @Resource
    private RoleService roleService;

    /** =========== FeignClient: 查询用户角色 ============= */
    @RequestMapping("/queryAccountRoles")
    List<String> queryAccountRoles(@RequestParam("loginId") Object loginId, @RequestParam("loginType") String loginType){
        return roleService.queryAccountRoles(loginId, loginType);
    }
}
