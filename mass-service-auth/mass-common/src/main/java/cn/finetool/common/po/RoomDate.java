package cn.finetool.common.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
@TableName(value = "tb_room_date")
public class RoomDate {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 房间Id主键 (room_info的主键)
     */
    private Integer riId;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate date;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 状态： 0：未预定 1：已预定 2：已入住 3: 打扫中
     */
    private Integer status;

    /**
     * 临时id，不存储到数据库
     */
    @TableField(exist = false)
    private String tempRoomId;
}
