package com.zhangyh.management.common.util.sychronize.annotation;

import java.lang.annotation.*;

/**
 * @author zhangyh
 * @Date 2023/3/24 9:58
 * @desc 在需要开启SQL同步的类上使用
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlSync {
    /**
     * 表名
     */
    String tableName();
}
