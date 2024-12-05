package cn.finetool.order.service;

import cn.finetool.common.dto.RechargeDto;
import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.util.Response;
import cn.finetool.common.vo.OrderVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RechargeOrderService extends IService<RechargeOrder> {

    Response createOrder(RechargeDto rechargeDto);

    void updateOrderStatus(String orderId, Integer orderStatus);

    Response queryOrder(String orderId);

    void handleRechargeOrder(String orderId);

    List<OrderVO> getRechargeOrderList(String userId);
}
