package cn.finetool.activity.strategy;

import cn.finetool.common.dto.VoucherDto;


public interface SaveVoucherStrategy {

    void save(VoucherDto voucherDto);

    void changeStatus(Integer voucherId, Integer status);

    void decreaseVoucherStock(String voucherId);
}
