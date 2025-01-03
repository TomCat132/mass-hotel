package cn.finetool.account.service;

import cn.finetool.common.util.Response;
import java.util.List;
import java.util.Map;

public interface AccountService {

    /**====== 查询用户所在商户 =====*/
    String queryMerchantOfUser(String userId);
    /**====== 查询商户员工编号 =====*/
    List<String> findMerchantEmployee(String merchantId);
    /**====== 查询用户消息列表 =====*/
    Response getUserMessageBoxList();
    /**====== 获取未读消息数量 =====*/
    Response getUnreadMessageCount();
    /**====== 标记所有消息为已读 =====*/
    Response tagAllMessageRead();
    /**====== 查询商户ID =====*/
    String findMerchantIdByUserId(String workerId);
    /**====== 查询用户位置信息 =====*/
    Response getUserLocationInfo();
    /**====== 查询用户所在酒店ID =====*/
    String findMerchantIdOfUserId(String userId);
    /**====== 查询商户员工列表 =====*/
    Response getMerchantEmployeeList(String merchantId);

}
