package cn.finetool.api.service;

import cn.finetool.common.configuration.MultipartSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "mass-account-service", path = "/user/api", configuration = MultipartSupportConfig.class)
public interface UserAPIService {

    @PutMapping("/updateUserInfo")
    void updateUserInfo(@RequestParam("userId") String userId,
                        @RequestParam("totalAmount") BigDecimal totalAmount);

    @GetMapping("/getUserAccount")
    BigDecimal getUserAccount(@RequestParam("userId") String userId);

    @PostMapping("/increaseUserAccount")
    void decreaseUserAccount(@RequestParam("userId") String userId,
                             @RequestParam("userPayAmount") BigDecimal userPayAmount);
}
