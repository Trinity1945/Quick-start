package com.zhangyh.management.common.util.sychronize.core;

import com.zhangyh.management.common.util.sychronize.annotation.SqlColumn;
import com.zhangyh.management.common.util.sychronize.sql.MySQLDialect;
import com.zhangyh.management.common.util.sychronize.sql.SingletonFactory;
import com.zhangyh.management.common.util.sychronize.sql.SqlDialect;
import com.zhangyh.management.common.util.sychronize.table.TableSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author zhangyh
 * @Date 2023/3/24 14:12
 * @desc
 */
@Slf4j
public class SqlBuilder {

    private static final String CREATE_TEMPLATE = "CREATE TABLE IF NOT EXISTS {0} (\n" +
            "{1}\n" +
            ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;";

    private static final String ADD_TEMPLATE = "alter table `{0}` add column {1}";
    private static final String MODIFY_TEMPLATE = "alter table `{0}` modify column {1}";
    private static final String DROP_TEMPLATE = "alter table `{0}` drop column {1}";

    private static final String ALTER_AUTO_INCREMENT = "alter table {0} modify {1} int auto_increment";

    private static final String DROP_TABLE = "drop table `{0}`";

    private static final String CHANG_TABLE = "ALTER TABLE `{0}` change {0}";
    private SqlDialect sqlDialect;

    public SqlBuilder() {
        this.sqlDialect = SingletonFactory.getInstance(MySQLDialect.class);
    }

    public SqlBuilder(SqlDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }

