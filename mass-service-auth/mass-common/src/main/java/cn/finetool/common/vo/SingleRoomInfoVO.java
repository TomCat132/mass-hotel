package cn.finetool.common.vo;

import cn.finetool.common.po.RoomDate;
import cn.finetool.common.po.RoomInfo;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SingleRoomInfoVO extends RoomInfo {

    /**
     * roomInfo
     */
    private RoomInfo roomInfo;

    /**
     * 房间 具体日期使用情况
     */
    private List<RoomDate> roomDateList;
}
