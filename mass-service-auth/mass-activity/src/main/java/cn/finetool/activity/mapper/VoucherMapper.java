package cn.finetool.activity.mapper;

import cn.finetool.common.po.Voucher;
import cn.finetool.common.vo.VoucherVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VoucherMapper extends BaseMapper<Voucher> {
    
    List<VoucherVO> findMerchantVoucherListById(@Param("merchantId") String merchantId);
}
