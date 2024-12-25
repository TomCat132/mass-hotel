package cn.finetool.activity.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.activity.mapper.UserSignMapper;
import cn.finetool.activity.service.UserSignService;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.po.UserSign;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.TimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserSignServiceImpl extends ServiceImpl<UserSignMapper, UserSign> implements UserSignService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    UserSignService userSignService;

    private  List<UserSign> userSignBatch = new ArrayList<>();

    @Override
    public Response userSign() {

        String userId = StpUtil.getLoginIdAsString();
        log.info("签到时间:{}", LocalDate.now().getDayOfYear());
        if (userSignService.isUserSign()){
            return Response.error(BusinessErrors.DATA_DUPLICATION.getCode(),"今日已签到,明天再来哦~");
        }
        LocalDateTime nowTime = LocalDateTime.now();
        LocalDate clickTime = nowTime.toLocalDate();
        
        redisTemplate.opsForValue().setBit(clickTime.getYear() + RedisCache.USER_SIGN_TABLE + userId
                ,nowTime.getDayOfYear(),true
                );
        redisTemplate.opsForList().leftPush(RedisCache.USER_SIGN_KEY_PREFIX + userId , TimeUtil.format(nowTime));


        return Response.success("签到成功");
    }
    
    // 已优化
    @Scheduled(fixedRate = 100000)
    public void saveUserSignToDatabase(){
        log.info("检查签到数据是否达标=================");
        Set<String> keys = redisTemplate.keys(RedisCache.USER_SIGN_KEY_PREFIX + "*");
        for (String key : keys) {
            List<Object> signTimes = redisTemplate.opsForList().range(key, 0, -1);
            if (signTimes != null && !signTimes.isEmpty()) {
                String userId = key.replace(RedisCache.USER_SIGN_KEY_PREFIX, "");
                for (Object signTime : signTimes){
                    UserSign userSign = new UserSign();
                    userSign.setUserId(userId);
                    LocalDateTime signedTime = TimeUtil.parse((String) signTime);
                    LocalDate localDate = signedTime.toLocalDate();
                    userSign.setSignTime(signedTime);
                    userSign.setSignDate(localDate);
                    userSignBatch.add(userSign);
                    if (userSignBatch.size() >= 1000){
                        userSignService.saveBatch(userSignBatch);
                        userSignBatch.clear();
                        // 删除 list 数据
                        redisTemplate.delete(key);
                    }
                }
            }
        }
    }

    @Override
    public boolean isUserSign() {
        String userId = StpUtil.getLoginIdAsString();
        Boolean signed = redisTemplate.opsForValue().getBit(LocalDate.now().getYear() + RedisCache.USER_SIGN_TABLE + userId,
                LocalDate.now().getDayOfYear());
        LocalDate today = LocalDate.now();
        // 今天是否签到
        if (!Boolean.TRUE.equals(signed)){
            // 今天凌晨 ~ 明天凌晨 是否签到
            UserSign userSign = userSignService.getOne(new LambdaQueryWrapper<UserSign>()
                    .eq(UserSign::getUserId, userId)
                    .between(UserSign::getSignTime, today.atStartOfDay(), today.plusDays(1).atStartOfDay()));
            if (userSign != null){
                return true;
            }else{
                return false;
            }
        }
        return true;
    }
}
