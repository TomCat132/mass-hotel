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
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

import static cn.finetool.common.util.Response.success;

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

        String userId = StpUtil.getLoginIdAsString();
        String merchantId = accountAPIService.queryMerchantOfUser(userId);
        voucherDto.setMerchantId(merchantId);
        if (Strings.isBlank(merchantId)) {
            throw new BusinessRuntimeException("用户未关联商户");
        }
        
        saveVoucherContext.saveVoucher(voucherDto);

        Voucher voucher = new Voucher();
        voucher.setVoucherId(voucherId);
        voucher.setCreateTime(nowTime);
        voucher.setVoucherType(voucherDto.getVoucherType());


        voucher.setUserId(userId);
        voucher.setMerchantId(merchantId);
        save(voucher);
        // 根据不同的活动券类型加入不同的表中


        return success("操作成功");
    }



    @Override
    public Response getAllCategoryVoucherList(String merchantId) {
        // 查询商户的优惠券
        List<VoucherVO> voucherList = voucherMapper.findMerchantVoucherListById(merchantId);
        voucherList = voucherList.stream().sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).toList();
        return success(voucherList);
    }

    @Override
    public void updateVoucherStatus(Integer voucherType, String voucherId, Integer status) {
        saveVoucherContext.changStatus(voucherType, voucherId, status);
    }
}
