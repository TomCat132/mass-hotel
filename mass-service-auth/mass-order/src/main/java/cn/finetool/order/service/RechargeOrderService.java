package cn.finetool.order.service;

import cn.finetool.common.dto.RechargeDto;
import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RechargeOrderService extends IService<RechargeOrder> {

    Response createOrder(RechargeDto rechargeDto);

    void updateOrderStatus(String orderId, Integer orderStatus);

    Response queryOrder(String orderId);

    void handleRechargeOrder(String orderId);
}
