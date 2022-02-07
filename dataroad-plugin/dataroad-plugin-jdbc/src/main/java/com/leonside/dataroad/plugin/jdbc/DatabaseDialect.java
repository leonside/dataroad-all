package com.leonside.dataroad.plugin.jdbc;


import com.leonside.dataroad.common.enums.DatabaseType;
import com.leonside.dataroad.flink.utils.RawTypeConverter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Database prototype specification
 *
 */
public interface DatabaseDialect {

    RawTypeConverter getRawTypeConverter();
    /**
     * 返回数据库类型
     *
     * @return 数据库类型对象
     */
    DatabaseType getDatabaseType();

    /**
     * 获取驱动类
     *
     * @return 驱动类名称
     */
    String getDriverClass();

    /**
     * 构造查询表结构的sql语句
     *
     * @param tableName 要查询的表名称
     * @return 查询sql
     */
    String getSqlQueryFields(String tableName);

    /**
     * 根据给定的字段和表构造查询sql
     *
     * @param column 字段名称列表
     * @param table 表名
     * @return 查询sql
     */
    String getSqlQueryColumnFields(List<String> column, String table);

    /**
     * 获取左引号
     *
     * @return 引号
     */
    String getStartQuote();

    /**
     * 获取右引号
     *
     * @return 引号
     */
    String getEndQuote();

    /**
     * 给值两边加引号，并以column起别名
     *
     * @param value 值
     * @param column 别名
     * @return "value" as column
     */
    String quoteValue(String value,String column);

    /**
     * 给字段加引号
     *
     * @param column 字段
     * @return "column"
     */
    String quoteColumn(String column);

    /**
     * 给字段列表加引号
     *
     * @param column 字段列表
     * @param table 表名
     * @return "table"."col1","table"."col2"
     */
    String quoteColumns(List<String> column, String table);

    /**
     * 给字段列表加引号
     *
     * @param column 字段列表
     * @return "col1","col2"
     */
    String quoteColumns(List<String> column);

    /**
     * 给表名加引号
     *
     * @param table 表名
     * @return "table"
     */
    String quoteTable(String table);

    /**
     * 根据字段和表构造insert语句
     *
     * @param column 字段列表
     * @param table 表名
     * @return insert sql
     */
    String getInsertStatement(List<String> column, String table);

    /**
     * 构造replace语句
     *
     * @param column 要upadte的字段列表
     * @param fullColumn 全部的字段列表
     * @param table 表名
     * @param updateKey 主键或唯一索引
     * @return replace sql
     */
    String getReplaceStatement(List<String> column, List<String> fullColumn, String table, Map<String,List<String>> updateKey);

    /**
     * 构造merger语句
     *
     * @param column 字段列表
     * @param table 表名
     * @param updateKey 主键或唯一索引
     * @return merge sql
     */
    String getUpsertStatement(List<String> column, String table, Map<String,List<String>> updateKey);

    /**
     * 构造分片切分语句
     *
     * @param columnName 切分键
     * @return mod(col, n) = m
     */
    String getSplitFilter(String columnName);

    /**
     * 构造分片切分语句，用于自定义sql
     *
     * @param tmpTable 临时表名
     * @param columnName 切分键
     * @return mod(tmpTable.col, n) = m
     */
    String getSplitFilterWithTmpTable(String tmpTable, String columnName);

    /**
     * 构造row number字段
     *
     * @param orderBy 排序字段
     * @return row_number() over(orderBy) as FLINKX_ROWNUM
     */
    String getRowNumColumn(String orderBy);

    /**
     * 获取fetchSize，用以指定一次读取数据条数
     *
     * @return fetchSize
     */
    int getFetchSize();

    /**
     * 获取查询超时时间
     *
     * @return 超时时间
     */
    int getQueryTimeout();

    String getDeleteStatementByKey(String primaryKey, String table);

    String getUpdateStatementByKey(List<String> column, String primaryKey, String table);

    default String getSelectFromStatement(String sql, String[] conditionFields){
        if(ArrayUtils.isEmpty(conditionFields)){
            return sql;
        }
        List<String> fieldExpressionCollector = Arrays.stream(conditionFields)
                .map(f -> format("%s = ?", quoteIdentifier(f)))
                .collect(Collectors.toList());
        String fieldExpressions = StringUtils.join(fieldExpressionCollector, " AND ");

        return "SELECT * FROM ("+ sql +") TMP_ WHERE " + fieldExpressions;
    }

    default String getSelectFromStatement(
            String schema, String tableName, String[] selectFields, String whereClause, String[] conditionFields) {
        String selectExpressions =
                Arrays.stream(selectFields)
                        .map(this::quoteIdentifier)
                        .collect(Collectors.joining(", "));

        List<String> fieldExpressionCollector = Arrays.stream(conditionFields)
                .map(f -> format("%s = ?", quoteIdentifier(f)))
                .collect(Collectors.toList());
        if(StringUtils.isNotEmpty(whereClause)){
            fieldExpressionCollector.add(whereClause);
        }
        String fieldExpressions = StringUtils.join(fieldExpressionCollector, " AND ");
        return "SELECT "
                + selectExpressions
                + " FROM "
                + buildTableInfoWithSchema(schema, tableName)
                + (conditionFields.length > 0 ? " WHERE " + fieldExpressions : "");
    }

    /** Get select fields statement by condition fields. Default use SELECT. */
    default String getSelectFromStatement(
            String schema, String tableName, String[] selectFields, String[] conditionFields) {
       return getSelectFromStatement(schema, tableName, selectFields, null, conditionFields);
    }

    default String quoteIdentifier(String identifier) {
//        return "\"" + identifier + "\"";
        return identifier;
    }
    /** build table-info with schema-info and table-name, like 'schema-info.table-name' */
    default String buildTableInfoWithSchema(String schema, String tableName) {
        if (StringUtils.isNotBlank(schema)) {
            return quoteIdentifier(schema) + "." + quoteIdentifier(tableName);
        } else {
            return quoteIdentifier(tableName);
        }
    }
}

