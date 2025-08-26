package com.example.project.common.ex;

import com.example.project.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResponseEntity<Result<Object>> handleBusinessException(BusinessException ex) {
        String msg = ex.getMessage();
        Integer code = ex.getCode();

        HttpStatus status = HttpStatus.BAD_REQUEST;
        if ("token已过期".equals(msg) || "未登录".equals(msg)) {
            status = HttpStatus.UNAUTHORIZED; // 401
        } else if (code != null) {
            if (code >= 400 && code < 600) {
                HttpStatus resolved = HttpStatus.resolve(code);
                status = resolved != null ? resolved : HttpStatus.BAD_REQUEST;
            }
        }

        Result<Object> body = Result.fail(code != null ? code : status.value(), msg != null ? msg : "业务异常");
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Result<Object>> handleException(Exception ex) {
        Result<Object> body = Result.fail(500, ex.getMessage() != null ? ex.getMessage() : "服务器错误");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
