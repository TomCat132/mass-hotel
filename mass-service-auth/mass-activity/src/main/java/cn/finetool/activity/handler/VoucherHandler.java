package cn.finetool.activity.handler;

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
import cn.finetool.common.util.Strings;
import cn.finetool.common.vo.VoucherVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoucherHandler extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {

    private static final SnowflakeIdWorker WORKER_ID = new SnowflakeIdWorker(2, 0);

    @Resource
    private SaveVoucherContext saveVoucherContext;
    @Resource
    private VoucherService voucherService;
    @Resource
    private VoucherMapper voucherMapper;
    @Resource
    private AccountAPIService accountAPIService;

    @Override
    @Transactional
    public Response addVoucher(VoucherDto voucherDto) throws JsonProcessingException {


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
        if (Strings.isBlank(merchantId)) {
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

        saveVoucherContext.changStatus(voucherInfo.getVoucherType(), voucherId, Status.VOUCHER_UP.getCode());
        return Response.success("已发放");
    }

    @Override
    public Response getAllCategoryVoucherList(String merchantId) {
        // 查询商户的优惠券
        List<VoucherVO> voucherList = voucherMapper.findMerchantVoucherListById(merchantId);
        if (Objects.nonNull(voucherList)) {
            return Response.success(voucherList);
        }
        return Response.success(Collections.emptyList());
    }

    @Override
    public void updateVoucherStatus(Integer voucherType, String voucherId, Integer status) {
        saveVoucherContext.changStatus(voucherType, voucherId, status);
    }
}
