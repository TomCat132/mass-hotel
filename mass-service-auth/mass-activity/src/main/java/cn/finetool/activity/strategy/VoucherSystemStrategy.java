package cn.finetool.activity.strategy;

import cn.finetool.activity.mapper.VoucherSystemMapper;
import cn.finetool.common.dto.VoucherDto;
import cn.finetool.common.po.VoucherSystem;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class VoucherSystemStrategy extends SaveVoucherStrategy{
    
    @Resource
    private VoucherSystemMapper voucherSystemMapper;
    
    @Override
    public void save(VoucherDto voucherDto) {
        if (Objects.nonNull(voucherDto.getVoucherSystem())){
            voucherSystemMapper.insert(voucherDto.getVoucherSystem());
        }
    }
    
    @Override
    public void changeStatus(String voucherId, Integer status) {
        voucherSystemMapper.update(new UpdateWrapper<VoucherSystem>()
                .set("status",status)
                .eq("voucher_id",voucherId));
    }


}
