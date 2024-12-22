package cn.finetool.account.handler;

import cn.finetool.account.mapper.UserMerchantMapper;
import cn.finetool.account.service.AccountService;
import cn.finetool.common.po.UserMerchant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AccountHandler implements AccountService {
    
    @Resource
    private UserMerchantMapper userMerchantMapper;
    
    @Override
    public String queryMerchantOfUser(String userId) {
        UserMerchant userMerchant = userMerchantMapper.selectOne(new QueryWrapper<UserMerchant>()
                .eq("user_id", userId));
        if (Objects.isNull(userMerchant)){
            return null;
        }
        return userMerchant.getMerchantId();
    }

    @Override
    public List<String> findMerchantEmployee(String merchantId) {
        List<UserMerchant> merchantEmployeeList = userMerchantMapper.selectList(new QueryWrapper<UserMerchant>()
                .eq("merchant_id", merchantId));
        return merchantEmployeeList.stream().map(UserMerchant::getUserId).collect(Collectors.toList());
    }
}
