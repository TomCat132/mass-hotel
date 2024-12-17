package cn.finetool.api.service;

import cn.finetool.common.configuration.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "mass-account-service", path = "/account/api", configuration = MultipartSupportConfig.class)
public interface AccountAPIService {

    /**====== 查询用户所在商户 =====*/
    @GetMapping("/queryMerchantOfUser")
    String queryMerchantOfUser(@RequestParam("userId") String userId);
}
