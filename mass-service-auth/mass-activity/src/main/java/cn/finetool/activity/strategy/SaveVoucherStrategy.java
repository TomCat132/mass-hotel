package cn.finetool.activity.strategy;

import cn.finetool.common.dto.VoucherDto;
import com.fasterxml.jackson.core.JsonProcessingException;


public abstract class SaveVoucherStrategy {

    void save(VoucherDto voucherDto) throws JsonProcessingException {
    }

    void changeStatus(String voucherId, Integer status) {
    }

    void decreaseVoucherStock(String voucherId) {
    }
}
