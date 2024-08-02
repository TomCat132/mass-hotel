package cn.finetool.account.mapper;

import cn.finetool.common.po.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    void updateUserInfo(@Param("userId") String userId,
                        @Param("account") BigDecimal totalAmount,
                        @Param("points") int points,
                        @Param("usePoints") int usePoints);
}

