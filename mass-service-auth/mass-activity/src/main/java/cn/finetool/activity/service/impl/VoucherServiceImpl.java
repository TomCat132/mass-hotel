package cn.finetool.activity.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.activity.mapper.VoucherMapper;
import cn.finetool.activity.service.VoucherService;
import cn.finetool.activity.strategy.SaveVoucherContext;
import cn.finetool.api.service.AccountAPIService;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.enums.CodeSign;
import cn.finetool.common.enums.Status;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.Voucher;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.SnowflakeIdWorker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {

    private static final SnowflakeIdWorker WORKER_ID = new SnowflakeIdWorker(2,0);

    @Resource
    private SaveVoucherContext saveVoucherContext;
    @Resource
    private VoucherService voucherService;
    @Resource
    private AccountAPIService accountAPIService;

    @Override
    @Transactional
    public Response addVoucher(VoucherDto voucherDto) {
        
        

        String voucherId = CodeSign.VoucherPrefix.getCode() + String.valueOf(WORKER_ID.nextId());
        voucherDto.setVoucherId(voucherId);
        LocalDateTime nowTime = LocalDateTime.now();
        
        saveVoucherContext.saveVoucher(voucherDto);
        
        Voucher voucher = new Voucher();
        voucher.setVoucherId(voucherId);
        voucher.setCreateTime(nowTime);
        voucher.setVoucherType(voucherDto.getVoucherType());
        String userId = StpUtil.getLoginIdAsString();
        String merchantId = accountAPIService.queryMerchantOfUser(userId);
        if (Strings.isBlank(merchantId)){
            throw new BusinessRuntimeException("用户未关联商户");
        }
        voucher.setUserId(userId);
        voucher.setMerchantId(merchantId);
        save(voucher);
        // 根据不同的活动券类型加入不同的表中
       

        return Response.success("操作成功");
    }

    @Override
    public Response grantVoucher(String voucherId) {

        Voucher voucherInfo = voucherService.getOne(new LambdaQueryWrapper<Voucher>()
                .eq(Voucher::getVoucherId, voucherId));

        saveVoucherContext.changStatus(voucherInfo.getVoucherType(), voucherId, Status.VOUCHER_SEND.getCode());
        return Response.success("已发放");
    }
}
