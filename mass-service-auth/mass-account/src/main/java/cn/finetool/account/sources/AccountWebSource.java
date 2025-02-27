package cn.finetool.account.sources;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.finetool.account.service.AccountService;
import cn.finetool.common.dto.EvaluationDto;
import cn.finetool.common.dto.UserDto;
import cn.finetool.common.po.Role;
import cn.finetool.common.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static cn.finetool.common.util.Response.success;

@RestController
@RequestMapping("/account")
@Api(tags = "账户Web资源接口")
public class AccountWebSource {

    private static final Logger Logger = LoggerFactory.getLogger(AccountWebSource.class);
    @Resource
    private AccountService accountService;

    @GetMapping("/message-box-list")
    @ApiOperation(value = "获取用户消息列表", notes = "获取用户消息列表")
    public Response getUserMessageBoxList() {
        try {
            return success(accountService.getUserMessageBoxList());
        } catch (Exception e) {
            Logger.error("/account/message-box-list \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @GetMapping("/unread-message-count")
    @ApiOperation(value = "获取未读消息数量", notes = "获取未读消息数量")
    public Response getUnreadMessageCount() {
        try {
            return success(accountService.getUnreadMessageCount());
        } catch (Exception e) {
            Logger.error("/account/unread-message-count \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @PutMapping("/tag-all-message-read")
    @ApiOperation(value = "标记所有消息为已读", notes = "标记所有消息为已读")
    public Response tagAllMessageRead() {
        try {
            accountService.tagAllMessageRead();
            return success("标记所有消息为已读成功");
        } catch (Exception e) {
            Logger.error("/account/tag-all-message-read \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @GetMapping("/get-user-location-info")
    @ApiOperation(value = "获取用户位置信息", notes = "获取用户位置信息")
    public Response getUserLocationInfo() {
        try {
            return success(accountService.getUserLocationInfo());
        } catch (Exception e) {
            Logger.error("/account/get-user-location-info \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @GetMapping("/get-merchant-employee-list")
    @ApiOperation(value = "获取商户员工列表", notes = "获取商户员工列表")
    public Response getMerchantEmployeeList(@RequestParam("merchantId") String merchantId) {
        try {
            return success(accountService.getMerchantEmployeeList(merchantId));
        } catch (Exception e) {
            Logger.error("/account/get-merchant-employee-list \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @PutMapping("/freeze-account")
    @ApiOperation(value = "冻结账户", notes = "冻结账户")
    public Response accountCold(@RequestParam("userId") String userId) {
        try {
            accountService.accountCold(userId);
            return success("账号已冻结");
        } catch (Exception e) {
            Logger.error("/account/freeze-account \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @PutMapping("/unfreeze-account")
    @ApiOperation(value = "解冻账户", notes = "解冻账户")
    public Response accountUnCold(@RequestParam("userId") String userId) {
        try {
            accountService.accountUnCold(userId);
            return success("账号已解冻");
        } catch (Exception e) {
            Logger.error("/account/unfreeze-account \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @PostMapping("/set-permission")
    @ApiOperation(value = "设置账户权限", notes = "设置账户权限")
    public Response setPermission(@RequestParam("userId") String userId,
                                  @RequestParam("permission") String permission) {
        try {
            accountService.setPermission(userId, permission);
            return success("权限已变更");
        } catch (Exception e) {
            Logger.error("/account/set-permission \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete-resigned-employee")
    @ApiOperation(value = "删除离职员工信息", notes = "删除离职员工信息")
    public Response deleteResignedEmployee(@RequestParam("userId") String userId) {
        try {
            accountService.deleteResignedEmployee(userId);
            return success("员工账号已删除");
        } catch (Exception e) {
            Logger.error("/account/delete-resigned-employee \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @PostMapping("/new-employee-info")
    @ApiOperation(value = "新增入职员工信息", notes = "新增入职员工信息")
    public Response newEmployeeInfo(@RequestBody UserDto userDto) {
        try {
            accountService.newEmployeeInfo(userDto);
            return success("账号已发放");
        } catch (Exception e) {
            Logger.error("/account/new-employee-info \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @PostMapping("/evaluate-after-end")
    @ApiOperation(value = "订单评价", notes = "订单评价")
    public Response evaluateAfterEnd(@RequestPart("evaluationDto") EvaluationDto evaluationDto,
                                     @RequestPart("avatarList") List<MultipartFile> avatarList) {
        try {
            accountService.evaluateAfterEnd(evaluationDto, avatarList);
            return success("感谢您的评价，我们会努力提供更好的服务");
        } catch (Exception e) {
            Logger.error("/account/evaluate-after-end \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @GetMapping("/get-evaluate-by-order-id")
    @ApiOperation(value = "根据订单号获取评价数据", notes = "根据订单号获取评价数据")
    public Response getEvaluateByOrderId(@RequestParam("orderId") String orderId) {
        try {
            return success(accountService.getEvaluateByOrderId(orderId));
        } catch (Exception e) {
            Logger.error("/account/get-evaluate-by-order-id \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @GetMapping("/get-evaluate-list-by-relation-id")
    @ApiOperation(value = "根据关联ID查询评价列表", notes = "根据关联ID查询评价列表")
    public Response getEvaluateListByRelationId(@RequestParam("relationId") String relationId) {
        try {
            return success(accountService.getEvaluateListByRelationId(relationId));
        } catch (Exception e) {
            Logger.error("/account/get-evaluate-list-by-relation-id \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @SaCheckRole(value = {"sys_admin"})
    @GetMapping("/user-info-list")
    @ApiOperation(value = "查询用户信息列表", notes = "AP: 账号管理-查询用户信息列表")
    public Response getUserInfoList(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size,
                                    @RequestParam(value = "keyword") String keyword) {
        try {
            return success(accountService.getUserInfoList(page, size, keyword));
        } catch (Exception e) {
            Logger.error("/account/user-info-list \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @GetMapping("/get-policy-list")
    @ApiOperation(value = "权限管理", notes = "AP:获取权限列表")
    public Response getPolicyList() {
        try {
            return success(accountService.getPolicyList());
        } catch (Exception e) {
            Logger.error("/account/get-policy-list \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @PostMapping("/add-policy")
    @ApiOperation(value = "新增权限", notes = "AP: 新增权限")
    public Response addPolicy(@RequestBody Role role) {
        try {
            accountService.addPolicy(role);
            return success("权限添加成功");
        } catch (Exception e) {
            Logger.error("/account/add-policy \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @PostMapping("/edit-policy")
    @ApiOperation(value = "权限设置", notes = "权限设置")
    public Response PolicySetting(@RequestParam("userId") String userId,
                                  @RequestParam("roleId") Integer roleId) {
        try {
            accountService.policySetting(userId, roleId);
            return success("权限设置成功");
        } catch (Exception e) {
            Logger.error("/account/edit-policy \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @PostMapping("/delete-policy")
    @ApiOperation(value = "删除权限", notes = "AP: 删除权限")
    public Response deletePolicy(@RequestParam("roleId") Integer roleId) {
        try {
            accountService.deletePolicy(roleId);
            return success("删除成功");
        } catch (Exception e) {
            Logger.error("/account/delete-policy \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @GetMapping("/get-evaluation-list")
    @ApiOperation(value = "用户评价数据列表", notes = "用户评价数据列表")
    public Response getEvaluationList(@RequestParam("merchantId") String merchantId,
                                      @RequestParam(value = "time", required = false) String time,
                                      @RequestParam(value = "status", required = false) Integer status,
                                      @RequestParam(value = "keyword", required = false) String keyword) {
        try {
            return success(accountService.getEvaluationList(merchantId, time, status, keyword));
        } catch (Exception e) {
            Logger.error("/account/get-evaluation-list \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @SaCheckRole(value = ("user"))
    @GetMapping("/get-account-info")
    @ApiOperation(value = "查询账号会员升级相关信息", notes = "C端: 查询账号升级相关信息")
    public Response getAccountInfo() {
        try {
            return success(accountService.getAccountInfo());
        } catch (Exception e) {
            Logger.error("/account/get-account-info \n{}", e);
            return Response.error(e.getMessage());
        }
    }

    @GetMapping("/get-convenient-info")
    @ApiOperation(value = "获取便捷入口信息", notes = "C端：获取便捷入口信息")
    public Response getConvenientInfo() {
        try {
            return success(accountService.getConvenientInfo());
        } catch (Exception e) {
            Logger.error("/account/get-convenient-info \n{}", e);
            return Response.error(e.getMessage());
        }
    }


}
