package cn.finetool.hotel.api;

import cn.finetool.hotel.service.RoomDateService;
import cn.finetool.hotel.service.RoomService;
import jakarta.annotation.Resource;
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

}
