package cn.finetool.activity.service;

import cn.finetool.common.po.UserVoucher;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserVoucherService extends IService<UserVoucher> {
    Response getVoucher(String voucherId, String userId);
}
