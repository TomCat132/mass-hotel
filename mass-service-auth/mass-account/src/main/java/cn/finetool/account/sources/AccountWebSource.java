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

@RestController
@RequestMapping("/account")
@Api(tags = "账户Web资源接口")
public class AccountWebSource {

    @Resource
    private AccountService accountService;

    @GetMapping("/message-box-list")
    @ApiOperation(value = "获取用户消息列表", notes = "获取用户消息列表")
    public Response getUserMessageBoxList() {
        return accountService.getUserMessageBoxList();
    }

    @GetMapping("/unread-message-count")
    @ApiOperation(value = "获取未读消息数量", notes = "获取未读消息数量")
    public Response getUnreadMessageCount() {
        return accountService.getUnreadMessageCount();
    }

    @PutMapping("/tag-all-message-read")
    @ApiOperation(value = "标记所有消息为已读", notes = "标记所有消息为已读")
    public Response tagAllMessageRead() {
        return accountService.tagAllMessageRead();
    }

    @GetMapping("/get-user-location-info")
    @ApiOperation(value = "获取用户位置信息", notes = "获取用户位置信息")
    public Response getUserLocationInfo() {
        return accountService.getUserLocationInfo();
    }

    @GetMapping("/get-merchant-employee-list")
    @ApiOperation(value = "获取商户员工列表", notes = "获取商户员工列表")
    public Response getMerchantEmployeeList(@RequestParam("merchantId") String merchantId) {
        return accountService.getMerchantEmployeeList(merchantId);
    }

    @PutMapping("/freeze-account")
    @ApiOperation(value = "冻结账户", notes = "冻结账户")
    public Response accountCold(@RequestParam("userId") String userId) {
        return accountService.accountCold(userId);
    }

    @PutMapping("/unfreeze-account")
    @ApiOperation(value = "解冻账户", notes = "解冻账户")
    public Response accountUnCold(@RequestParam("userId") String userId) {
        return accountService.accountUnCold(userId);
    }

    @PostMapping("/set-permission")
    @ApiOperation(value = "设置账户权限", notes = "设置账户权限")
    public Response setPermission(@RequestParam("userId") String userId,
                                  @RequestParam("permission") String permission) {
        return accountService.setPermission(userId, permission);
    }

    @DeleteMapping("/delete-resigned-employee")
    @ApiOperation(value = "删除离职员工信息", notes = "删除离职员工信息")
    public Response deleteResignedEmployee(@RequestParam("userId") String userId) {
        return accountService.deleteResignedEmployee(userId);
    }

    @PostMapping("/new-employee-info")
    @ApiOperation(value = "新增入职员工信息", notes = "新增入职员工信息")
    public Response newEmployeeInfo(@RequestBody UserDto userDto) {
        return accountService.newEmployeeInfo(userDto);
    }

    @PostMapping("/evaluate-after-end")
    @ApiOperation(value = "订单评价", notes = "订单评价")
    public Response evaluateAfterEnd(@RequestPart("evaluationDto") EvaluationDto evaluationDto,
                                     @RequestPart("avatarList") List<MultipartFile> avatarList) {
        return accountService.evaluateAfterEnd(evaluationDto, avatarList);
    }

    @GetMapping("/get-evaluate-by-order-id")
    @ApiOperation(value = "根据订单号获取评价数据", notes = "根据订单号获取评价数据")
    public Response getEvaluateByOrderId(@RequestParam("orderId") String orderId) {
        return accountService.getEvaluateByOrderId(orderId);
    }

    @GetMapping("/get-evaluate-list-by-relation-id")
    @ApiOperation(value = "根据关联ID查询评价列表", notes = "根据关联ID查询评价列表")
    public Response getEvaluateListByRelationId(@RequestParam("relationId") String relationId) {
        return accountService.getEvaluateListByRelationId(relationId);
    }

    @SaCheckRole(value = {"sys_admin"})
    @GetMapping("/user-info-list")
    @ApiOperation(value = "查询用户信息列表", notes = "AP: 账号管理-查询用户信息列表")
    public Response getUserInfoList(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size,
                                    @RequestParam(value = "keyword") String keyword) {
        return accountService.getUserInfoList(page, size, keyword);
    }

    @GetMapping("/get-policy-list")
    @ApiOperation(value = "权限管理", notes = "AP:获取权限列表")
    public Response getPolicyList() {
        return accountService.getPolicyList();
    }
    
    @PostMapping("/add-policy")
    @ApiOperation(value = "新增权限", notes = "AP: 新增权限")
    public Response addPolicy(@RequestBody Role role){
        return accountService.addPolicy(role);
    }
    
    @PostMapping("/edit-policy")
    @ApiOperation(value = "权限设置", notes = "权限设置")
    public Response PolicySetting(@RequestParam("userId") String userId,
                                  @RequestParam("roleId") Integer roleId){
        return accountService.policySetting(userId, roleId);
    }
    
    @PostMapping("/delete-policy")
    @ApiOperation(value = "删除权限", notes = "AP: 删除权限")
    public Response deletePolicy(@RequestParam("roleId") Integer roleId){
        return accountService.deletePolicy(roleId);
    }

    @GetMapping("/get-evaluation-list")
    @ApiOperation(value = "用户评价数据列表", notes = "用户评价数据列表")
    public Response getEvaluationList(@RequestParam("merchantId") String merchantId,
                                      @RequestParam(value = "time", required = false) String time,
                                      @RequestParam(value = "status", required = false) Integer status,
                                      @RequestParam(value = "keyword", required = false) String keyword){
        return accountService.getEvaluationList(merchantId, time, status, keyword);
    }
    
    @SaCheckRole(value = ("user"))
    @GetMapping("/get-account-info")
    @ApiOperation(value = "查询账号会员升级相关信息", notes = "C端: 查询账号升级相关信息")
    public Response getAccountInfo(){
        return accountService.getAccountInfo();
    }
    
    @GetMapping("/get-convenient-info")
    @ApiOperation(value = "获取便捷入口信息", notes = "C端：获取便捷入口信息")
    public Response getConvenientInfo(){
        return accountService.getConvenientInfo();
    }
    

}
