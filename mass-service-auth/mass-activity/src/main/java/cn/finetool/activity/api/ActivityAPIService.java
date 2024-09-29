package cn.finetool.activity.api;

import cn.finetool.activity.service.UserVoucherService;
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

    /** ============= 领取优惠券   ========== */
    @PostMapping("/getVoucher")
    public Response getVoucher(@RequestParam("voucherId") String voucherId,
                               @RequestParam("userId") String userId){
       return userVoucherService.getVoucher(voucherId, userId);
    }
}
