package cn.finetool.common.enums;


/**
 * description: 业务错误码
 */
public enum BusinessErrors {

    RUNTIME_ERROR(400, "运行时错误"),

    DATA_DUPLICATION(1001, "数据重复"),
    DATA_NOT_EXIST(1002, "数据不存在"),
    DATA_STATUS_ERROR(1003, "数据状态错误"),
    PARAM_CANNOT_EMPTY(1004, "参数不能为空"),
    DATA_NOT_MATCH(1005, "数据不匹配"),
    SYSTEM_ERROR(1006, "系统错误"),
    AUTHENTICATION_ERROR(1007, "参数校验错误"),


    ACCOUNT_NOT_ENOUGH(2001, "账户余额不足"),
    ACCOUNT_NOT_EXIST(2002, "账户不存在"),
    ACCOUNT_LOCKED(2003, "账户被锁定"),



    TOKEN_IS_INVALID(3001, "Token已经失效，重新登录"),

    PERMISSION_DENIED(3003, "权限不足"),




    ORDER_CREATE_REQUEST_FAIL(7001, "订单创建请求失败"),


    IMAGE_CONVERT_ERROR(8001, "图片转换失败"),

    ALI_PAY_ERROR( 9001, "支付宝支付失败"),
    ALI_PAY_VERIFY_ERROR( 9002, "支付宝支付异步验证失败"),
    ;

    //错误码
    private int code;
    //具体错误信息
    private String msg;

    BusinessErrors(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
