package cn.finetool.activity.strategy;

import cn.finetool.common.dto.VoucherDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.LinkedHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class VoucherOperationContext {

    private final Map<Integer, SaveVoucherStrategy> strategies = new LinkedHashMap<>();

    @Resource
    private CouponStrategy couponStrategy;
    @Resource
    private VoucherSystemStrategy voucherSystemStrategy;
    @Resource
    private VoucherConsumeStrategy voucherConsumeStrategy;

    @PostConstruct
    public void addStrategy() {
        log.info("初始化 活动券保存策略");
        strategies.put(0, couponStrategy);
        strategies.put(1, voucherSystemStrategy);
        strategies.put(2, voucherConsumeStrategy);
        log.info("活动券保存策略初始化完成");
    }

    public void saveVoucher(VoucherDto voucherDto) throws JsonProcessingException {
        SaveVoucherStrategy strategy = strategies.get(voucherDto.getVoucherType());
        strategy.save(voucherDto);
    }

    public void changStatus(Integer voucherType, String voucherId, Integer status) {
        SaveVoucherStrategy strategy = strategies.get(voucherType);
        strategy.changeStatus(voucherId, status);
    }

    public boolean decreaseVoucherStock(Integer voucherType, String voucherId, String userId) {
        SaveVoucherStrategy strategy = strategies.get(voucherType);
        return strategy.decreaseVoucherStock(voucherId, userId);
    }

    public void deleteVoucher(String voucherId, Integer voucherType) {
        SaveVoucherStrategy strategy = strategies.get(voucherType);
        strategy.deleteVoucher(voucherId);
    }
}
