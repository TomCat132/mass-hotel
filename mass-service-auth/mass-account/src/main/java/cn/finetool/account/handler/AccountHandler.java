package cn.finetool.account.handler;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.account.mapper.UserMerchantMapper;
import cn.finetool.account.service.AccountService;
import cn.finetool.api.mapper.MessageBoxMapper;
import cn.finetool.common.enums.Status;
import cn.finetool.common.po.MessageBox;
import cn.finetool.common.po.UserMerchant;
import cn.finetool.common.util.IpUtil;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import static cn.finetool.common.util.Response.success;

@Component
public class AccountHandler implements AccountService {
    
    @Resource
    private UserMerchantMapper userMerchantMapper;
    @Resource
    private MessageBoxMapper messageBoxMapper;
    
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

    @Override
    public Response getUserMessageBoxList() {
        List<MessageBox> messageBoxList = messageBoxMapper.selectList(new QueryWrapper<MessageBox>()
                .eq("accept_id", StpUtil.getLoginIdAsString())
                .orderByDesc("sender_time"));
        if (CollectionUtils.isNotEmpty(messageBoxList)){
            return success(messageBoxList);
        }
        return success(Collections.emptyList());
    }

    @Override
    public Response getUnreadMessageCount() {
        return success(messageBoxMapper.selectList(new QueryWrapper<MessageBox>()
                .eq("accept_id", StpUtil.getLoginIdAsString())
                .eq("status", Status.MESSAGE_UNREAD.code())).size());
    }

    @Override
    public Response tagAllMessageRead() {
        messageBoxMapper.update(new UpdateWrapper<MessageBox>()
                .set("status", Status.MESSAGE_READ.code())
                .eq("accept_id", StpUtil.getLoginIdAsString()));
        return success("所有消息已被标记为已读");
    }

    @Override
    public String findMerchantIdByUserId(String workerId) {
        return userMerchantMapper.selectOne(new QueryWrapper<UserMerchant>()
                .eq("user_id", workerId)).getMerchantId();
    }

    @Override
    public Response getUserLocationInfo() {
        Map<String, String> locationInfo = IpUtil.getLocationInfo();
        return success(locationInfo);
    }
}