    /**
     * 构建创建表的语句
     *
     * @param schema 表结构
     * @return SQL
     */
    public String buildCreateTableSql(TableSchema schema) {
        if (schema == null) {
            throw new RuntimeException("缺失表信息");
        }
        String autoIncrement = null;
        String tableName = sqlDialect.wrapTableName(schema.getTableName());
        StringBuilder fieldStrBuilder = new StringBuilder();
        List<TableSchema.Field> fields = schema.getFields();
        StringBuilder indexBuider = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            TableSchema.Field filed = fields.get(i);
            SqlColumn.SqlIndex indexType = filed.getIndexType();
            if (indexType.equals(SqlColumn.SqlIndex.PRI)) {
                indexBuider.append(SqlColumn.SqlIndex.createScript(SqlColumn.SqlIndex.PRI, filed.getName())).append(",").append("\r\n");
                if (filed.isAutoIncrement()) {
                    autoIncrement = MessageFormat.format(ALTER_AUTO_INCREMENT, tableName, filed.getName());
                }
            } else if (indexType.equals(SqlColumn.SqlIndex.UNI)) {
                indexBuider.append(SqlColumn.SqlIndex.createScript(SqlColumn.SqlIndex.UNI, filed.getName())).append(",").append("\r\n");
            } else if (indexType.equals(SqlColumn.SqlIndex.MUL)) {
                indexBuider.append(SqlColumn.SqlIndex.createScript(SqlColumn.SqlIndex.UNI, filed.getName())).append(",").append("\r\n");
            }
            fieldStrBuilder.append(buildCreateFieldSql(filed));

            // 最后一个字段后没有逗号和换行
            if (i != fields.size() - 1) {
                fieldStrBuilder.append(",");
                fieldStrBuilder.append("\n");
            }
        }
        if (indexBuider.length() > 0) {
            fieldStrBuilder.append(",").append("\r\n");
            indexBuider.delete(indexBuider.length() - 3, indexBuider.length());
            fieldStrBuilder.append(indexBuider);
        }
        String fieldInfo = fieldStrBuilder.toString();
        //填充模板
        String templateTable = MessageFormat.format(CREATE_TEMPLATE, tableName, fieldInfo);
        String res = templateTable + "QAQ" + autoIncrement;
        log.info("execute script is:\r\n{}", templateTable + "\r\n" + autoIncrement);
        return res;
    }

    /**
     * 构建字段
     *
     * @param filed 字段
     * @return 字段
     */
    public String buildCreateFieldSql(TableSchema.Field filed) {
        if (filed == null) {
            throw new RuntimeException("缺少字段信息");
        }
        String name = sqlDialect.wrapFiledName(filed.getName().toLowerCase());
        Integer length = filed.getLength();
        Integer decimalPoint = filed.getDecimalPoint();
        String type = filed.getType();
        Boolean allowNull = filed.getAllowNull();
        String defaultValue = filed.getDefaultValue();
        String comment = filed.getComment();
        StringBuilder fieldStringBuilder = new StringBuilder();
        //字段模板  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',  `deleted` tinyint DEFAULT '0' COMMENT '是否删除，0否，1是',
        //字段名
        fieldStringBuilder.append(name);
        //字段类型
        fieldStringBuilder.append(" ").append(type);
        //字段长度 有小数点设置小数点
        if (decimalPoint != null) {
            fieldStringBuilder.append(String.format("(%d,%d)", length, decimalPoint));
        }
        if (decimalPoint == null && length != null) {
            fieldStringBuilder.append(String.format("(%d)", length));
        }
        //默认值
        if (StringUtils.isNotBlank(defaultValue)) {
            fieldStringBuilder.append(" ").append("default ").append("'").append(defaultValue).append("'");
        }
        //是否为空
        fieldStringBuilder.append(" ").append(allowNull ? "null" : "not null");
        //注释
        if (StringUtils.isNotBlank(comment)) {
            fieldStringBuilder.append(" ").append(String.format("comment '%s'", comment));
        }
        return fieldStringBuilder.toString();
    }

    /**
     * 构建Model与数据库对应的修改sql语句
     *
     * @param modelTable
     * @param dbTable
     * @return
     */
    public String buildAlterTableSql(TableSchema modelTable, TableSchema dbTable) {
        String tableName = modelTable.getTableName();
        StringBuilder scriptBuilder = new StringBuilder();
        StringBuilder lastDrop = new StringBuilder();
        StringBuilder primaryKey=new StringBuilder();
        Map<String, TableSchema.Field> modelField = modelTable.getFields().stream().collect(Collectors.toMap(TableSchema.Field::getName, e -> e));
        Map<String, TableSchema.Field> dbField = dbTable.getFields().stream().collect(Collectors.toMap(TableSchema.Field::getName, e -> e));
        //删除多余的字段
        AtomicInteger atomicInteger = new AtomicInteger();
        dbField.forEach((fk, fv) -> {
            TableSchema.Field field = modelField.get(fk);
            if (field == null) {
                atomicInteger.getAndIncrement();
                String dropColum = MessageFormat.format(DROP_TEMPLATE, tableName, fk);
                scriptBuilder.append(dropColum).append(";").append("\r\n");
            }
        });
        if (atomicInteger.get() == dbField.size()) {
            lastDrop.append(scriptBuilder.substring(scriptBuilder.lastIndexOf("alter table")));
            scriptBuilder.delete(scriptBuilder.lastIndexOf("alter table"), scriptBuilder.length());
        }
        //字段新增或修改
        modelField.forEach((k, v) -> {
            TableSchema.Field field = dbField.get(k);
            if (field == null) {
                //不存在字段则添加字段
                String fieldSql = buildCreateFieldSql(v);
                String addColumSql = MessageFormat.format(ADD_TEMPLATE, tableName, fieldSql);
                scriptBuilder.append(addColumSql).append(";").append("\r\n");
                if (!v.getIndexType().equals(SqlColumn.SqlIndex.NON)) {
                    //主键则添加主键索引
                    String index = SqlColumn.SqlIndex.alterScript(v.getIndexType(), tableName, v.getName());
                    scriptBuilder.append(index).append(";").append("\r\n");
                    if (v.isAutoIncrement() && v.getType().equals("INT") && v.getIndexType().equals(SqlColumn.SqlIndex.PRI)) {
                        //自增且为主键 则修改字段为自增
                        String auto_increment = MessageFormat.format(ALTER_AUTO_INCREMENT, tableName, v.getName());
                        primaryKey.append(index).append(";").append("\r\n").append(auto_increment).append(";").append("\r\n");
                        scriptBuilder.delete(scriptBuilder.lastIndexOf("ALTER TABLE"),scriptBuilder.length());
                    }
                }
            } else {
                if (v.equals(field)) {
                    return;
                }
//                try {
//                    HashMap<String, Object> compareFields = compareFields(v, field);
//                    compareFields.forEach((s, o) -> System.out.println("k:"+s+"="+o));
//                } catch (IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                }

                //直接修改字段
                String fieldSql = buildCreateFieldSql(v);
                String modifyColumSql = MessageFormat.format(MODIFY_TEMPLATE, tableName, fieldSql);
                scriptBuilder.append(modifyColumSql).append(";").append("\r\n");
                if (v.getIndexType().equals(SqlColumn.SqlIndex.PRI) && v.isAutoIncrement()) {
                    scriptBuilder.delete(scriptBuilder.lastIndexOf(";"), scriptBuilder.length());
                    scriptBuilder.append(" auto_increment ;\r\n");
                }
                //model索引与数据库索引不同
                if (!v.getIndexType().equals(field.getIndexType()) && !v.getIndexType().equals(SqlColumn.SqlIndex.NON)) {
                    String alterIndexScript = SqlColumn.SqlIndex.alterScript(v.getIndexType(), tableName, v.getName());
                    scriptBuilder.append(alterIndexScript).append(";").append("\r\n");
                    if (!field.getIndexType().equals(SqlColumn.SqlIndex.NON)) {
                        String dropScript = SqlColumn.SqlIndex.dropScript(field.getIndexType(), field.getName(), tableName);
                        scriptBuilder.append(dropScript).append(";").append("\r\n");
                    }
                }
            }
        });
        if (lastDrop.length() > 0) {
            scriptBuilder.append(lastDrop);
        }
        if(primaryKey.length()>0){
            scriptBuilder.append(primaryKey);
        }
        int index = scriptBuilder.lastIndexOf("\r\n");
        if(index!=-1){
            log.info("execute script is :\r\n{}",  scriptBuilder.delete(index, scriptBuilder.length()));
        }
        return scriptBuilder.toString();
    }

    public String buildDropTableSql(String tableName){
      return  MessageFormat.format(DROP_TABLE,tableName);
    }

    /**
     * 比较两个对象中值不同的字段
     * @param object1 /
     * @param object2 /
     * @return
     * @param <T> /
     * @throws IllegalAccessException /
     */
    public static <T> HashMap<String,Object> compareFields(T object1, T object2) throws IllegalAccessException {
        HashMap<String,Object> differentFields = new HashMap<>();
        Class<?> clazz = object1.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value1 = field.get(object1);
            Object value2 = field.get(object2);
            if (!Objects.equals(value1, value2)) {
                differentFields.put(field.getName(), value1 + "|" + value2);
            }
        }
        return differentFields;
    }

}
