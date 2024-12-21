package cn.finetool.activity.service;

import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.po.Voucher;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface VoucherService extends IService<Voucher> {

    Response addVoucher(VoucherDto voucherDto) throws JsonProcessingException;

    Response grantVoucher(String voucherId);

    /**
     * 获取所有类型的活动券列表
     *
     * @param merchantId 商户编号
     * @return
     */
    Response getAllCategoryVoucherList(String merchantId);

    /**
     * @param voucherType 类型
     * @param voucherId   活动券编号
     * @param status      状态
     */
    void updateVoucherStatus(Integer voucherType, String voucherId, Integer status);
}
