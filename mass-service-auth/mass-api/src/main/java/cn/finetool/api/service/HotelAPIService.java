package cn.finetool.api.service;


import cn.finetool.common.configuration.MultipartSupportConfig;
import cn.finetool.common.vo.RoomOrderBaseInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

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
}
