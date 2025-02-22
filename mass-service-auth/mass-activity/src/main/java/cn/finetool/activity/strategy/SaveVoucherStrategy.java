package cn.finetool.activity.strategy;

import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.po.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.TreeSet;


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

    public void deleteVoucher(String voucherId) {
    }
}
