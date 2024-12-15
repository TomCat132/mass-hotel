package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 房间信息表
 */
@Data
@TableName(value = "tb_room_info")
public class RoomInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 房间信息Id
     */
    private String roomId;

    /**
     * 房间号
     */
    private String roomInfoId;

    /**
     * 房间状态 0：不可用 1：可用
     */
    private Integer status;

    /**
     * 0:普通 1:密码
     */
    private Integer type;
}
