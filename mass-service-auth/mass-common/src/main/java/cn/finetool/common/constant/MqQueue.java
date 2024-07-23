package cn.finetool.common.constant;

public class MqQueue {

    /** ======== 充值方案(活动) 队列 ======== */
    public static final String RECHARGE_PLAN_QUEUE = "recharge_plan_queue";

    /** ======== 充值方案(活动) 死信队列 ======== */
    public static final String RECHARGE_PLAN_DLX_QUEUE = "recharge_plan_dlx_queue";

    /** ======== 订单队列 ======== */
    public static final String RECHARGE_ORDER_QUEUE = "order_queue";

    /** ======== 订单死信队列 ======== */
    public static final String RECHARGE_ORDER_DLX_QUEUE = "order_dlx_queue";
}
