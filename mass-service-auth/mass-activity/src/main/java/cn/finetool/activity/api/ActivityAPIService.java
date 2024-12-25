package cn.finetool.activity.api;

import cn.finetool.activity.service.UserVoucherService;
import cn.finetool.activity.service.VoucherService;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.util.Response;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    /**
     * 根据 VoucherId 查询活动券相关信息
     * @param voucherId
     * @return
     */
    @GetMapping("/getVoucherBaseInfo")
    VoucherDto getVoucherBaseInfo(@RequestParam("voucherId") String voucherId){
        return voucherService.getVoucherBaseInfo(voucherId);
    }
    
    /**
     * 更新活动券状态为：已使用
     * @param voucherId
     * @param status
     */
    @PutMapping("/usedVoucher")
    void usedVoucher(@RequestParam("voucherId") String voucherId,
                     @RequestParam("status") Integer status){
         voucherService.usedVoucher(voucherId, status);
    }
    
}
