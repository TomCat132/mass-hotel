package cn.finetool.common.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelVo implements Serializable {


    /**
     * 主键id
     */
    @TableId(value = "hotel_id", type = IdType.AUTO)
    private Integer hotelId;

    /**
     *  酒店名称
     */
    private String hotelName;

    /**
     *  酒店详细地址
     */
    private String address;

    /**
     *  酒店位置经度
     */
    @TableField(value = "hotel_lng")
    private Double hotelLng;

    /**
     * 酒店位置纬度
     */
    @TableField(value = "hotel_lat")
    private Double hotelLat;

    /**
     *  酒店地址
     */
    private Double distance;

}
