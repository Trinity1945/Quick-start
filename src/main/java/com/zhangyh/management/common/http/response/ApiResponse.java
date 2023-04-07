package com.zhangyh.management.common.http.response;


import com.zhangyh.management.common.constants.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyh
 * @Date 2023/4/3 17:55
 * @desc
 */
@Data
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 31415926535L;
    private String msg;

    private int code;

    private T data;

    public ApiResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public ApiResponse(String msg, int code, T data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public ApiResponse(ErrorCode errorCode) {
        this(errorCode.getMessage(),errorCode.getCode(),null);
    }

    public ApiResponse(ErrorCode errorCode, T data) {
        this(errorCode.getMessage(),errorCode.getCode(),data);
    }

    public ApiResponse() {

    }
}
