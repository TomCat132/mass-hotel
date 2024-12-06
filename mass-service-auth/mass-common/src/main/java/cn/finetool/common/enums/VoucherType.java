package cn.finetool.common.enums;

import lombok.Getter;

@Getter
public enum VoucherType {

    COUPON(0, "优惠券"),
    CONSUME(1,"消费券"),
    SKILL(2,"秒杀券"),
    SYSTEM(3,"系统券")
    ;

    //错误码
    private final int code;
    //具体错误信息
    private final String msg;

    VoucherType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
