package cn.finetool.common.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum Status {

    /**
     * 旅店状态
     */
    HOTEL_RUNNING(0, "营业中"),
    HOTEL_CERTIFICATION(1, "已通过"),
    HOTEL_REJECT(2, "不合格"),
    HOTEL_CHECK(3, "待审核"),
    HOTEL_CLOSE(4, "已关闭"),
    HOTEL_RECTIFICATION(5, "整顿中"),
    /**
     * 旅店类型:hotel_type
     * */
    TYPE_HOTEL(0, "酒店"),
    TYPE_APARTMENT(1, "公寓"),
    TYPE_HOSTEL(3, "民宿"),
    /**
     * 充值计划 状态
     */
    RECHARGE_PLAN_UP(0, "上架中"),
    RECHARGE_PLAN_DOWN(1, "已下架"),
    RECHARGE_PLAN_MAINTAIN(2, "维护中"),
    /**
     * 具体房间日期 状态
     */
    ROOM_DATE_CAN_USE(0, "可预定"),
    ROOM_DATE_RESERVED(1, "已预定"),
    ROOM_DATE_CAN_NOT_USE(2, "不可预定"),
    ROOM_DATE_EXPIRED(3, "已过期"),
    /**
     * 订单状态
     */
    ORDER_WAIT(0, "待支付"),
    ORDER_SUCCESS(1, "支付成功"),
    ORDER_FAIL(2, "支付失败"),
    ORDER_CANCEL(3, "已取消"),
    ORDER_REFUND(4, "退款中"),
    ORDER_REFUND_SUCCESS(5, "退款成功"),
    ORDER_REFUND_FAIL(6, "退款失败"),
    ORDER_CLOSE(7, "订单失效"),
    /**
     * 优惠券状态
     */
    VOUCHER_PREPARE(0, "待发放"),
    VOUCHER_UP(1, "已上架"),
    VOUCHER_DOWN(2, "已下架"),
    VOUCHER_CAN_USE(2, "可使用"),
    VOUCHER_USED(3, "已使用"),
    VOUCHER_EXPIRED(4, "已过期"),
    VOUCHER_INVALID(5, "无效"),
    VOUCHER_COLD(6, "已冻结"),
    /**
     * 房间入住表  状态：0：已预定 1：办理中 2：入住中 3：已取消 4：已退房 5:已更换 6:异常(超时) 7:异常(未归还门禁卡) 8:退房待确认 
     */
    ROOMBOOKING_RESERVED(0, "已预定"),
    ROOMBOOKING_DOING(1, "办理中"),
    ROOMBOOKING_CHECK_IN(2, "入住中"),
    ROOMBOOKING_CANCEL(3, "已取消"),
    ROOMBOOKING_CHECK_OUT(4, "已退房"),
    ROOMBOOKING_CHANGE(5, "已更换"),
    ROOMBOOKING_TIMEOUT(6, "异常(超时)"),
    ROOMBOOKING_CHECK_OUT_WAIT_CONFIRM(7, "异常(未归还门禁卡)"),
    ROOMBOOKING_CHECK_OUT_WAIT_CHECK(8, "退房待确认"),
    ROOMBOOKING_OFFLINE_RESERVED(9, "已预定（线下办理入住）"),

    /**
     * 预定信息子状态  0: 未使用 1: 线上办理 2：线下办理
     */
    ROOMBOOKING_SUB_STATUS_NOT_USE(0, "未使用"),
    ROOMBOOKING_SUB_STATUS_ONLINE(1, "线上办理"),
    ROOMBOOKING_SUB_STATUS_OFFLINE(2, "线下办理"),
    /**
     * 查询方式
     */
    QUERY_ORDER(0, "订单查询"),
    QUERY_PHONE(1, "手机号查询"),
    /**
     * 按钮状态
     */
    CAN_USE(0, "启用"),
    BAN_USE(1, "禁用"),

    IS_DELETED(1, "已删除"),
    NOT_DELETED(0, "未删除"),

    /**
     * 房间门类型: 0:门禁卡 1：密码门
     */
    ROOM_DOOR_TYPE_CARD(0, "门禁卡"),
    ROOM_DOOR_TYPE_PASSWORD(1, "密码门"),
    
    ROOM_INFO_CAN_NOT_USE(0, "不可用"),
    ROOM_INFO_CAN_USE(1, "可用"),
    ROOM_INFO_CLEANING(2, "清洁中"),
    
    MESSAGE_UNREAD(0, "未读"),
    MESSAGE_READ(1, "已读"),

    ACCOUNT_OFFLINE(0, "离线"),
    ACCOUNT_ONLINE(1, "在线"),
    // user_merchant
    ACCOUNT_CLOD(1, "已冻结"),
    ACCOUNT_NORMAL(0, "在职"),
    ACCOUNT_RESIGNED(2, "离职"),

    /**
     * 评价状态
     */
    EVALUATION_NO(0, "未评价"),
    EVALUATION_YES(1, "已评价"),

    /**
     * 请求状态
     */
    REQUEST_NOT(0, "未处理"),
    REQUEST_DOING(1, "处理中"),
    REQUEST_DONE(2, "已处理"),
    REQUEST_FAIL(3, "已超期"),
    REQUEST_CANT_DO(4, "无法处理"),
    /**
     * 会员等级
     */
    MEMBER_BRONZE(0, "青铜会员"),
    MEMBER_SILVER(1, "白银会员"),
    MEMBER_GOLD(2, "黄金会员"),
    MEMBER_PLATINUM(3, "铂金会员"),
    MEMBER_DIAMOND(4, "钻石会员"),

    /**
     * 活动类型
     */
    ACTIVITY_VOUCHER(0, "活动券"),

    /**
     * 状态 0 待上架，1 可兑换，2 已售罄 3.已结束
     */
    POINT_EXCHANGE_WAIT(0, "待上架"),
    POINT_EXCHANGE_CAN_EXCHANGE(1, "可兑换"),
    POINT_EXCHANGE_SOLD_OUT(2, "已售罄"),
    POINT_EXCHANGE_END(3, "已结束"),
    
    POINT_USAGE_EXCHANGED(0, "待使用"),
    POINT_USAGE_USED(1, "已使用"),
    POINT_USAGE_EXPIRED(2, "已过期"),
    POINT_USAGE_CANT_USE(3, "不可用"),
    ;

    private final int code;
    private final String desc;

    Status(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int code() {
        return this.code;
    }
    
    public String desc() {
        return this.desc;
    }

    /**
     * 订单状态 code -> desc
     * @param code
     * @return
     */
    public static String OrderToDesc(int code){
        Map<Integer, String> map = new HashMap<>();
        // 填充订单状态枚举（固定值）
        map.put(0, "待支付");
        map.put(1, "支付成功");
        map.put(2, "支付失败");
        map.put(3, "已取消");
        map.put(4, "退款中");
        map.put(5, "退款成功");
        map.put(6, "退款失败");
        map.put(7, "订单失效");
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getKey() == code) {
                return entry.getValue();
            }
        }
        return "未知状态";
    }
}
