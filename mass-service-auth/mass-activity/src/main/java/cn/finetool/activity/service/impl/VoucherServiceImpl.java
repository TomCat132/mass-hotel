package cn.finetool.activity.service.impl;

import cn.finetool.activity.mapper.VoucherMapper;
import cn.finetool.activity.service.VoucherService;
import cn.finetool.activity.strategy.SaveVoucherContext;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.enums.CodeSign;
import cn.finetool.common.enums.Status;
import cn.finetool.common.po.Voucher;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {

    private static final SnowflakeIdWorker WORKER_ID = new SnowflakeIdWorker(2,0);

    @Resource
    private SaveVoucherContext saveVoucherContext;

    @Resource
    private VoucherService voucherService;

    @Override
    public Response addVoucher(VoucherDto voucherDto) {

        String voucherId = CodeSign.VoucherPrefix.getCode() + String.valueOf(WORKER_ID.nextId());
        voucherDto.getVoucherCoupon().setVoucherId(voucherId);
        LocalDateTime nowTime = LocalDateTime.now();

        Voucher voucher = new Voucher();
        voucher.setVoucherId(voucherId);
        voucher.setCreateTime(nowTime);
        voucher.setVoucherType(voucherDto.getVoucherType());
        save(voucher);
        // 根据不同的活动券类型加入不同的表中
        saveVoucherContext.saveVoucher(voucherDto);

        return Response.success("操作成功,请手动发放活动券");
    }

    @Override
    public Response grantVoucher(Integer voucherId) {

        Voucher voucherInfo = voucherService.getOne(new LambdaQueryWrapper<Voucher>()
                .eq(Voucher::getVoucherId, voucherId));

        saveVoucherContext.changStatus(voucherInfo.getVoucherType(), voucherId, Status.VOUCHER_SEND.getCode());
        return Response.success("已发放");
    }
}
