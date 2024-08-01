package cn.finetool.account.api;

import cn.finetool.account.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/user/api")
public class UserAPIService {

    @Resource
    private UserService userService;

    /** =========== FeignClient: 更新用户数据（充值订单） ============= */
    @PutMapping("/updateUserInfo")
    public void updateUserInfo(@RequestParam("userId") String userId,
                               @RequestParam("totalAmount") BigDecimal totalAmount){
        userService.updateUserInfo(userId, totalAmount);
    }
}
