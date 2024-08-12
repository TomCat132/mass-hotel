package cn.finetool.order.service.impl;

import cn.finetool.common.po.OrderStatus;
import cn.finetool.order.mapper.OrderStatusMapper;
import cn.finetool.order.service.OrderStatusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusServiceImpl extends ServiceImpl<OrderStatusMapper, OrderStatus> implements OrderStatusService {
}
