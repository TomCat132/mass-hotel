package cn.finetool.activity.service.impl;

import cn.finetool.activity.mapper.UserVoucherMapper;
import cn.finetool.activity.service.UserVoucherService;
import cn.finetool.activity.service.VoucherService;
import cn.finetool.activity.strategy.SaveVoucherContext;
import cn.finetool.api.handler.MessageHandler;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.po.UserVoucher;
import cn.finetool.common.po.Voucher;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.Strings;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
public class UserVoucherServiceImpl extends ServiceImpl<UserVoucherMapper, UserVoucher> implements UserVoucherService {

    @Resource
    private UserVoucherService userVoucherService;
    @Resource
    private SaveVoucherContext saveVoucherContext;
    @Resource
    private VoucherService voucherService;
    @Resource
    private MessageHandler messageHandler;

    @Override
    public Response getVoucher(String voucherId, String userId) {
            // 接口幂等性校验
            UserVoucher voucher = userVoucherService.getOne(new LambdaQueryWrapper<UserVoucher>()
                    .eq(UserVoucher::getVoucherId, voucherId));
            if (voucher != null){
                throw new BusinessRuntimeException("每个人限领一次");
            }
            Voucher voucherInfo = voucherService.getOne(new LambdaQueryWrapper<Voucher>()
                    .eq(Voucher::getVoucherId, voucherId));
            //扣减库存
            saveVoucherContext.decreaseVoucherStock(voucherInfo.getVoucherType(), voucherId, userId);
            //保存用户优惠券
            UserVoucher userVoucher = new UserVoucher();
            userVoucher.setVoucherId(voucherId);
            userVoucher.setUserId(userId);
            save(userVoucher);

            return Response.success("领取成功");
        }

}

