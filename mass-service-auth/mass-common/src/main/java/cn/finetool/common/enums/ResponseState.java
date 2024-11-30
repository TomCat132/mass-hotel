package cn.finetool.common.enums;


/**
 * 响应错误码
 */
public enum ResponseState {
    SUCCESS(200, "成功"),
    ERROR(400, "失败"),
    UNKNOWN(-2, "未知错误");

    private int code;
    private String errorMsg;

    ResponseState(int code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
