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
}
