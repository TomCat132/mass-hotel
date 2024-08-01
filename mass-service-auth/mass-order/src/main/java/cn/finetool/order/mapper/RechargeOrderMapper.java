package cn.finetool.order.mapper;

import cn.finetool.common.po.RechargeOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface RechargeOrderMapper extends BaseMapper<RechargeOrder> {

    void updateOrderStatus(String orderId, Integer orderStatus,
                           @Param("cancelTime") LocalDateTime cancelTime);

    RechargeOrder queryOrder(@Param(("orderId")) String orderId);

    void handleRechargeOrder(@Param(("orderId")) String orderId,
                             @Param("nowTime") LocalDateTime now,
                             @Param("payType") int payType,
                             @Param("orderStatus") int orderStatus);
}
