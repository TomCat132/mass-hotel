package cn.finetool.account.api;

import cn.finetool.account.service.AccountService;
import cn.finetool.common.po.Evaluation;
import cn.finetool.common.po.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**====== 查询用户所在酒店ID =====*/
    @GetMapping("/findHotelIdOfUserId")
    String findHotelIdOfUserId(@RequestParam("userId") String userId){
        return accountService.findMerchantIdOfUserId(userId);
    }

    /**====== 保存评价数据 =====*/
    @PostMapping(value = "/saveEvaluation", consumes = "application/json")
    void saveEvaluation(@RequestBody Evaluation evaluation){
         accountService.saveEvaluation(evaluation);
    }

    /**====== 根据用户ID查询用户信息 =====*/
    @GetMapping("/findUserInfoyUserId")
    User findUserInfoByUserId(@RequestParam("userId") String userId){
        return accountService.findUserInfoUserId(userId);
    }

    /**====== 通知用户(客户响应呼叫) =====*/
    @PostMapping("/noticeUser")
    void noticeUser(@RequestParam("chatId") String chatId,
                    @RequestParam("requestId")String requestId,
                    @RequestParam("customerId") String customerId,
                    @RequestParam("conductorId") String conductorId) throws JsonProcessingException {
        accountService.noticeUser(chatId, requestId, customerId, conductorId);
    }

    /**====== 返回用户账号金额 =====*/
    @PostMapping("/returnAccountBalance")
    void returnAccountBalance(@RequestParam("userId") String userId,
                              @RequestParam("userPayAmount") BigDecimal userPayAmount,
                              @RequestParam("isAdd") boolean isAdd){
        accountService.changeAccountBalance(userId, userPayAmount, isAdd);
    }

    /**====== 发放消费券 =====*/
    @PostMapping("/grantConsumeVoucher")
    void grantConsumeVoucher(@RequestParam("userId") String userId,
                             @RequestParam("consumeCount") Integer consumeCount){
        accountService.grantConsumeVoucher(userId, consumeCount);
    }

    /**====== 赠送积分 =====*/
    @PostMapping("/grantPoints")
    void grantPoints(@RequestParam("userId") String userId,
                     @RequestParam("rewardPoints") Integer rewardPoints){
        accountService.grantPoints(userId, rewardPoints);
    }


}
