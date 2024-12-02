package cn.finetool.order.api;


import cn.finetool.common.dto.CreateOrderDto;
import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.vo.OrderVo;
import cn.finetool.order.service.OrderService;
import cn.finetool.order.service.OrderStatusService;
import cn.finetool.order.service.RechargeOrderService;
import cn.finetool.order.service.impl.RechargeOrderServiceImpl;
import cn.finetool.order.service.impl.RoomOrderServiceImpl;
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
    private OrderStatusService orderStatusService;

    @Resource
    private RechargeOrderServiceImpl rechargeOrderServiceImpl;

    @Resource
    private RoomOrderServiceImpl roomOrderService;

    @Resource
    public OrderService orderHandler;

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

    /** ========= 充值订单状态修改 ========= */
    @PutMapping("/changeOrderStatus")
    public void changeOrderStatus(@RequestParam("orderId") String orderId,
                                  @RequestParam("orderStatus") Integer orderStatus,
                                  @RequestParam(value = "payType",required = false) Integer payType){
        orderStatusService.changeOrderStatus(orderId, orderStatus,payType);
    }

    /** ========= 查询 房间订单信息 ========= */
    @GetMapping("/queryRoomOrderInfo")
    public RoomOrder queryRoomOrderInfo(@RequestParam("orderId") String orderId){
        return roomOrderService.queryRoomOrderInfo(orderId);
    }

    /** ========= 创建房间订单 ========= */
    @RequestMapping(value = "/createRoomOrder",consumes = "application/json")
    public void CreateRoomOrder(@RequestBody CreateOrderDto createOrderDto){
        roomOrderService.createRoomOrderInfo(createOrderDto);
    }

    /** ========= 获取 用户 充值订单列表 ========= */
    @GetMapping("/getRechargeOrderList")
    public List<OrderVo> getRechargeOrderList(@RequestParam("userId") String userId){
        return rechargeOrderService.getRechargeOrderList(userId);
    }

    /** ========= 获取 用户 房间订单列表 ========= */
    @GetMapping("/getRoomOrderList")
    public List<OrderVo> getRoomOrderList(@RequestParam("userId") String userId){
        return roomOrderService.getRoomOrderList(userId);
    }

    /** ========= 删除订单 ========= */
    @PutMapping("/deleteOrder")
    public void deleteOrder(@RequestParam("orderId") String orderId){
        orderHandler.deleteOrder(orderId);
    }


}

