package com.zhangyh.management.common.util.sychronize.annotation;

import java.lang.annotation.*;
import java.text.MessageFormat;

/**
 * @author zhangyh
 * @Date 2023/3/24 10:35
 * @desc
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SqlColumn {
    /**
     *  字段
     */
    String field();

    /**
     * 注释
     */
    String comment() default "";

    /**
     * 数据类型长度
     */
    int length() default 255;

    /**
     * 小数点位数 只有double、Float生效
     */
    int decimalPoint() default 3;

    /**
     * 默认值
     */
    String defaultValue() default "";

    /**
     * 允许为空
     */
    boolean allowNull() default true;

    /**
     * 自增
     */
    boolean autoIncrement() default false;

    /**
     * 索引类型
     */
    SqlIndex index() default SqlIndex.NON;

    enum SqlIndex{
        PRI("PRIMARY KEY (`{0}`) USING BTREE", "ALTER TABLE {0} ADD PRIMARY KEY ( `{1}` ) ", "主键索引"),
        UNI("UNIQUE KEY `{0}_uni` (`{0}`) USING BTREE", "ALTER TABLE {0} ADD UNIQUE {1}_uni (`{1}`)", "唯一索引"),
        MUL("KEY `{0}_mul` (`{0}`) USING BTREE", "ALTER TABLE {0} ADD INDEX {1}_mul (`{1}`)", "普通索引"),
        NON("无索引")
        ;

        SqlIndex(String name) {
            this.name = name;
        }

        private static final String DROP_TEMPLATE = "drop index {0} on {1}";
        private String createTemplate;

        private String alterTemplate;

        private String name;

        SqlIndex(String createTemplate, String alterTemplate, String name) {
            this.createTemplate = createTemplate;
            this.alterTemplate = alterTemplate;
            this.name = name;
        }

        public static String createScript(SqlIndex indexType,String field){
            return MessageFormat.format(indexType.createTemplate,field);
        }

        public static String alterScript(SqlIndex indexType,String tableName,String field){
            return MessageFormat.format(indexType.alterTemplate,tableName,field);
        }

        public static String dropScript(SqlIndex indexType, String field, String table) {
            if (PRI.equals(indexType)) {
                return null;
            }
            return MessageFormat.format(DROP_TEMPLATE, field + "_" + indexType.name().toLowerCase(), table);
        }
    }
}
