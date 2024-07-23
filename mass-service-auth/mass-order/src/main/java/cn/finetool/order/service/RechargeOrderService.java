package cn.finetool.order.service;

import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.util.Response;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RechargeOrderService extends IService<RechargeOrder> {

    Response createOrder(RechargeOrder rechargeOrder);

    void updateOrderStatus(String orderId, Integer orderStatus);
}
