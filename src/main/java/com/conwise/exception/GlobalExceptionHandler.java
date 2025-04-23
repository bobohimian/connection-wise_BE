package com.conwise.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * 全局异常处理器
 * 用于统一处理应用程序中的异常，并返回标准化的API响应
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理IllegalArgumentException异常
     * @param ex 异常
     * @param request 请求
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * 处理所有其他未捕获的异常
     * @param ex 异常
     * @param request 请求
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(
            Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().body("An unexpected error occurred: " + ex.getMessage());
    }
}