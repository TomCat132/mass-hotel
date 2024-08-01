package cn.finetool.common.enums;

import lombok.Getter;

@Getter
public enum PayType {
    ALI_PAY(0,"支付宝支付"),

    WX_PAY(1,"微信支付");

    private final int code;
    private final String msg;

    PayType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
