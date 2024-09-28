package cn.finetool.order.service.impl;

import cn.finetool.common.po.OrderStatus;
import cn.finetool.order.mapper.OrderStatusMapper;
import cn.finetool.order.service.OrderStatusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderStatusServiceImpl extends ServiceImpl<OrderStatusMapper, OrderStatus> implements OrderStatusService {

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Override
    public void changeOrderStatus(String orderId, Integer orderStatus, Integer payType) {
        LocalDateTime operationTIme =  LocalDateTime.now();
        orderStatusMapper.changeOrderStatus(orderId, orderStatus,operationTIme ,payType);
    }
}
