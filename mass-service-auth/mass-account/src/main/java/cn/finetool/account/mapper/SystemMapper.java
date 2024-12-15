package cn.finetool.account.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SystemMapper {
    String getMerchantId(@Param("userId") String userId);
}
