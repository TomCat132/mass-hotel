package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "tb_room")
public class Room implements Serializable {

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 房间ID
     */
    private String roomId;

    /**
     * 房间名称
     */
    private String roomName;

    /**
     * 酒店Id
     */
    private Integer hotelId;

    /**
     * 房间 类型
     */
    private Integer roomType;

    /**
     * 床信息模块（bedInfo）：bedType：床型 bedNum：床数 bedSize：床面积
     * 设施模块（facilities）
     * roomSize：房间大小 , floor：楼层
     * bathroom：卫生间 kitchen：厨房
     * balcony：阳台 breakfast：早餐
     * wifi：有无 tv：电视v airConditioning：空调 parking：停车位
     */
    private String roomDesc;

    /**
     * 房间基础定价
     */
    private BigDecimal basicPrice;

    /**
     * 非数据库字段
     */
    @TableField(exist = false)
    private List<RoomInfo> roomInfoList = new ArrayList<>();

    public void addAll(List<RoomInfo> roomInfoList) {
        this.roomInfoList.addAll(roomInfoList);
    }
}
