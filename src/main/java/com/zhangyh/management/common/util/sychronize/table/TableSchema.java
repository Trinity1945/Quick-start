package com.zhangyh.management.common.util.sychronize.table;

import com.zhangyh.management.common.util.sychronize.annotation.SqlColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangyh
 * @Date 2023/3/24 13:42
 * @desc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableSchema {
    /**
     * 表名
     */
    private String tableName;

    /**
     * 表注释
     */
    private String tableComment;

    /**
     * 字段
     */
    private List<Field> fields = new ArrayList<>();


    public void addFiled(Field filed){
        this.fields.add(filed);
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Field {
        /**
         * 字段名
         */
        private String name;

        /**
         * 数据类型
         */
        private String type;

        /**
         * 数据长度
         */
        private Integer length;

        /**
         * 小数点位数
         */
        private Integer decimalPoint;

        /**
         * 默认值
         */
        private String defaultValue;

        /**
         * 是否允许为空
         */
        private Boolean allowNull;

        /**
         * 是否自增
         */
        private boolean autoIncrement;

        private SqlColumn.SqlIndex indexType;

        /**
         * 注释
         */
        private String comment;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Field field = (Field) o;
            return autoIncrement == field.autoIncrement && Objects.equals(name, field.name) && Objects.equals(type, field.type) && Objects.equals(length, field.length) && Objects.equals(decimalPoint, field.decimalPoint) && Objects.equals(defaultValue, field.defaultValue) && Objects.equals(allowNull, field.allowNull) && indexType == field.indexType && Objects.equals(comment, field.comment);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type, length, decimalPoint, defaultValue, allowNull, autoIncrement, indexType, comment);
        }
    }
}
