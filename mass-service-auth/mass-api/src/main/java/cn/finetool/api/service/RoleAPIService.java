package cn.finetool.api.service;

import cn.finetool.common.configuration.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "mass-account-service", path = "/role/api" ,configuration = MultipartSupportConfig.class)
public interface RoleAPIService {

    /**
     * 查询账户角色
     * @param loginId
     * @param loginType
     * @return
     */
    @RequestMapping("/queryAccountRoles")
    List<String> queryAccountRoles(@RequestParam("loginId") Object loginId, @RequestParam("loginType") String loginType);
}
