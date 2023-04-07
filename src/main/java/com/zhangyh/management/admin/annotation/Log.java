package com.zhangyh.management.admin.annotation;

import java.lang.annotation.*;

/**
 * @author zhangyh
 * @Date 2023/4/7 16:15
 * @desc 日志记录
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( ElementType.METHOD)
@Documented
public @interface Log {
    /**
     * 日志级别
     * @return
     */
    LogLevel value() default LogLevel.INFO;

    /**
     * 功能
     */
    BusinessTypeEnum businessType() default BusinessTypeEnum.OTHER;
}
