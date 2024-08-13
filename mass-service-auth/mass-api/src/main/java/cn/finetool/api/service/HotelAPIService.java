package cn.finetool.api.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "mass-hotel-service", path = "/hotel/api")
public interface HotelAPIService {

    @GetMapping("/queryResidualRoomInfo")
    List<Integer> queryResidualRoomInfo(@RequestParam("roomId") String roomId,
                                        @RequestParam("checkInDate") LocalDate checkInDate,
                                        @RequestParam("checkOutDate") LocalDate checkOutDate);
}
