package cn.finetool.activity.sources;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.finetool.activity.service.VoucherService;
import cn.finetool.common.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
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
    @ApiOperation(value = "获取所有类型的活动券列表", notes = "PMS: 获取所有分类的优惠券列表")
    public Response getAllCategoryVoucherList(@RequestParam("merchantId") String merchantId) {
        return voucherService.getAllCategoryVoucherList(merchantId);
    }
}
