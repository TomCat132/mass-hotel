package cn.finetool.order.api;


import cn.dev33.satoken.stp.StpUtil;
import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.vo.OrderVo;
import cn.finetool.order.service.RechargeOrderService;
import cn.finetool.order.service.impl.RechargeOrderServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order/api")
@Slf4j
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

    /** ========= 获取 用户 充值订单列表 ========= */
    @GetMapping("/getRechargeOrderList")
    public List<OrderVo> getRechargeOrderList(@RequestParam("userId") String userId){
        return rechargeOrderService.getRechargeOrderList(userId);
    }

}

