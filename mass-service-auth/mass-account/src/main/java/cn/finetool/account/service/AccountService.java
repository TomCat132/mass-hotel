package cn.finetool.account.service;

import java.util.List;

public interface AccountService {

    /**====== 查询用户所在商户 =====*/
    String queryMerchantOfUser(String userId);

    /**====== 查询商户员工编号 =====*/
    List<String> findMerchantEmployee(String merchantId);
}
