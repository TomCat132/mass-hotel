package cn.finetool.common.enums;

import lombok.Getter;

@Getter
public enum Status {

    RECHARGE_PLAN_UP(0,"上架中"),
    RECHARGE_PLAN_DOWN(1,"已下架"),

    ORDER_WAIT(0,"待支付"),
    ORDER_SUCCESS(1,"支付成功"),
    ORDER_FAIL(2,"支付失败"),
    ORDER_CANCEL(3,"取消订单"),
    ORDER_REFUND(4,"退款中"),
    ORDER_REFUND_SUCCESS(5,"退款成功"),
    ORDER_REFUND_FAIL(6,"退款失败"),
    ORDER_FINISH(7,"订单完成"),
    ORDER_CLOSE(8,"订单失效"),

    ;

    private final int code;
    private final String desc;

    Status(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
