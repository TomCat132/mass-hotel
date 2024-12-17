package cn.finetool.activity.strategy;

import cn.finetool.activity.mapper.VoucherConsumeMapper;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.VoucherConsume;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

/**
 * 消费券
 */
@Component
public class VoucherConsumeStrategy extends SaveVoucherStrategy {

    public static final Logger Logger = LoggerFactory.getLogger(VoucherConsumeStrategy.class);
    @Resource
    private VoucherConsumeMapper voucherConsumeMapper;

    @Override
    public void save(VoucherDto voucherDto) {
        if (Objects.nonNull(voucherDto.getVoucherConsume())) {
            // 截取当年当月时间与数据库比对,变重复插入数据
            LocalDateTime beginTime = voucherDto.getVoucherConsume().getBeginTime();
            // 截取当月第一天
            beginTime = LocalDateTime.of(beginTime.getYear(), beginTime.getMonth(), 1, 0, 0, 0);
            VoucherConsume data = voucherConsumeMapper.selectOne(new QueryWrapper<VoucherConsume>()
                    .eq("begin_time", beginTime));
            if (Objects.nonNull(data)) {
                Logger.info("{} 已经发放系统券，禁止重复发放", beginTime);

                throw new BusinessRuntimeException(beginTime.format(DateTimeFormatter.ofPattern("yyyy-MM")) + "已经发放系统券，禁止重复发放");
            }
            voucherDto.getVoucherConsume().setVoucherId(voucherDto.getVoucherId());
            voucherConsumeMapper.insert(voucherDto.getVoucherConsume());
        }
    }
}
