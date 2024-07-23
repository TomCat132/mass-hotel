package cn.finetool.activity.service.impl;

import cn.finetool.activity.mapper.UserSignMapper;
import cn.finetool.activity.service.UserSignService;
import cn.finetool.common.po.UserSign;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserSignServiceImpl extends ServiceImpl<UserSignMapper, UserSign> implements UserSignService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public Response userSign() {
        return null;
    }
}
