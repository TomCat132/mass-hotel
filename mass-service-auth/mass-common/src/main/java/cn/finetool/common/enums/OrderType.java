package cn.finetool.common.enums;

/**
 * description: 订单类型枚举
 */
public enum OrderType {
    RECHARGE_ORDER(0, "充值订单"), // 充值类型订单

    CONSUME_ROOM_ORDER(1, "入驻订单"); // 入驻类型订单

    private int value;
    private String desc;

    OrderType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static OrderType valueOf(int value) {
        for (OrderType orderType : OrderType.values()) {
            if (orderType.getValue() == value) {
                return orderType;
            }
        }
        return null;
    }

}

