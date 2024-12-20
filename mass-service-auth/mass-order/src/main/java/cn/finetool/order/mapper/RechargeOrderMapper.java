package cn.finetool.order.mapper;

import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.vo.OrderVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RechargeOrderMapper extends BaseMapper<RechargeOrder> {

    void updateOrderStatus(String orderId, Integer orderStatus,
                           @Param("cancelTime") LocalDateTime cancelTime);

    OrderVO queryOrder(@Param(("orderId")) String orderId);

    void handleRechargeOrder(@Param(("orderId")) String orderId,
                             @Param("payTime") LocalDateTime nowTime,
                             @Param("payType") int payType,
                             @Param("orderStatus") int orderStatus);

    List<OrderVO> getRechargeOrderList(@Param(("userId")) String userId);

    List<OrderVO> getAppOrderList();
}
