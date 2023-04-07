package com.zhangyh.management.common.util.sychronize.core;


import com.zhangyh.management.common.util.sychronize.annotation.ColumnType;
import com.zhangyh.management.common.util.sychronize.annotation.SqlColumn;
import com.zhangyh.management.common.util.sychronize.core.scanner.CustomScannerRegistrar;
import com.zhangyh.management.common.util.sychronize.sql.type.TypeCovert;
import com.zhangyh.management.common.util.sychronize.table.TableSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangyh
 * @Date 2023/3/29 8:28
 * @desc
 */
@Slf4j
@RequiredArgsConstructor
public class TableSynchronize implements CommandLineRunner {


    private final JdbcTemplate jdbcTemplate;

    @Resource
    DefaultListableBeanFactory beanFactory;

    @Resource
    TransactionTemplate transactionTemplate;

    /**
     * 构建tableSchema
     *
     * @return
     */
    public List<TableSchema> buildTableSchema() {
        if (CustomScannerRegistrar.getInstantInfo() == null || CustomScannerRegistrar.getInstantInfo().size() == 0) {
            throw new RuntimeException("No Model");
        }
        List<TableSchema> schemaList = new ArrayList<>();
        CustomScannerRegistrar.getInstantInfo().forEach((key, value) -> {
            TableSchema tableSchema = new TableSchema();
            String tableName = (String) value.get("tableName");
            tableSchema.setTableName(tableName.toLowerCase());
            ArrayList<TableSchema.Field> fieldMessage = new ArrayList<>();
            //设置字段信息
            Arrays.stream(key.getDeclaredFields()).forEach(field -> {
                SqlColumn annotation = field.getAnnotation(SqlColumn.class);
                ColumnType columnType = field.getAnnotation(ColumnType.class);
                if (annotation != null) {
                    TableSchema.Field f = new TableSchema.Field();
                    //获取注解信息

                    f.setName(annotation.field());
                    if(columnType==null){
                        String jdbcType = TypeCovert.getJdbcType(field.getType(), annotation.length());
                        f.setType(jdbcType);
                        //不是缺省长度类型才设置类型长度
                        if (!TypeCovert.ignoreLength(jdbcType)) {
                            f.setLength(annotation.length());
                        }
                    }else{
                        f.setType(columnType.jdbcType().name());
                        //不是缺省长度类型才设置类型长度
                        if (!TypeCovert.ignoreLength(columnType.jdbcType().name())) {
                            f.setLength(annotation.length());
                        }
                    }

                    f.setAllowNull(annotation.allowNull());
                    f.setIndexType(annotation.index());
                    f.setAutoIncrement(annotation.autoIncrement());
                    f.setComment(StringUtils.isNotBlank(annotation.comment()) ? annotation.comment() : null);
                    f.setDefaultValue(StringUtils.isNotBlank(annotation.defaultValue()) ? annotation.defaultValue() : null);
                    //如果是Double||Float类型则设置小数点
                    if (field.getType().equals(Double.class) || field.getType().equals(Float.class)) {
                        f.setDecimalPoint(annotation.decimalPoint());
                    }
                    fieldMessage.add(f);
                }
            });
            tableSchema.setFields(fieldMessage);
            schemaList.add(tableSchema);
        });
        return schemaList;
    }

    @Override
    public void run(String... args) {
        SqlBuilder sqlBuilder = new SqlBuilder();
        Map<String, TableSchema> dbTables = getDbTables().stream()
                .map(this::getDbTableSchema)
                .collect(Collectors.toMap(TableSchema::getTableName, e -> e));
        Map<String, TableSchema> modelTable = buildTableSchema().stream().collect(Collectors.toMap(TableSchema::getTableName, e -> e));
        modelTable.forEach((k, v) -> {
            try {
                TableSchema tableSchema = dbTables.get(k);
                //不存在表则创建
                if (tableSchema == null) {
                    String createTableSql = sqlBuilder.buildCreateTableSql(v);
                    String[] sqlArray = createTableSql.split("QAQ");
                    transactionTemplate.execute(status -> {
                        try {
                            jdbcTemplate.batchUpdate(sqlArray);
                        } catch (Exception e) {
                            status.setRollbackOnly();
                        }
                        return null;
                    });
                } else if (!tableSchema.getFields().equals(v.getFields())) {//结构不同则修改表
                    String alterTableSql = sqlBuilder.buildAlterTableSql(v, tableSchema);
                    if (!org.springframework.util.StringUtils.hasText(alterTableSql)) {
                        return;
                    }
                    String[] split = alterTableSql.split("\r\n");
                    transactionTemplate.execute(status -> {
                        try {
                            jdbcTemplate.batchUpdate(split);
                        } catch (Exception e) {
                            status.setRollbackOnly();
                        }
                        return null;
                    });
                }
            } catch (Exception e) {
                log.error("execute mysql synchronizer error : ", e);
            }
        });
//        dbTables.forEach((dbName,db)->{
//            TableSchema tableSchema = modelTable.get(dbName);
//            if(tableSchema==null){
//                String dropTableSql = sqlBuilder.buildDropTableSql(dbName);
//                jdbcTemplate.batchUpdate(dropTableSql);
//            }
//        });
        beanFactory.destroyBean(this);
    }

    /**
     * 获取数据库中的表名
     *
     * @return
     */
    private List<String> getDbTables() {
        return jdbcTemplate.execute((ConnectionCallback<List<String>>) con -> {
            List<String> tables = new ArrayList<>();
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("show tables");
            while (resultSet.next()) {
                String table = resultSet.getString(1);
                tables.add(table);
            }
            return tables;
        });
    }

    /**
     * 获取数据库中的表结构，构造成tableSchema
     *
     * @param tableName
     * @return
     */
    private TableSchema getDbTableSchema(String tableName) {
        return jdbcTemplate.execute((ConnectionCallback<TableSchema>) con -> {
            TableSchema tableSchema = new TableSchema();
            tableSchema.setTableName(tableName);
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("show full fields from " + tableName);
            while (resultSet.next()) {
                String[] typeInfo = resultSet.getString(2).split("\\(");
                String index = resultSet.getString(5);
                String autoIncrement = resultSet.getString(7);
                TableSchema.Field field = TableSchema.Field.builder()
                        .name(resultSet.getString(1))
                        .type(typeInfo[0].toUpperCase())
                        .length(typeInfo.length == 2&&!TypeCovert.ignoreLength(typeInfo[0].toUpperCase()) && !isDecimal(typeInfo[0]) ? Integer.valueOf(typeInfo[1].replace(")", "")) : isDecimal(typeInfo[0]) ? Integer.valueOf(typeInfo[1].substring(0, typeInfo[1].indexOf(","))) : null)
                        .decimalPoint(isDecimal(typeInfo[0]) ? Integer.valueOf(typeInfo[1].replace(")", "").substring(typeInfo[1].indexOf(",") + 1)) : null)
                        .allowNull(resultSet.getBoolean(4))
                        .indexType(org.springframework.util.StringUtils.hasText(index) ? SqlColumn.SqlIndex.valueOf(index) : SqlColumn.SqlIndex.NON)
                        .autoIncrement(StringUtils.isNotBlank(autoIncrement) && autoIncrement.equals("auto_increment"))
                        .defaultValue(resultSet.getString(6))
                        .comment(StringUtils.isNotBlank(resultSet.getString(9))?resultSet.getString(9):null)
                        .build();
                tableSchema.getFields().add(field);
            }
            return tableSchema;
        });
    }

    private boolean isDecimal(String type) {
        return type.equals("decimal") || type.equals("double") || type.equals("float");
    }
}
