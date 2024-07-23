package cn.finetool.activity.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.activity.mapper.UserSignMapper;
import cn.finetool.activity.service.UserSignService;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.po.UserSign;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
public class UserSignServiceImpl extends ServiceImpl<UserSignMapper, UserSign> implements UserSignService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    UserSignService userSignService;

    @Override
    public Response userSign() {

        String userId = StpUtil.getLoginIdAsString();
        log.info("签到时间:{}", LocalDate.now().getDayOfYear());
        if (userSignService.isUserSign()){
            return Response.error(BusinessErrors.DATA_DUPLICATION.getCode(),"今日已签到");
        }
        LocalDateTime nowTime = LocalDateTime.now();
        LocalDate clickTime = nowTime.toLocalDate();


        redisTemplate.opsForValue().setBit(clickTime.getYear() + RedisCache.USER_SIGN_TABLE + userId
                ,nowTime.getDayOfYear(),true
                );

        // 保存到数据库
        UserSign userSign = new UserSign();
        userSign.setUserId(userId);
        save(userSign);

        return Response.success("签到成功");
    }

    @Override
    public boolean isUserSign() {
        String userId = StpUtil.getLoginIdAsString();
        Boolean signed = redisTemplate.opsForValue().getBit(LocalDate.now().getYear() + RedisCache.USER_SIGN_TABLE + userId,
                LocalDate.now().getDayOfYear());
        return Boolean.TRUE.equals(signed);
    }
}
