package com.zhangyh.management.common.util.sychronize.annotation;

import java.lang.annotation.*;
import java.sql.JDBCType;

/**
 * @author zhangyh
 * @Date 2023/4/6 8:27
 * @desc 字段类型
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ColumnType {
    JDBCType jdbcType() default JDBCType.VARCHAR;
}
