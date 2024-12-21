package cn.finetool.activity.api;

import cn.finetool.activity.service.UserVoucherService;
import cn.finetool.activity.service.VoucherService;
import cn.finetool.common.util.Response;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activity/api")
public class ActivityAPIService {

    @Resource
    private UserVoucherService userVoucherService;
    @Resource
    private VoucherService voucherService;

    /** ============= 领取优惠券   ========== */
    @PostMapping("/getVoucher")
    public Response getVoucher(@RequestParam("voucherId") String voucherId,
                               @RequestParam("userId") String userId){
       return userVoucherService.getVoucher(voucherId, userId);
    }

    /**
     * @param voucherType 类型
     * @param voucherId   活动券编号
     * @param status      状态
     * @return boolean: true false
     */
    @PostMapping("/updateVoucherStatus")
    void updateVoucherStatus(@RequestParam("voucherType") Integer voucherType,
                                @RequestParam("voucherId") String voucherId,
                                @RequestParam("status") Integer status){
        voucherService.updateVoucherStatus(voucherType, voucherId, status);
    }
}
