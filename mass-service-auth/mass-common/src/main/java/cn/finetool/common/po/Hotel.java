package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 酒店信息表
 */
@Data
@TableName("tb_hotel")
public class Hotel {

    /**
     * 主键id
     */
    @TableId(value = "hotel_id", type = IdType.AUTO)
    private Integer hotelId;

    /**
     * 酒店编号
     */
    private String merchantId;

    /**
     * 酒店名称
     */
    private String hotelName;

    /**
     * 酒店地址
     */
    private String address;

    /**
     * 城市
     */
    private String city;

    /**
     * 酒店星级
     */
    private Integer starRating;

    /**
     * 酒店电话
     */
    private String phoneNumber;

    /**
     * 消费次数
     */
    private Integer liveCount;

    /**
     * 酒店类型
     */
    private Integer hotelType;

    /**
     * 酒店位置经度
     */
    @TableField(value = "hotel_lng")
    private Double hotelLng;

    /**
     * 酒店位置纬度
     */
    @TableField(value = "hotel_lat")
    private Double hotelLat;

    /**
     * 状态
     */
    private Integer status;

    /**
     * RoomList 房间类型列表
     */
    @TableField(exist = false)
    private List<Room> roomList = new ArrayList<>();


    public void putRoomList(List<Room> roomList) {
        this.roomList.addAll(roomList);
    }
}
