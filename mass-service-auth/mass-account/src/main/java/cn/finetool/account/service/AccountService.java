package cn.finetool.account.service;

public interface AccountService {

    /**====== 查询用户所在商户 =====*/
    String queryMerchantOfUser(String userId);
}
