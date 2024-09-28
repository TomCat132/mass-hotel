package cn.finetool.activity.service;

import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.po.Voucher;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;

public interface VoucherService extends IService<Voucher> {

    Response addVoucher(VoucherDto voucherDto);
}
