package com.zhangyh.management.common.exception;


import com.zhangyh.management.common.constants.ErrorCode;
import com.zhangyh.management.common.http.response.ApiResponse;
import com.zhangyh.management.common.http.response.ResponseHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangyh
 * @Date 2023/4/3 18:00
 * @desc
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 统一异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ApiResponse<Object> exception(Exception e) {
        log.error("unknown error: ", e);
        return ResponseHelper.failed(ErrorCode.SYSTEM_ERROR);
    }

    @ExceptionHandler(value= BindException.class)
    public ApiResponse<Object> bindException(BindException bindException){
        BindingResult result = bindException.getBindingResult();
        if(result.hasErrors()){
            List<String> errorMessages = result.getAllErrors().stream().map(objectError -> {
                FieldError fieldError = (FieldError) objectError;
                return fieldError.getField() + "-->" + fieldError.getDefaultMessage();
            }).collect(Collectors.toList());
            return ResponseHelper.failed(ErrorCode.INVALID_PARAMS,errorMessages);
        }
        return ResponseHelper.failed(ErrorCode.INVALID_PARAMS);
    }

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage(), e);
        return ResponseHelper.failed(e.getCode(), e.getMessage());
    }

}
