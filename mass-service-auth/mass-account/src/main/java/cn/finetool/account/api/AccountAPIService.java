package cn.finetool.account.api;

import cn.finetool.account.service.AccountService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account/api")
public class AccountAPIService {

    @Resource
    private AccountService accountService;
    
    /**====== 查询用户所在商户 =====*/
    @GetMapping("/queryMerchantOfUser")
    String queryMerchantOfUser(@RequestParam("userId") String userId){
        return accountService.queryMerchantOfUser(userId);
    }

    /**====== 查询商户员工编号ID =====*/
    @GetMapping("/findMerchantEmployee")
    List<String> findMerchantEmployee(@RequestParam("merchantId") String merchantId){
        return accountService.findMerchantEmployee(merchantId);
    }

    /**====== 查询商户ID =====*/
    @GetMapping("/findMerchantIdByUserId")
    String findMerchantIdByUserId(@RequestParam("workerId") String workerId){
        return accountService.findMerchantIdByUserId(workerId);
    }
}
