package cn.finetool.api.service;


import cn.finetool.common.configuration.MultipartSupportConfig;
import cn.finetool.common.util.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "mass-activity-service", path = "/activity/api",configuration = MultipartSupportConfig.class)
public interface ActivityAPIService {

    @PostMapping("/getVoucher")
    Response getVoucher(@RequestParam("voucherId") String voucherId,
                        @RequestParam("userId") String userId);
}
