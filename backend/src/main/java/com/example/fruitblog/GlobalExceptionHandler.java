package com.example.fruitblog;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常处理：把业务异常转成友好的 JSON 错误信息。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务校验失败（注册查重、参数不合法、无权限等） */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorBody> handleIllegal(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorBody(e.getMessage()));
    }

    /** 登录失败（这里没用到，保留兜底） */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorBody> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorBody("用户名或密码错误"));
    }

    /** 兜底：未知异常（但保留 ResponseStatusException 自带的状态码，如 404/403） */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> handleOther(Exception e) {
        if (e instanceof org.springframework.web.server.ResponseStatusException rse) {
            return ResponseEntity.status(rse.getStatusCode())
                    .body(new ErrorBody(rse.getReason()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorBody("服务器内部错误"));
    }

    /** 简单错误体 */
    public record ErrorBody(String message) {}
}
