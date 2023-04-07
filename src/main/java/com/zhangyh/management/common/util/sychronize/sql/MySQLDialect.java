package com.zhangyh.management.common.util.sychronize.sql;

/**
 * @author zhangyh
 * @Date 2023/3/24 14:07
 * @desc
 */
public class MySQLDialect implements SqlDialect{
    @Override
    public String wrapTableName(String tableName) {
       return String.format("`%s`",tableName);
    }

    @Override
    public String parseTableName(String tableName) {
        if(tableName.startsWith("`")&&tableName.endsWith("`")){
            return tableName.substring(1,tableName.length()-1);
        }
        return tableName;
    }

    @Override
    public String wrapFiledName(String filedName) {
        return String.format("`%s`",filedName);
    }

    @Override
    public String parseFiledName(String filedName) {
        if(filedName.startsWith("`")&&filedName.endsWith("`")){
            return filedName.substring(1,filedName.length()-1);
        }
        return filedName;
    }
}
