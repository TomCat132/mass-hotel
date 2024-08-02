package cn.finetool.order.api;


import cn.finetool.common.po.RechargeOrder;
import cn.finetool.order.service.RechargeOrderService;
import cn.finetool.order.service.impl.RechargeOrderServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order/api")
public class OrderAPIService {

    @Resource
    private RechargeOrderService rechargeOrderService;

    @Resource
    private RechargeOrderServiceImpl rechargeOrderServiceImpl;

    /** ========= 更改订单状态 ========= */
    @PutMapping("/updateOrderStatus")
    public void updateOrderStatus(@RequestParam("orderId") String orderId, @RequestParam("orderStatus") Integer orderStatus){
        rechargeOrderService.updateOrderStatus(orderId, orderStatus);
    }

    /** ========= 查询充值订单 ========= */
    @GetMapping("/queryRechargeOrder/{orderId}")
    public RechargeOrder queryRechargeOrder(@PathVariable("orderId") String orderId){
        return rechargeOrderServiceImpl.getOrderById(orderId);
    }

    /** ========= 处理充值订单 ========= */
    @PutMapping("/handleRechargeOrder")
    public void handleRechargeOrder(@RequestParam("orderId") String orderId){
        rechargeOrderService.handleRechargeOrder(orderId);
    }


}

