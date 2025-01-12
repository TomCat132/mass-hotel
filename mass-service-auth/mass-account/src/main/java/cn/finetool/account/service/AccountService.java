package cn.finetool.account.service;

import cn.finetool.common.dto.EvaluationDto;
import cn.finetool.common.dto.UserDto;
import cn.finetool.common.po.Evaluation;
import cn.finetool.common.po.User;
import cn.finetool.common.util.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.Map;
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
}
