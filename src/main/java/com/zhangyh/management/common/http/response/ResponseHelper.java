package com.zhangyh.management.common.http.response;


import com.zhangyh.management.common.constants.ErrorCode;

/**
 * @author zhangyh
 * @Date 2023/4/3 17:56
 * @desc
 */
public class ResponseHelper {

    public static <T> ApiResponse<T> success(){
        return new ApiResponse<T>(ErrorCode.SUCCESS);
    }

    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<T>(ErrorCode.SUCCESS,data);
    }

    public static <T> ApiResponse<T> failed(){
        return new ApiResponse<T>(ErrorCode.SYSTEM_ERROR);
    }

    public static <T> ApiResponse<T> failed(ErrorCode errorCode){
        return new ApiResponse<>(errorCode);
    }

    public static <T> ApiResponse<T> failed(ErrorCode errorCode,T data){
        return new ApiResponse<>(errorCode,data);
    }

    public static <T> ApiResponse<T> failed(int code,String msg){
        return new ApiResponse<>(msg,code,null);
    }
}
