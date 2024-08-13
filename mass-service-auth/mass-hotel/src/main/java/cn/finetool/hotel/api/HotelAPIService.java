package cn.finetool.hotel.api;

import cn.finetool.hotel.service.RoomService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/hotel/api")
public class HotelAPIService {

    @Resource
    private RoomService roomService;

    /** ========== 查询该天日期房间类型的具体剩余数量 ===========*/
    @GetMapping("/queryResidualRoomInfo")
    public List<Integer> queryResidualRoomInfo(@RequestParam("roomId") String roomId,
                                               @RequestParam("checkInDate")LocalDate checkInDate,
                                               @RequestParam("checkOutDate") LocalDate checkOutDate){
        return roomService.queryResidualRoomInfo(roomId,checkInDate,checkOutDate);
    }

}
