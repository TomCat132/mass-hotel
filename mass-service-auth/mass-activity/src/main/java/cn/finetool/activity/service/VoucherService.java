package cn.finetool.activity.service;

import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.po.Voucher;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface VoucherService extends IService<Voucher> {

    Response addVoucher(VoucherDto voucherDto) throws JsonProcessingException;
    
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

    /**
     * 获取有效的活动券列表
     */
    Response getValidVoucherList();

    /**
     * 根据活动券编号获取活动券基本信息
     * @param voucherId
     */
    VoucherDto getVoucherBaseInfo(String voucherId);

    /**
     * 更新活动券状态为：已使用
     * @param voucherId 活动券编号
     * @param status 装填
     */
    void usedVoucher(String voucherId, Integer status);

    /**
     * 活动券编号
     * @param voucherId 活动券编号
     */
    Response deleteVoucherByVoucherId(String voucherId);
}
