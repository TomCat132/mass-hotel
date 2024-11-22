package cn.finetool.common.enums;

import lombok.Getter;

@Getter
public enum Status {

    /** 充值计划 状态 */
    RECHARGE_PLAN_UP(0,"上架中"),
    RECHARGE_PLAN_DOWN(1,"已下架"),
    RECHARGE_PLAN_MAINTAIN(2,"维护中"),
    /** 具体房间日期 状态 */
    ROOM_DATE_CAN_USE(0,"可预定"),
    ROOM_DATE_RESERVED(1,"已预定"),
    ROOM_DATE_CAN_NOT_USE(2,"不可预定"),
    ROOM_DATE_EXPIRED(3,"已过期"),
    /** 订单状态 */
    ORDER_WAIT(0,"待支付"),
    ORDER_SUCCESS(1,"支付成功"),
    ORDER_FAIL(2,"支付失败"),
    ORDER_CANCEL(3,"已取消"),
    ORDER_REFUND(4,"退款中"),
    ORDER_REFUND_SUCCESS(5,"退款成功"),
    ORDER_REFUND_FAIL(6,"退款失败"),
    ORDER_CLOSE(7,"订单失效"),
    /** 优惠券状态 */
    VOUCHER_PREPARE(0,"待发放"),
    VOUCHER_SEND(1,"已发放"),
    VOUCHER_CAN_USE(2,"可使用"),
    VOUCHER_USED(3,"已使用"),
    VOUCHER_EXPIRED(4,"已过期"),
    VOUCHER_INVALID(5,"无效"),
    /** 房间入住表  状态：0：已预定 1：办理中 2：入住中 3：已取消 4：已退房 5:已更换 */
    ROOMBOOKING_RESERVED(0,"已预定"),
    ROOMBOOKING_DOING(1,"办理中"),
    ROOMBOOKING_CHECK_IN(2,"入住中"),
    ROOMBOOKING_CANCEL(3,"已取消"),
    ROOMBOOKING_CHECK_OUT(4,"已退房"),
    ROOMBOOKING_CHANGE(5,"已更换"),
    /** 查询方式 */
    QUERY_ORDER(0,"订单查询"),
    QUERY_PHONE(1,"手机号查询"),
    /** 按钮状态 */
    CAN_USE(0,"启用"),
    BAN_USE(1,"禁用"),
    ;

    private final int code;
    private final String desc;

    Status(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
