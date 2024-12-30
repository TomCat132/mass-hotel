package cn.finetool.api.service;

import cn.finetool.common.configuration.MultipartSupportConfig;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "mass-account-service", path = "/account/api", configuration = MultipartSupportConfig.class)
public interface AccountAPIService {

    /**====== 查询用户所在商户 =====*/
    @GetMapping("/queryMerchantOfUser")
    String queryMerchantOfUser(@RequestParam("userId") String userId);

    /**====== 查询商户员工编号ID =====*/
    @GetMapping("/findMerchantEmployee")
    List<String> findMerchantEmployee(@RequestParam("merchantId") String merchantId);

    /**====== 查询商户ID =====*/
    @GetMapping("/findMerchantIdByUserId")
    String findMerchantIdByUserId(@RequestParam("workerId") String workerId);

    /**====== 查询用户所在酒店ID =====*/
    @GetMapping("/findHotelIdOfUserId")
    String findMerchantIdOfUserId(@RequestParam("userId") String userId);
}
