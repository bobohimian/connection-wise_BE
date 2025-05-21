package com.conwise.model;

/**
 * API 响应状态码枚举类
 */
public enum ResponseCode {

    // 通用状态码 (1000-1999)
    SUCCESS(1000, "操作成功"),
    BAD_REQUEST(1001, "请求参数错误"),
    UNAUTHORIZED(1002, "未授权，请登录"),
    FORBIDDEN(1003, "无权限访问"),
    NOT_FOUND(1004, "资源未找到"),
    SERVER_ERROR(1005, "服务器内部错误"),
    OPERATION_FAILED(1006, "操作失败"),

    // 用户相关状态码 (2000-2999)
    USER_LOGIN_SUCCESS(2000, "登录成功"),
    USER_LOGIN_FAILED(2001, "登录失败，用户名或密码错误"),
    USER_REGISTER_SUCCESS(2002, "注册成功"),
    USER_REGISTER_FAILED(2003, "注册失败"),
    USER_ALREADY_EXISTS(2004, "用户已存在"),
    USER_NOT_FOUND(2005, "用户不存在"),
    USER_PASSWORD_ERROR(2006, "密码错误"),
    USER_SESSION_EXPIRED(2007, "会话已过期，请重新登录"),
    USER_EMAIL_ALREADY_USED(2004, "用户已存在"),



    // 画布相关状态码 (3000-3999)
    CANVAS_CREATE_SUCCESS(3000, "画布创建成功"),
    CANVAS_CREATE_FAILED(3001, "画布创建失败"),
    CANVAS_UPDATE_SUCCESS(3002, "画布更新成功"),
    CANVAS_UPDATE_FAILED(3003, "画布更新失败"),
    CANVAS_DELETE_SUCCESS(3004, "画布删除成功"),
    CANVAS_DELETE_FAILED(3005, "画布删除失败"),
    CANVAS_NOT_FOUND(3006, "画布不存在"),
    CANVAS_PERMISSION_DENIED(3007, "无权限操作该画布"),
    CANVAS_QUERY_SUCCESS(3008, "画布查询成功"),
    CANVAS_QUERY_FAILED(3009, "画布查询失败"),

    // 画布分享相关状态码 (4000-4999)
    CANVAS_SHARE_SUCCESS(4000, "画布分享成功"),
    CANVAS_SHARE_FAILED(4001, "画布分享失败"),
    CANVAS_SHARE_MODIFY_PERMISSION_FAILED(4002, "修改权限失败"),
    CANVAS_SHARE_PERMISSION_DENIED(4003, "无权限查看共享画布"),
    CANVAS_SHARE_ALREADY_EXISTS(4004, "画布已分享"),
    CANVAS_SHARE_USER_NOT_FOUNT(4005,"分享用户不存在"),

    // WebSocket 相关状态码 (5000-5999)
    WS_CONNECT_SUCCESS(5000, "WebSocket 连接成功"),
    WS_CONNECT_FAILED(5001, "WebSocket 连接失败"),
    WS_DISCONNECTED(5002, "WebSocket 连接已断开"),
    WS_MESSAGE_SEND_SUCCESS(5003, "消息发送成功"),
    WS_MESSAGE_SEND_FAILED(5004, "消息发送失败"),
    WS_PERMISSION_DENIED(5005, "无权限加入该画布会话"),
    WS_Canvas_NOT_FOUND(5006, "WebSocket 会话对应的画布不存在");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据状态码获取枚举对象
     * @param code 状态码
     * @return 对应的 ResponseCode 枚举，若无匹配则返回 null
     */
    public static ResponseCode fromCode(int code) {
        for (ResponseCode responseCode : values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }
    }