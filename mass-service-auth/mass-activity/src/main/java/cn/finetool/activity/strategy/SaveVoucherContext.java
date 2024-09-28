package cn.finetool.activity.strategy;

import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.enums.Status;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SaveVoucherContext {

    private static final Map<Integer,SaveVoucherStrategy> strategies = new HashMap<>();

    @Resource
    private CouponStrategy couponStrategy;

    @PostConstruct
    public void addStrategy(){
        strategies.put(0, couponStrategy);
    }

    public void saveVoucher(VoucherDto voucherDto){
        SaveVoucherStrategy strategy = strategies.get(voucherDto.getVoucherType());
        strategy.save(voucherDto);
    }

    public void changStatus(Integer voucherType, Integer voucherId, Integer status) {
        SaveVoucherStrategy strategy = strategies.get(voucherType);
        strategy.changeStatus(voucherId,status);
    }

    public void decreaseVoucherStock(Integer voucherType, String voucherId) {
        SaveVoucherStrategy strategy = strategies.get(voucherType);
        strategy.decreaseVoucherStock(voucherId);
    }
}
