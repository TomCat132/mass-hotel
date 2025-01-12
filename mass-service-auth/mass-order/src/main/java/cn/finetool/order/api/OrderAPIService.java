package cn.finetool.order.api;


import cn.finetool.common.dto.CreateOrderDto;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.vo.OrderVO;
import cn.finetool.order.service.OrderService;
import cn.finetool.order.service.OrderStatusService;
import cn.finetool.order.service.RechargeOrderService;
import cn.finetool.order.service.RoomOrderService;
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
    private RoomOrderService roomOrderService;
    @Resource
    public OrderService orderHandler;

    /** ========= 更改订单状态 ========= */
    @PutMapping("/updateOrderStatus")
    public void updateOrderStatus(@RequestParam("orderId") String orderId, @RequestParam("orderStatus") Integer orderStatus){
        rechargeOrderService.updateOrderStatus(orderId, orderStatus);
    }

    /** ========= 查询充值订单 ========= */
    @GetMapping("/queryRechargeOrder/{orderId}")
    public OrderVO queryRechargeOrder(@PathVariable("orderId") String orderId){
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
                                  @RequestParam(value = "payType", required = false) Integer payType){
        orderStatusService.changeOrderStatus(orderId, orderStatus, payType);
    }

    /** ========= 查询 房间订单信息 ========= */
    @GetMapping("/queryOrderInfo")
    RoomOrder queryOrderInfo(@RequestParam("orderId") String orderId){
        return roomOrderService.queryRoomOrderInfo(orderId);
    }

    /** ========= 创建房间订单 ========= */
    @RequestMapping(value = "/createRoomOrder",consumes = "application/json")
    public void CreateRoomOrder(@RequestBody CreateOrderDto createOrderDto){
        roomOrderService.createRoomOrderInfo(createOrderDto);
    }

    /** ========= 获取 用户 充值订单列表 ========= */
    @GetMapping("/getRechargeOrderList")
    public List<OrderVO> getRechargeOrderList(@RequestParam("userId") String userId){
        return rechargeOrderService.getRechargeOrderList(userId);
    }

    /** ========= 获取 用户 房间订单列表 ========= */
    @GetMapping("/getRoomOrderList")
    public List<OrderVO> getRoomOrderList(@RequestParam("userId") String userId){
        return roomOrderService.getRoomOrderList(userId);
    }

    /** ======== 逻辑删除 订单 =======*/
    @PutMapping("/deleteOrder")
    void deleteOrder(@RequestParam("orderId") String orderId){
        orderHandler.deleteOrder(orderId);
    }

    /**======== 根据订单号查询用户id ======= */
    @GetMapping("/findUserIdByOrderId")
    String findUserIdByOrderId(@RequestParam("orderId") String orderId){
        return orderHandler.findUserIdByOrderId(orderId);
    }

    /**======== 更新订单评价状态 ======= */
    @PutMapping("/updateEvaluateStatus")
    void updateEvaluateStatus(@RequestParam("orderId") String orderId,
                              @RequestParam("isEvaluate") Integer isEvaluate){
        orderHandler.updateEvaluateStatus(orderId, isEvaluate);
    }

    /**======== 根据订单号查询商户id ======= */
    @GetMapping("/getAppRechargeOrderList")
    String findMerchantIdByOrderId(@RequestParam("orderId") String orderId) {
        return orderHandler.findMerchantIdByOrderId(orderId);
    }
}

