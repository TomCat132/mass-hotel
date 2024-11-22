package cn.finetool.common.constant;

public class RedisCache {

    /** =========== 用户签到表缓存标记 ========== */
    public static final String USER_SIGN_TABLE = "||user_sign_table_userId:";

    /** =========== 用户签到表队列 ========== */
    public static final String USER_SIGN_KEY_PREFIX = "user_sign_key_userId:";

    /** =========== 有效充值方案列表缓存 ========== */
    public static final String VALID_RECHARGE_PLAN_LIST = "valid_recharge_plan_list";

    /** =========== 充值订单超时标记 ========== */
    public static final String RECHARGE_ORDER_IS_TIMEOUT = "recharge_order_is_timeout_order_id:";

    /** =========== 充值订单过期时间：5分钟 ========== */
    public static final Integer RECHARGE_ORDER_EXPIRATION_TIME = 300000;

    /** =========== 酒店 GEO 位置列表缓存 ========== */
    public static final String HOTEL_LOCATION_LIST = "hotel_location_list";

    /** =========== 酒店 ID 前缀 ========== */
    public static final String HOTEL_ID_PREFIX = "hotel_id:";

    /** =========== 房间订单锁 ========== */
    public static final String ROOM_ORDER_LOCK = "room_order_lock";

    /** =========== 房间预订订单前缀 ========== */
    public static final String ROOM_RESERVED_ORDER_IS_TIMEOUT = "room_reserved_order_is_timeout_order_id:";

    /** =========== 预定锁定房间分布式锁 （roomId）======== */
    public static final String RESERVE_ROOM = "reserve_room_roomId:";

    /** =========== 用户酒店绑定表缓存标记 ========== */
    public static final String USER_HOTEL_BINDING = "user_hotel_binding_userId:";

}
