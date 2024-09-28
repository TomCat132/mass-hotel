package cn.finetool.activity.service.impl;

import cn.finetool.activity.mapper.VoucherMapper;
import cn.finetool.activity.service.VoucherService;
import cn.finetool.activity.strategy.SaveVoucherContext;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.po.Voucher;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {

    @Resource
    private static final SnowflakeIdWorker workerID = new SnowflakeIdWorker(0,0);

    @Resource
    private SaveVoucherContext saveVoucherContext;

    @Override
    public Response addVoucher(VoucherDto voucherDto) {

        String voucherId = String.valueOf(workerID.nextId());

        LocalDateTime nowTime = LocalDateTime.now();

        Voucher voucher = new Voucher();
        voucher.setVoucherId(voucherId);
        voucher.setCreateTime(nowTime);
        voucher.setVoucherType(voucherDto.getVoucherType());
        // 根据不同的活动券类型加入不同的表中
        saveVoucherContext.saveVoucher(voucherDto);

        return null;
    }
}
