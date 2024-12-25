package cn.finetool.order.mapper;

import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.vo.OrderVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RoomOrderMapper extends BaseMapper<RoomOrder> {
    List<OrderVO> getRoomOrderList(@Param("userId") String userId);

    List<OrderVO> queryMerchantRoomOrderList(@Param("merchantId") String merchantId);

    String findMerchantIdByOrderId(@Param("orderId") String orderId);
}
