package cn.finetool.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class QueryRoomTypeDto implements Serializable {

    /**
     * 酒店名称
     */
    private Integer hotelId;

    /**
     * 入住时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate checkInDate;

    /**
     * 离开时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate checkOutDate;
}
