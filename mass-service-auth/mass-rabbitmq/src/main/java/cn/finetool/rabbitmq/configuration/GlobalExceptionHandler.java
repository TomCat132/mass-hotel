package cn.finetool.rabbitmq.configuration;

import cn.dev33.satoken.exception.NotRoleException;
import cn.finetool.common.exception.BusinessRuntimeException;
import cn.finetool.common.util.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BusinessRuntimeException.class)
    @ResponseBody
    public Response businessRunTimeException(HttpServletRequest req,BusinessRuntimeException e) {
        if (e.isHasBusinessError()){
            return Response.error(e.getBusinessError().getCode(),e.getMessage());
        }
        return Response.error(e.getMessage());
    }

    @ExceptionHandler(value = NotRoleException.class)
    @ResponseBody
    public Response businessRunTimeException(HttpServletRequest req,Exception e) {
        return Response.error("权限不足");
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public Response methodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException e) {
        // 获取所有异常
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        return Response.error(String.join(",", errors));
    }

}
