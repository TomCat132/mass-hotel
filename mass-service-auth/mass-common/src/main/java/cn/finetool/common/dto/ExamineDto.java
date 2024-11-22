package cn.finetool.common.dto;

import lombok.Data;

@Data
public class ExamineDto implements java.io.Serializable {

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 是否确认房间信息
     */
    private boolean isConfirmRoom;

    /**
     * 是否确认缴纳押金
     */
    private boolean isConfirmDeposit;


}
