package cn.finetool.api.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "mass-account-service", path = "/user/api", contextId = "User")
public interface UserAPIService {

    @PutMapping("/updateUserInfo")
    void updateUserInfo(@RequestParam("userId") String userId,
                        @RequestParam("totalAmount") BigDecimal totalAmount);
}
