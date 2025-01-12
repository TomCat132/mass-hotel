package cn.finetool.common.enums;


import lombok.Getter;

@Getter
public enum SysEnum {

    MERCHANT_HOTEL_PREFIX("1001", "酒店前缀标志"),
    ROOM_INFO_ID_PREFIX("1002", "具体房间信息ID前缀标志"),
    EVALUATION_PREFIX("1003", "评价前缀标志"),
    CHAT_RECORD_PREFIX("1004", "聊天记录前缀标志"),
    CHAT_MESSAGE_PREFIX("1005", "聊天消息前缀标志"),
    USER_PREFIX("1010", "用户前缀标志"),
    ROOM_PREFIX("1011", "房间类型前缀标志"),
    VOUCHER_PREFIX("1012", "活动优惠券前缀标志"),
    RECHARGE_ORDER_PREFIX("1013", "充值订单前缀标志"),
    ROOM_ORDER_PREFIX("1014","酒店预定订单前缀标志"),
    FILE_PREFIX("1015", "文件前缀标志"),
    FILE_PATH_PREFIX("1016", "文件路径前缀标志"),
    USER_REQUEST_PREFIX("1017", "用户请求前缀标志"),
    ;
    
    private final String code;
    private final String desc;
    


    SysEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String code(){
        return this.code;
    }
    
    public String desc(){
        return this.desc;
    }


    /**
     * 根据key得到枚举类型
     *
     * @param code
     * @return
     */
    public static SysEnum getEnumCategory(String code) {
        SysEnum[] enums = SysEnum.values();

        for (SysEnum enum1 : enums) {
            if (String.valueOf(enum1.code).equals(code)) {
                return enum1;
            }
        }

        return null;
    }
}
