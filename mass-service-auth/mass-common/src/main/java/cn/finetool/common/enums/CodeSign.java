package cn.finetool.common.enums;


import lombok.Getter;

@Getter
public enum CodeSign {


    UserPrefix(1010, "用户前缀标志"),
    RoomTypePrefix(1011, "房间类型前缀标志"),
    VoucherPrefix(1012, "活动优惠券前缀标志"),
    RechargeOrderPrefix(1013, "充值订单前缀标志"),
    HotelOrderPrefix(1014,"酒店预定订单前缀标志");

    private final int code;

    private final String desc;

    CodeSign(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
