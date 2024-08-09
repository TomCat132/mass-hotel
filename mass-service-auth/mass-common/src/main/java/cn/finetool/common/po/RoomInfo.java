package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Map;

@Data
@TableName(value = "tb_room_info")
public class RoomInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 房间信息Id
     */
    private Integer roomId;

    /**
     * 具体房间Id
     */
    private String roomInfoId;

    /**
     * 房间状态 0-空闲 1-预约 2-使用中 3-维修中 4-空置 5-报废
     */
    private Integer status;
}
