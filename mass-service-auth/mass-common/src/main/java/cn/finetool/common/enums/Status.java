package cn.finetool.common.enums;

import lombok.Getter;

@Getter
public enum Status {

    RECHARGE_PLAN_UP(0,"上架中"),
    RECHARGE_PLAN_DOWN(1,"已下架"),

    ROOM_DATE_CAN_USE(0,"可预定"),
    ROOM_DATE_RESERVED(1,"已预定"),
    ROOM_DATE_CHECK_IN(2,"已入住"),
    ROOM_DATE_CHECK_OUT(3,"已离店"),

    ORDER_WAIT(0,"待支付"),
    ORDER_SUCCESS(1,"支付成功"),
    ORDER_FAIL(2,"支付失败"),
    ORDER_CANCEL(3,"已取消"),
    ORDER_REFUND(4,"退款中"),
    ORDER_REFUND_SUCCESS(5,"退款成功"),
    ORDER_REFUND_FAIL(6,"退款失败"),
    ORDER_CLOSE(7,"订单失效"),
    ;

    private final int code;
    private final String desc;

    Status(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
