package cn.finetool.account.api;

import cn.finetool.account.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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

    /** =========== FeignClient: 获取账户余额 ============= */
    @GetMapping("/getUserAccount")
    public BigDecimal getUserAccount(@RequestParam("userId") String userId){
       return userService.getUserAccount(userId);
    }
    /** =========== FeignClient: 扣除账户余额 ============= */
    @PostMapping("/increaseUserAccount")
    public void decreaseUserAccount(@RequestParam("userId") String userId,
                                    @RequestParam("userPayAmount") BigDecimal userPayAmount){
        userService.decreaseUserAccount(userId, userPayAmount);
    }
}
