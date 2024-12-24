package cn.finetool.activity.strategy;

import cn.finetool.common.dto.VoucherDto;
import com.fasterxml.jackson.core.JsonProcessingException;


public abstract class SaveVoucherStrategy {

    void save(VoucherDto voucherDto) throws JsonProcessingException {
    }

    void changeStatus(String voucherId, Integer status) {
    }

    boolean decreaseVoucherStock(String voucherId, String userId) {
        return true;
    }

    /**
     * @param voucherId : => affairId
     * @param userId    : 领取优惠券的用户ID
     * @param success
     */
    void sendMessage(String voucherId,String userId, boolean success){
    }
}
