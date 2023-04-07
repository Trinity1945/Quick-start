package com.zhangyh.management.admin.annotation;

import lombok.Getter;

/**
 * @author zhangyh
 * @Date 2023/4/7 17:14
 * @desc 日志级别
 */
@Getter
public enum LogLevel {
    WARNING(0,"警告"),
    INFO(1,"普通"),
    ERROR(2,"错误");

    LogLevel(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    private Integer code;
    private String name;
}
