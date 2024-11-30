package cn.finetool.order.mapper;

import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.vo.OrderVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Mapper
public interface RoomOrderMapper extends BaseMapper<RoomOrder> {
    List<OrderVo> getRoomOrderList(@RequestParam("userId") String userId);
}
