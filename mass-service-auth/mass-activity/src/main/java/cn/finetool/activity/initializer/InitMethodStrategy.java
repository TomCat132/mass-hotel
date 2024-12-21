package cn.finetool.activity.initializer;

import cn.finetool.activity.handler.VoucherHandler;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.enums.VoucherType;
import cn.finetool.common.po.VoucherConsume;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn({"voucherHandler"})
public class InitMethodStrategy {
    public static final Logger logger = LoggerFactory.getLogger(InitMethodStrategy.class);
    @Resource
    private VoucherHandler voucherService;

    // 每月1日00:00:00执行
//    @Scheduled(cron = "0 0 1 * *?")
    @PostConstruct
    public void init() {

        try {
            //获取当月第一天00:00:00 的时间
            LocalDateTime monthStartDay = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            VoucherDto voucherDto = new VoucherDto();
            voucherDto.setVoucherType(VoucherType.CONSUME.getCode());
            VoucherConsume voucherConsume = new VoucherConsume();
            voucherConsume.setBeginTime(monthStartDay);
            voucherDto.setVoucherConsume(voucherConsume);
            voucherService.addVoucher(voucherDto);
            logger.info("初始化{}消费券", monthStartDay);
        } catch (Exception e) {
            logger.warn("初始化消费券失败", e.getMessage());
        }
    }
}
