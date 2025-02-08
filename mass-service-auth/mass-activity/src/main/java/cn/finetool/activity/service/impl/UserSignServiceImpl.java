package cn.finetool.activity.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.activity.mapper.SignRewardMapper;
import cn.finetool.activity.mapper.UserSignMapper;
import cn.finetool.activity.mapper.VoucherMapper;
import cn.finetool.activity.service.UserSignService;
import cn.finetool.api.service.AccountAPIService;
import cn.finetool.common.constant.RedisCache;
import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.enums.VoucherType;
import cn.finetool.common.po.SignReward;
import cn.finetool.common.po.UserSign;
import cn.finetool.common.po.Voucher;
import cn.finetool.common.util.JsonUtil;
import cn.finetool.common.util.Response;
import cn.finetool.common.util.Strings;
import cn.finetool.common.util.TimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserSignServiceImpl extends ServiceImpl<UserSignMapper, UserSign> implements UserSignService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserSignService userSignService;
    @Resource
    private SignRewardMapper signRewardMapper;
    @Resource
    private VoucherMapper voucherMapper;
    @Resource
    private AccountAPIService accountAPIService;

    private List<UserSign> userSignBatch = new ArrayList<>();


    @Override
    public Response userSign() throws JsonProcessingException {

        String userId = StpUtil.getLoginIdAsString();
        log.info("签到时间:{}", LocalDate.now().getDayOfYear());
        if (userSignService.isUserSign()) {
            return Response.error(BusinessErrors.DATA_DUPLICATION.getCode(), "今日已签到,明天再来哦~");
        }
        LocalDateTime nowTime = TimeUtil.now();
        LocalDate signDate = nowTime.toLocalDate();

        redisTemplate.opsForValue().setBit(signDate.getYear() + RedisCache.USER_SIGN_TABLE + userId
                , nowTime.getDayOfYear(), true
        );
        redisTemplate.opsForList().leftPush(RedisCache.USER_SIGN_KEY_PREFIX + userId, TimeUtil.format(nowTime));

        UserSign userSign = new UserSign();
        userSign.setUserId(userId);
        userSign.setSignTime(nowTime);
        userSign.setSignDate(signDate);

        //发放奖励
        SignReward rewardDate = signRewardMapper.selectOne(new QueryWrapper<SignReward>()
                .eq("reward_date", signDate));
        if (Objects.nonNull(rewardDate)) {
            //解析奖励内容
            Map contentMap = JsonUtil.fromJsonString(rewardDate.getRewardContent(), Map.class);
            // 活动券奖励
            String voucherId = (String) contentMap.getOrDefault("voucher_id", "");
            if (Strings.isNotBlank(voucherId)) {
                Voucher voucher = voucherMapper.selectOne(new QueryWrapper<Voucher>()
                        .eq("voucher_id", voucherId));
                if (Objects.nonNull(voucher)) {
                    // TODO: 目前只发放消费券
                    if (Strings.equals(VoucherType.CONSUME.code(), voucher.getVoucherType())) {
                        // 发放消费券. 只有消费券才传递数量
                        Integer consumeCount = (Integer) contentMap.getOrDefault("consume_count", 0);
                        accountAPIService.grantConsumeVoucher(userId, consumeCount);
                    }
                }
            }
            // 积分奖励
            Integer rewardPoints = (Integer) contentMap.getOrDefault("reward_points", 0);
            accountAPIService.grantPoints(userId, rewardPoints);
        }


        return Response.success("签到成功");
    }

//    // 已优化
//    @Scheduled(fixedRate = 100000)
//    public void saveUserSignToDatabase(){
//        log.info("检查签到数据是否达标=================");
//        Set<String> keys = redisTemplate.keys(RedisCache.USER_SIGN_KEY_PREFIX + "*");
//        for (String key : keys) {
//            List<Object> signTimes = redisTemplate.opsForList().range(key, 0, -1);
//            if (signTimes != null && !signTimes.isEmpty()) {
//                String userId = key.replace(RedisCache.USER_SIGN_KEY_PREFIX, "");
//                for (Object signTime : signTimes){
//                    UserSign userSign = new UserSign();
//                    userSign.setUserId(userId);
//                    LocalDateTime signedTime = TimeUtil.parseToLocalDateTime((String) signTime);
//                    LocalDate localDate = signedTime.toLocalDate();
//                    userSign.setSignTime(signedTime);
//                    userSign.setSignDate(localDate);
//                    userSignBatch.add(userSign);
//                    if (userSignBatch.size() >= 1000){
//                        userSignService.saveBatch(userSignBatch);
//                        userSignBatch.clear();
//                        // 删除 list 数据
//                        redisTemplate.delete(key);
//                    }
//                }
//            }
//        }
//    }

    @Override
    public boolean isUserSign() {
        String userId = StpUtil.getLoginIdAsString();
        Boolean signed = redisTemplate.opsForValue().getBit(LocalDate.now().getYear() + RedisCache.USER_SIGN_TABLE + userId,
                LocalDate.now().getDayOfYear());
        LocalDate today = LocalDate.now();
        // 今天是否签到
        if (!Boolean.TRUE.equals(signed)) {
            // 今天凌晨 ~ 明天凌晨 是否签到
            UserSign userSign = userSignService.getOne(new LambdaQueryWrapper<UserSign>()
                    .eq(UserSign::getUserId, userId)
                    .between(UserSign::getSignTime, today.atStartOfDay(), today.plusDays(1).atStartOfDay()));
            return Objects.nonNull(userSign);
        }
        return true;
    }

    @Override
    public Response userSignList(String userId) {
        List<UserSign> signRecords = new ArrayList<>();
        int startDays = TimeUtil.now().getDayOfYear();
        int year = TimeUtil.now().getYear();
        String bitmapKey = year + RedisCache.USER_SIGN_TABLE + userId;
        //获取当年 当天以前的签到记录
        for (int dayOfYear = 1; dayOfYear <= startDays; dayOfYear++) {
            Boolean signed = redisTemplate.opsForValue().getBit(bitmapKey, dayOfYear);
            if (Boolean.TRUE.equals(signed)) {
                UserSign signRecord = new UserSign();
                signRecord.setUserId(userId);
                signRecord.setSignDate(LocalDate.ofYearDay(year, dayOfYear));
                signRecords.add(signRecord);
            }
        }
        List<LocalDate> signDateList = signRecords.stream()
                .map(UserSign::getSignDate)
                .collect(Collectors.toList());

        return Response.success(signDateList);
    }
}
