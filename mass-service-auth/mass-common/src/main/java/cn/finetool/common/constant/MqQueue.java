package cn.finetool.common.constant;

public class MqQueue {

    /** ======== 充值方案(活动) 队列 ======== */
    public static final String RECHARGE_PLAN_QUEUE = "recharge_plan_queue";

    /** ======== 充值方案(活动) 死信队列 ======== */
    public static final String RECHARGE_PLAN_DLX_QUEUE = "recharge_plan_dlx_queue";

    /** ======== 订单队列 ======== */
    public static final String RECHARGE_ORDER_QUEUE = "order_queue";

    /** ======== 房间预订订单队列 ======== */
    public static final String ROOM_RESERVE_ORDER_QUEUE = "room_reserve_order_queue";
    
    /** ======== 活动券有效期队列 ======== */
    public static final String VOUCHER_UP_QUEUE = "voucher_up_queue";
    
    /** ======== 活动券失效队列 ======== */
    public static final String VOUCHER_DOWN_QUEUE = "voucher_down_queue";
}
