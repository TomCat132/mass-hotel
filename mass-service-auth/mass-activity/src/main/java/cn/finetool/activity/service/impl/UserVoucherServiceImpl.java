package cn.finetool.activity.service.impl;

import cn.finetool.activity.mapper.UserVoucherMapper;
import cn.finetool.activity.service.UserVoucherService;
import cn.finetool.common.po.UserVoucher;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserVoucherServiceImpl extends ServiceImpl<UserVoucherMapper, UserVoucher> implements UserVoucherService {
}
