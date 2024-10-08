package cn.finetool.common.constant;


public class MqRoutingKey {

    /** ========== 充值方案(活动) 死信队列 路由 ========== */
    public static final String RECHARGE_PLAN_DLX_ROUTING_KEY = "recharge_plan_dlx_routing_key";

    /** ========== 充值方案(活动) 正常队列 路由 ========== */
    public static final String RECHARGE_PLAN_ROUTING_KEY = "recharge_plan_routing_key";

    /** ========== 订单 队列 路由 ========== */
    public static final String ORDER_ROUTING_KEY = "order_routing_key";

    /** ========== 房间预订订单 队列 路由 ========== */
    public static final String ROOM_RESERVE_ORDER_ROUTING_KEY = "room_reserve_order_routing_key";

}
