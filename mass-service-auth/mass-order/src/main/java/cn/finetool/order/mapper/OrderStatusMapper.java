package cn.finetool.order.mapper;

import cn.finetool.common.po.OrderStatus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface OrderStatusMapper extends BaseMapper<OrderStatus> {
    void changeOrderStatus(@Param("orderId") String orderId,
                           @Param("orderStatus") Integer orderStatus,
                           @Param("operationTime")LocalDateTime operationTime,
                           @Param("payType") Integer payType);
}
