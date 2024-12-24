package cn.finetool.common.enums;

import lombok.Getter;

@Getter
public enum VoucherType {

    COUPON(0, "优惠券"),
    SYSTEM(1,"系统券"),
    CONSUME(2,"消费券"),
    SKILL(3,"秒杀券"),
    ;

    //错误码
    private final int code;
    //具体错误信息
    private final String msg;

    VoucherType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public Integer code(){
        return this.code;
    }
    
    public String desc(){
        return this.msg;
    }
    
    public static String toDesc(int code){
        for(VoucherType voucherType : VoucherType.values()){
            if(voucherType.code() == code){
                return voucherType.desc();
            }
        }
        return "未知";
    }

}
