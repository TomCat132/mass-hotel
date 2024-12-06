package cn.finetool.activity.mapper;

import cn.finetool.common.po.VoucherCoupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CouponMapper extends BaseMapper<VoucherCoupon> {
}
