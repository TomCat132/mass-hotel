package cn.finetool.hotel.mapper;

import cn.finetool.common.po.ChatMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import feign.Param;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    
    ChatMessage findNewMessage(@Param("chatId") String chatId);
}
