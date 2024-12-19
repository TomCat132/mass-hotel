package cn.finetool.activity.strategy;

import cn.finetool.common.dto.VoucherDto;


public abstract class SaveVoucherStrategy {

    void save(VoucherDto voucherDto) {
    }

    void changeStatus(String voucherId, Integer status) {
    }

    void decreaseVoucherStock(String voucherId) {
    }
}
