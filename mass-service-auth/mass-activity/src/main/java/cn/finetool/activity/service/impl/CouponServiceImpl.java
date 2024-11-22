package cn.finetool.activity.service.impl;

import cn.finetool.activity.mapper.CouponMapper;
import cn.finetool.activity.service.CouponService;
import cn.finetool.common.po.Coupon;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {
}