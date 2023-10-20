package com.wenxin.sm.entity;

import lombok.Data;

enum Code {
    // 请求成功
    Success(200),
    // 请求失败
    Error(400),
    // 未登录
    NoLogin(401),
    // 服务器错误异常
    Exception(500);

    public final int value;

    Code(int value) {
        this.value = value;
    }
}

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public Result() {
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(Code.Success.value, message, data);
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(Code.Success.value, message);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(Code.Error.value, message);
    }

    public static <T> Result<T> IllegalRequest() {
        return new Result<>(Code.Error.value, "非法请求");
    }

    public static <T> Result<T> NoLogin() {
        return new Result<>(Code.NoLogin.value, "用户未登录或登录信息已过期");
    }

    public static <T> Result<T> Exception() {
        return new Result<>(Code.Exception.value, "服务器异常");
    }
}
