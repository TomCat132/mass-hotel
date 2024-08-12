package cn.finetool.hotel.api;

import cn.finetool.hotel.service.RoomService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/hotel/api")
public class HotelAPIService {

    @Resource
    private RoomService roomService;

    /** ========== 查询该天日期房间类型的具体剩余数量 ===========*/
    @GetMapping("/queryResidualRoomInfo")
    public Integer queryResidualRoomInfo(@RequestParam("roomId") String roomId,
                                         @RequestParam("date")LocalDate date){
        return roomService.queryResidualRoomInfo(roomId, date);
    }

}
