package cn.finetool.account.service;

import cn.finetool.common.dto.EvaluationDto;
import cn.finetool.common.dto.UserDto;
import cn.finetool.common.po.Evaluation;
import cn.finetool.common.po.Role;
import cn.finetool.common.po.User;
import cn.finetool.common.util.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

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
    /**====== 账户冻结 =====*/
    Response accountCold(String userId);
    /**====== 账户解冻 =====*/
    Response accountUnCold(String userId);
    /**====== 设置账户权限 =====*/
    Response setPermission(String userId, String permission);
    /**====== 删除离职员工信息 =====*/
    Response deleteResignedEmployee(String userId);
    /**====== 新增入职员工信息 =====*/
    Response newEmployeeInfo(UserDto userDto);
    /**====== 保存评价信息 =====*/
    void saveEvaluation(Evaluation evaluation);
    /**====== 用户评价 =====*/
    Response evaluateAfterEnd(EvaluationDto evaluationDto, List<MultipartFile> avatarList);
    /**====== 查询订单评价信息 =====*/
    Response getEvaluateByOrderId(String orderId);
    /**====== 根据关联ID查询评价列表 =====*/
    Response getEvaluateListByRelationId(String relationId);
    /**====== 根据用户ID查询用户名 =====*/
    User findUserInfoUserId(String userId);
    /**====== 通知用户(客户响应呼叫) =====*/
    void noticeUser(String chatId, String requestId, String customerId, String conductorId) throws JsonProcessingException;
    /**====== AP: 账号管理-查询用户信息列表 =====*/
    Response getUserInfoList(Integer page, Integer size, String keyword);
    /**====== AP: 获取权限列表 =====*/
    Response getPolicyList();
    /**====== AP: 新增权限 =====*/
    Response addPolicy(Role role);
    /**====== AP: 权限设置 =====*/
    Response policySetting(String userId, Integer roleId);
    /**====== AP: 删除权限 =====*/
    Response deletePolicy(Integer roleId);
    /**====== AP: 冻结/解冻用户 =====*/
    void changeAccountBalance(String userId, BigDecimal userPayAmount, boolean isAdd);
    /**====== PMS: 用户评价数据列表 =====*/
    Response getEvaluationList(String merchantId, String time, Integer status, String keyword);
    /**====== 发放消费券 =====*/
    void grantConsumeVoucher(String userId, Integer consumeCount);
    /**====== 赠送积分 =====*/
    void grantPoints(String userId, Integer rewardPoints);
    /**====== 查询账号升级相关信息 =====*/
    Response getAccountInfo();
    /**====== C端：获取便捷入口信息" =====*/
    Response getConvenientInfo();
}
