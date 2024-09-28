package cn.finetool.activity.contoller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.finetool.activity.service.VoucherService;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.util.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/voucher")
@Api(tags = "优惠券管理")
public class VoucherController {

    @Resource
    private VoucherService voucherService;

    @SaCheckRole(value = {"admin","super_admin"},mode = SaMode.OR)
    @PostMapping("/admin/add")
    @ApiOperation(value = "添加活动券", notes = "添加活动券")
    public Response addVoucher(@RequestBody VoucherDto voucherDto){
        return voucherService.addVoucher(voucherDto);
    }
}
