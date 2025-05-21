package com.conwise.model;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private boolean ok;
    private int code;
    private String msg;
    private T data;

    // 默认构造方法
    public ApiResponse() {
    }

    // 带参数的构造方法
    public ApiResponse(boolean ok, int code, String msg, T data) {
        this.ok = ok;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // 成功响应的静态工厂方法
    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(true, 1000, "操作成功", null);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, 1000, "操作成功", data);
    }

    public static <T> ApiResponse<T> ok(String msg, T data) {
        return new ApiResponse<>(true, 1000, msg, data);
    }

    // 失败响应的静态工厂方法
    public static <T> ApiResponse<T> fail(int code, String msg) {
        return new ApiResponse<>(false, code, msg, null);
    }

    public static <T> ApiResponse<T> fail(ResponseCode responseCode) {
        return new ApiResponse<>(false, responseCode.getCode(), responseCode.getMessage(), null);
    }

    // 链式设置方法
    public ApiResponse<T> withCode(int code) {
        this.code = code;
        return this;
    }

    public ApiResponse<T> withMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public ApiResponse<T> withData(T data) {
        this.data = data;
        return this;
    }

    public ApiResponse<T> withOk(boolean ok) {
        this.ok = ok;
        return this;
    }
}