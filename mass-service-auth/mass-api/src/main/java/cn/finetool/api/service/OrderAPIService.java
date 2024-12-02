package cn.finetool.api.service;



import cn.finetool.common.configuration.MultipartSupportConfig;
import cn.finetool.common.dto.CreateOrderDto;
import cn.finetool.common.po.OrderStatus;
import cn.finetool.common.po.RechargeOrder;
import cn.finetool.common.po.RoomOrder;
import cn.finetool.common.vo.OrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mass-order-service", path = "/order/api", configuration = MultipartSupportConfig.class)
public interface OrderAPIService {

    @GetMapping("/queryRechargeOrder/{orderId}")
    RechargeOrder queryRechargeOrder(@PathVariable("orderId") String orderId);

    @PutMapping("/handleRechargeOrder")
    void handleRechargeOrder(@RequestParam("orderId") String orderId);

    /**====== 获取 用户 充值订单列表 =====*/
    @GetMapping("/getRechargeOrderList")
    List<OrderVo> getRechargeOrderList(@RequestParam("userId") String userId);

    @PutMapping("/changeOrderStatus")
    void changeOrderStatus(@RequestParam("orderId") String orderId,
                           @RequestParam("orderStatus") Integer orderStatus,
                           @RequestParam(value = "payType",required = false) Integer payType);

    /** ======== 获取 充值订单状态 =======*/
    @GetMapping("/queryOrder")
    OrderStatus queryOrder(@RequestParam("orderId") String orderId);

    /** ======== 获取 房间订单信息 =======*/
    @GetMapping("/queryOrderInfo")
    RoomOrder queryOrderInfo(@RequestParam("orderId") String orderId);

    /** ======== 创建 房间预定 订单 =======*/
    @RequestMapping(value = "/createRoomOrder", consumes = "application/json")
    void createRoomOrder(@RequestBody CreateOrderDto createOrderDto);

    /** ======== 获取 用户 房间预定订单列表 =======*/
    @GetMapping("/getRoomOrderList")
    List<OrderVo> getRoomOrderList(@RequestParam("userId") String userId);

    /** ======== 逻辑删除 订单 =======*/
    @PutMapping("/deleteOrder")
    void deleteOrder(@RequestParam("orderId") String orderId);
}
