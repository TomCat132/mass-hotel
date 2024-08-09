package cn.finetool.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class RoomDto implements Serializable {

    /**
     * 房间名称
     */
    private String roomName;

    /**
     *  酒店Id
     */
    private Integer hotelId;

    /**
     * 房间 类型
     */
    private Integer roomType;

    /**
     床信息模块（bedInfo）：bedType：床型 bedNum：床数 bedSize：床面积
     设施模块（facilities）
     roomSize：房间大小 , floor：楼层
     bathroom：卫生间 kitchen：厨房
     balcony：阳台 breakfast：早餐
     wifi：有无 tv：电视v airConditioning：空调 parking：停车位
     */
    private Map<String,Object> roomDesc;

    /**
     * 房间基础定价
     */
    private BigDecimal basicPrice;
}
