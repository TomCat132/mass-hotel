package cn.finetool.common.constant;

public class MqExchange {

    /** ========= 充值方案(活动) 死信交换机 =========== */
    public static final String RECHARGE_PLAN_DLX_EXCHANGE = "recharge_plan_dlx_exchange";
    /** ========= 充值方案(活动) 交换机 =========== */
    public static final String RECHARGE_PLAN_EXCHANGE = "recharge_plan_exchange";
    /** ========= 订单交换机 =========== */
    public static final String ORDER_EXCHANGE = "order_exchange";
    /** ========= 房间预订订单交换机 =========== */
    public static final String ROOM_RESERVE_ORDER_EXCHANGE = "room_reserve_order_exchange";
    /** ========= 活动券上架交换机 =========== */
    public static final String VOUCHER_UP_EXCHANGE = "voucher_up_exchange";
    /** ========= 活动券下架交换机 =========== */
    public static final String VOUCHER_DOWN_EXCHANGE = "voucher_down_exchange";
    /** ========= 房间订单超时交换机 =========== */
    public static final String ROOM_BOOKING_TIMEOUT_EXCHANGE = "room_booking_timeout_exchange";
    /** ========= 房间订单即将结束提醒  交换机 =========== */
    public static final String ROOM_ORDER_ENDING_REMIND_EXCHANGE = "room_order_ending_remind_exchange";
}
