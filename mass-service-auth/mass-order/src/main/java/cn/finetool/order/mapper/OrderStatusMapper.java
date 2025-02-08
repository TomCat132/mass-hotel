package cn.finetool.order.mapper;

import cn.finetool.common.po.OrderStatus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

public interface OrderStatusMapper extends BaseMapper<OrderStatus> {

    /**
     * 更新订单状态
     * @param orderId： 订单号
     * @param orderStatus： 订单状态
     * @param operationTime： 操作时间
     * @param payType: payType 参数如果未null,则是取消订单动作 (operationTime === cancelTime);
     *               不为null,则是支付订单动作 (operationTime === payTime)
     */
    void changeOrderStatus(@Param("orderId") String orderId,
                           @Param("orderStatus") Integer orderStatus,
                           @Param("operationTime")LocalDateTime operationTime,
                           @Param("payType") Integer payType);
}
