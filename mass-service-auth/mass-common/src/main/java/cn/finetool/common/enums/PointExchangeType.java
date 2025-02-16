package cn.finetool.common.enums;

import lombok.Getter;

public enum PointExchangeType {

    VOUCHER(0, "活动券"),
    
    ;
    
    
    private final int code;
    private final String desc;

    PointExchangeType(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public int code() {
        return this.code;
    }

    public String desc() {
        return this.desc;
    }
}
