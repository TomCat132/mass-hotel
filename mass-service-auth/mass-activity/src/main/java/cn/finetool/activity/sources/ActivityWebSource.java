package cn.finetool.activity.sources;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.finetool.activity.service.VoucherService;
import cn.finetool.common.po.SignReward;
import cn.finetool.common.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/activity")
@Api(value = "活动Web资源接口")
public class ActivityWebSource {

    @Resource
    private VoucherService voucherService;

    @SaCheckRole(value = {"admin", "super_admin"}, mode = SaMode.OR)
    @GetMapping("/categoryVoucherList")
    @ApiOperation(value = "获取所有类型的活动券列表", notes = "AP: 获取所有分类的优惠券列表")
    public Response getAllCategoryVoucherList(@RequestParam(value = "merchantId", required = false) String merchantId) {
        return voucherService.getAllCategoryVoucherList(merchantId);
    }
    
    @GetMapping("/platformVoucherList")
    @ApiOperation(value = "获取所有平台的活动券列表", notes = "AP: 获取所有平台的活动券列表")
    public Response getPlatFormVoucherList(){
        return voucherService.getPlatFormVoucherList();
    }
    

    @GetMapping("/voucher-list")
    @ApiOperation(value = "获取有效的所有活动券列表", notes = "PMS: 获取有效的所有活动券列表")
    public Response getValidVoucherList(){
        return voucherService.getValidVoucherList();
    }

    /**
     * @param voucherId 活动券编号
     */
    @DeleteMapping("/voucher")
    @ApiOperation(value = "删除活动券", notes = "PMS: 删除活动券")
    public Response deleteVoucherByVoucherId(@RequestParam("voucherId") String voucherId){
        return voucherService.deleteVoucherByVoucherId(voucherId);
    }
    
    @PostMapping("/sign-reward")
    @ApiOperation(value = "设置签到奖励", notes = "AP: 设置签到奖励")
    public Response signRewardSetting(@RequestBody SignReward signReward){
        return voucherService.signRewardSetting(signReward);
    }
    
    @PostMapping("/change-reward-content")
    @ApiOperation(value = "根据ID更改奖励内容", notes = "AP: 根据ID更改奖励内容")
    public Response changeRewardContent(@RequestParam("rewardId") String rewardId,
                                        @RequestParam("content") String content){
        return voucherService.changeRewardContent(rewardId, content);
    }
    
    @GetMapping("/reward-content-list")
    @ApiOperation(value = "获取奖励内容列表", notes = "AP: 按月获取签到奖励内容列表")
    public Response getRewardContentListByMonth(@RequestParam("date") String date){
        return voucherService.getRewardContentListByMonth(date);
    }
    
    @GetMapping("/time-limited-activities")
    @ApiOperation(value = "获取限时活动列表", notes = "C端: 获取限时活动列表")
    public Response getTimeLimitedActivities(){
        return voucherService.getTimeLimitedActivities();
    }

    @GetMapping("/activity-info")
    @ApiOperation(value = "获取活动详情", notes = "C端: 获取活动详情")
    public Response getActivityInfo(@RequestParam("activityId") String activityId){
        return voucherService.getActivityInfo(activityId);
    }
    
    @GetMapping("/is-received-voucher")
    @ApiOperation(value = "判断用户是否已领取活动券", notes = "C端: 判断用户是否已领取活动券")
    public Response isReceivedVoucher(@RequestParam("activityId") String activityId){
        return voucherService.isReceivedVoucher(activityId);
    }
    
   
}
