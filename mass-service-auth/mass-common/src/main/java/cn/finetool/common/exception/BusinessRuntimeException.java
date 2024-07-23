package cn.finetool.common.exception;

import cn.finetool.common.enums.BusinessErrors;
import lombok.Getter;

@Getter
public class BusinessRuntimeException extends RuntimeException{

    private BusinessErrors businessError;

    private final boolean hasBusinessError;

    public BusinessRuntimeException(String message) {
        super(message);
        this.hasBusinessError = false;
    }

    public BusinessRuntimeException(BusinessErrors businessError) {
        super(businessError.getMsg());
        this.businessError = businessError;
        this.hasBusinessError = true;
    }

    public BusinessRuntimeException(BusinessErrors businessError, String message) {
        super(message);
        this.businessError = businessError;
        this.hasBusinessError = true;
    }

}
