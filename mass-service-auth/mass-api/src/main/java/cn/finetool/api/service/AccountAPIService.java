package cn.finetool.api.service;

import cn.finetool.common.configuration.MultipartSupportConfig;
import cn.finetool.common.po.Evaluation;
import cn.finetool.common.po.User;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**====== 保存评价数据 =====*/
    @PostMapping(value = "/saveEvaluation", consumes = "application/json")
    void saveEvaluation(@RequestBody Evaluation evaluation);

    /**====== 根据用户ID查询用户名 =====*/
    @GetMapping("/findUserInfoyUserId")
    User findUserInfoByUserId(@RequestParam("userId") String userId);
    
    /**====== 通知用户(客户响应呼叫) =====*/
    @PostMapping("/noticeUser")
    void noticeUser(@RequestParam("chatId") String chatId,
                    @RequestParam("requestId")String requestId,
                    @RequestParam("customerId") String customerId,
                    @RequestParam("conductorId") String conductorId);

    /**====== 返回用户账号金额 =====*/
    @PostMapping("/returnAccountBalance")
    void returnAccountBalance(@RequestParam("userId") String userId,
                              @RequestParam("userPayAmount") BigDecimal userPayAmount,
                              @RequestParam("isAdd") boolean isAdd);
    
    /**====== 发放消费券 =====*/
    @PostMapping("/grantConsumeVoucher")
    void grantConsumeVoucher(@RequestParam("userId") String userId,
                             @RequestParam("consumeCount") Integer consumeCount);

    /**====== 赠送积分 =====*/
    @PostMapping("/grantPoints")
    void grantPoints(@RequestParam("userId") String userId,
                     @RequestParam("rewardPoints") Integer rewardPoints);

    /**====== 使用积分 =====*/
    @PostMapping("/usePoints")
    void usePoints(@RequestParam("userId") String userId,@RequestParam("needPoints") Integer needPoints);
}
