package cn.finetool.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomInfoVo implements Serializable {


    /**
     * 房间类型编号
     */
    private String roomId;

    /**
     * 房间类型名称
     */
    private String roomName;

    /**
     * 房间类型
     */
    private Integer roomType;

    /**
     * 房间描述
     */
    private String roomDesc;

    /**
     * 房间图片
     */
    private String  roomAvatar;

    /**
     * 房间价格
     */
    private BigDecimal todayPrice;

    /**
     * 房间剩余数量
     */
    private Integer roomCount;
}
