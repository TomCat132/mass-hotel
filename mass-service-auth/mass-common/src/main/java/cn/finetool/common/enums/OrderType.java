package cn.finetool.common.enums;

/**
 * description: 订单类型枚举
 */
public enum OrderType {
    RECHARGE_ORDER(0, "充值订单"), // 充值类型订单

    CONSUME_ROOM_ORDER(1, "入驻订单"); // 入驻类型订单

    private final int code;
    private final String desc;

    OrderType(int value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    public int code() {
        return this.code;
    }

    public String desc() {
        return this.desc;
    }
}

