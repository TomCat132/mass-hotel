package cn.finetool.api.service;


import cn.finetool.common.configuration.MultipartSupportConfig;
import cn.finetool.common.dto.OrderPayDto;
import cn.finetool.common.vo.RoomOrderBaseInfo;
import java.math.BigDecimal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Component
@FeignClient(name = "mass-hotel-service", path = "/hotel/api", configuration = MultipartSupportConfig.class)
public interface HotelAPIService {

    @GetMapping("/queryResidualRoomInfo")
    List<Integer> queryResidualRoomInfo(@RequestParam("roomId") String roomId,
                                        @RequestParam("checkInDate") LocalDate checkInDate,
                                        @RequestParam("checkOutDate") LocalDate checkOutDate);

    @PutMapping("/updateRoomDateStatus")
    void updateRoomDateStatus(@RequestParam("roomDateId") Integer roomDateId,
                              @RequestParam("checkInDate") LocalDate checkInDate,
                              @RequestParam("checkOutDate") LocalDate checkOutDate,
                              @RequestParam("status") Integer status);

    @GetMapping("/getBookedRoomBaseInfo")
    RoomOrderBaseInfo getBookedRoomBaseInfo(@RequestParam("orderId") String orderId,
                                            @RequestParam("roomDateId") Integer roomDateId);

    /**
     * 根据房间日期ID获取房间价格
     * @param roomDateId
     * @return
     */
    @GetMapping("/getRoomDatePriceById")
    BigDecimal getRoomDatePriceById(@RequestParam("roomDateId") Integer roomDateId);

    /**
     * 校验计算订单金额
     * @param orderPayDto
     * @return
     */
    @PostMapping("/caculatePayAmount")
    BigDecimal caculatePayAmount(@RequestBody OrderPayDto orderPayDto);
    
    /**======== 根据OrderId更改房间预定信息状态 ======== **/
    @PutMapping("/updateRoomBookingStatus")
    void updateRoomBookingStatus(@RequestParam("orderId") String orderId,
                                 @RequestParam("status") Integer status);

    /**======== 持久化WebSocket消息 ======== **/
    @PostMapping("/saveWebSocketMessage")
    void saveWebSocketMessage(@RequestParam("message") String message,
                              @RequestParam("receiverId") String receiverId);
}
