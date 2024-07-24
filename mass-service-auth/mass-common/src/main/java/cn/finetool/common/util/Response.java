package cn.finetool.common.util;




import cn.finetool.common.enums.BusinessErrors;
import cn.finetool.common.enums.ResponseState;
import cn.finetool.common.exception.BusinessRuntimeException;

import java.io.Serializable;

public class Response<T> implements Serializable {

    public Response() {

    }

    public static Response build(ResponseState state) {
        Response response = new Response();
        response.setCode(state.getCode());
        return response;
    }


    public static Response success(Object data) {
        Response response = build(ResponseState.SUCCESS);
        response.setData(data);
        return response;
    }

    public static Response success(Object data, String message) {
        Response response = build(ResponseState.SUCCESS);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static Response error(int code,String message){
        Response response = new Response();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    public static Response error(String message) {
        Response response = build(ResponseState.ERROR);
        response.setMessage(message);
        return response;
    }

    public static Response error(BusinessRuntimeException exception) {
        Response response = build(ResponseState.ERROR);
        response.setMessage(exception.getMessage());
        response.setCode(exception.getBusinessError().getCode());
        return response;
    }

    public static Response error(BusinessErrors errors) {
        Response response = build(ResponseState.ERROR);
        response.setMessage(errors.getMsg());
        response.setCode(errors.getCode());
        return response;
    }


    public static Response unknown(String message) {
        Response response = build(ResponseState.UNKNOWN);
        response.setMessage(message);
        return response;
    }

    private int code;

    private Object data = null;

    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
