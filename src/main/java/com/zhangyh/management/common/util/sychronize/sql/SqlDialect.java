package com.zhangyh.management.common.util.sychronize.sql;

/**
 * @author zhangyh
 * @Date 2023/3/24 14:04
 * @desc
 */
public interface SqlDialect {
    //封装表名
    String wrapTableName(String tableName);
    //解析表名
    String parseTableName(String tableName);
    //封装字段名
    String wrapFiledName(String tableName);
    //解析字段名
    String parseFiledName(String tableName);
}
