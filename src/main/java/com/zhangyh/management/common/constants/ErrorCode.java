package com.zhangyh.management.common.constants;

/**
 * @author zhangyh
 * @Date 2023/4/3 17:57
 * @desc
 */
public enum ErrorCode {
    //4000为校验错误
    SUCCESS(200,"OK"),
    INVALID_PARAMS(4001,"非法参数"),
    BUSINESS_ERROR(4002,"请求错误"),
    MISSING_PARAMS(4003,"参数缺失"),
    NOT_EXIST_USER(4004,"用户不存在"),
    DATA_NOT_EXIST(4005,"数据不存在"),
    VERIFY_CODE_ERROR(4006,"验证码错误"),
    UNLOGIN(4007,"用户未登录"),
    NO_AUTH_ERROR(4008,"无权限" ),
    PASSWORD_ERROR(4009,"密码错误" ),
    ACCOUNT_DUPLICATE(4010,"账号重复" ),

    //5000为系统异常
    SYSTEM_ERROR(5000, "系统内部异常"),
    OPERATION_ERROR(5001, "操作失败");

    private final String message;

    private final int code;

    ErrorCode(int code,String message) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
