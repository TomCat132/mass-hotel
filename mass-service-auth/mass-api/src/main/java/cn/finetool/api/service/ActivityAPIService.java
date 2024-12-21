package cn.finetool.api.service;


import cn.finetool.common.configuration.MultipartSupportConfig;
import cn.finetool.common.util.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "mass-activity-service", path = "/activity/api", configuration = MultipartSupportConfig.class)
public interface ActivityAPIService {

    /**
     * @param voucherId 活动券编号
     * @param userId    用户编号
     * @return
     */
    @PostMapping("/getVoucher")
    Response getVoucher(@RequestParam("voucherId") String voucherId,
                        @RequestParam("userId") String userId);

    /**
     * @param voucherType 类型
     * @param voucherId   活动券编号
     * @param status      状态
     * @return boolean: true false
     */
    @PostMapping("/updateVoucherStatus")
    void updateVoucherStatus(@RequestParam("voucherType") Integer voucherType,
                                @RequestParam("voucherId") String voucherId,
                                @RequestParam("status") Integer status);
}
