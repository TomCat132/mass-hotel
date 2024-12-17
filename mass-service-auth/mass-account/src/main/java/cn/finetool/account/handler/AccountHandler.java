package cn.finetool.account.handler;

import cn.finetool.account.mapper.UserMerchantMapper;
import cn.finetool.account.service.AccountService;
import cn.finetool.common.po.UserMerchant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.util.Objects;
import org.apache.logging.log4j.util.Strings;
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
}
