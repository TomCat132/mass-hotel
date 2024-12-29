package cn.finetool.hotel.api;

import cn.finetool.common.dto.OrderPayDto;
import cn.finetool.common.vo.RoomOrderBaseInfo;
import cn.finetool.hotel.handler.HotelAdminService;
import cn.finetool.hotel.service.HotelService;
import cn.finetool.hotel.service.RoomDateService;
import cn.finetool.hotel.service.RoomService;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/hotel/api")
public class HotelAPIService {

    @Resource
    private RoomService roomService;
    @Resource
    private RoomDateService roomDateService;
    @Resource
    private HotelService hotelService;
    @Resource
    private HotelAdminService hotelAdminService;
    
    /** ========== 查询该天日期房间类型的具体剩余数量 ===========*/
    @GetMapping("/queryResidualRoomInfo")
    public List<Integer> queryResidualRoomInfo(@RequestParam("roomId") String roomId,
                                               @RequestParam("checkInDate")LocalDate checkInDate,
                                               @RequestParam("checkOutDate") LocalDate checkOutDate){
        return roomService.queryResidualRoomInfo(roomId,checkInDate,checkOutDate);
    }

    @PutMapping("/updateRoomDateStatus")
    public void updateRoomDateStatus(@RequestParam("roomDateId") Integer roomDateId,
                                     @RequestParam("checkInDate") LocalDate checkInDate,
                                     @RequestParam("checkOutDate") LocalDate checkOutDate,
                                     @RequestParam("status") Integer status){
        roomDateService.updateRoomDateStatus(roomDateId,checkInDate,checkOutDate,status);
    }

    @GetMapping("/getBookedRoomBaseInfo")
    public RoomOrderBaseInfo getBookedRoomBaseInfo(@RequestParam("orderId") String orderId,
                                                   @RequestParam("roomDateId") Integer roomDateId){
        return hotelService.getBookedRoomBaseInfo(orderId,roomDateId);
    }

    /**
     * 根据房间日期ID获取房间价格
     * @param roomDateId
     * @return
     */
    @GetMapping("/getRoomDatePriceById")
    BigDecimal getRoomDatePriceById(@RequestParam("roomDateId") Integer roomDateId){
        return hotelAdminService.getRoomDatePriceById(roomDateId);
    }

    /**
     * 校验计算订单金额
     * @param orderPayDto
     * @return
     */
    @PostMapping("/caculatePayAmount")
    BigDecimal caculatePayAmount(@RequestBody OrderPayDto orderPayDto){
        return hotelService.caculatePayAmount(orderPayDto);
    }

    /**======== 根据OrderId更改房间预定信息状态 ======== **/
    @PutMapping("/updateRoomBookingStatus")
    void updateRoomBookingStatus(@RequestParam("orderId") String orderId,
                                 @RequestParam("status") Integer status) {
        hotelAdminService.updateRoomBookingStatus(orderId, status);
    }
}
