package cn.finetool.api.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "mass-hotel-service", path = "/hotel/api")
public interface HotelAPIService {

    @GetMapping("/queryResidualRoomInfo")
    Integer queryResidualRoomInfo(@RequestParam("roomId") String roomId,
                                  @RequestParam("date") LocalDate localDate);
}
