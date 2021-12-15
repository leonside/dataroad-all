package com.leonside.dataroad.plugin.jdbc;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract base parent class of other database prototype implementations
 *
 */
public abstract class BaseDatabaseDialect implements DatabaseDialect, Serializable {

    public static final int DB_TABLE_PART_SIZE = 2;

    @Override
    public String getStartQuote() {
        return "\"";
    }

    @Override
    public String getEndQuote() {
        return "\"";
    }

    @Override
    public String quoteColumn(String column) {
        return getStartQuote() + column + getEndQuote();
    }

    @Override
    public String quoteColumns(List<String> column) {
        return quoteColumns(column, null);
    }

    @Override
    public String quoteColumns(List<String> column, String table) {
        String prefix = StringUtils.isBlank(table) ? "" : quoteTable(table) + ".";
        List<String> list = new ArrayList<>();
        for(String col : column) {
            list.add(prefix + quoteColumn(col));
        }
        return StringUtils.join(list, ",");
    }

    @Override
    public String quoteTable(String table) {
        String[] parts = table.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; ++i) {
            if(i != 0) {
                sb.append(".");
            }
            sb.append(getStartQuote() + parts[i] + getEndQuote());
        }
        return sb.toString();
    }

    @Override
    public String getReplaceStatement(List<String> column, List<String> fullColumn, String table, Map<String,List<String>> updateKey) {
        throw new UnsupportedOperationException("replace mode is not supported");
    }

    protected String makeReplaceValues(List<String> column, List<String> fullColumn){
        List<String> values = new ArrayList<>();
        boolean contains = false;

        for (String col : column) {
            values.add("? " + quoteColumn(col));
        }

        for (String col : fullColumn) {
            for (String c : column) {
                if (c.equalsIgnoreCase(col)){
                    contains = true;
                    break;
                }
            }

            if (contains){
                contains = false;
                continue;
            } else {
                values.add("null "  + quoteColumn(col));
            }

            contains = false;
        }

        return StringUtils.join(values,",");
    }

    @Override
    public String getUpsertStatement(List<String> column, String table, Map<String,List<String>> updateKey) {
        if(updateKey == null || updateKey.isEmpty()) {
            return getInsertStatement(column, table);
        }

        List<String> updateColumns = getUpdateColumns(column, updateKey);
        if(CollectionUtils.isEmpty(updateColumns)){
            return "MERGE INTO " + quoteTable(table) + " T1 USING "
                    + "(" + makeValues(column) + ") T2 ON ("
                    + updateKeySql(updateKey) + ") WHEN NOT MATCHED THEN "
                    + "INSERT (" + quoteColumns(column) + ") VALUES ("
                    + quoteColumns(column, "T2") + ")";
        } else {
            return "MERGE INTO " + quoteTable(table) + " T1 USING "
                    + "(" + makeValues(column) + ") T2 ON ("
                    + updateKeySql(updateKey) + ") WHEN MATCHED THEN UPDATE SET "
                    + getUpdateSql(updateColumns, "T1", "T2") + " WHEN NOT MATCHED THEN "
                    + "INSERT (" + quoteColumns(column) + ") VALUES ("
                    + quoteColumns(column, "T2") + ")";
        }
    }

    /**
     * 获取都需要更新数据的字段
     */
    protected List<String> getUpdateColumns(List<String> column, Map<String,List<String>> updateKey){
        Set<String> indexColumns = new HashSet<>();
        for (List<String> value : updateKey.values()) {
            indexColumns.addAll(value);
        }

        List<String> updateColumns = new ArrayList<>();
        for (String col : column) {
            if(!indexColumns.contains(col)){
                updateColumns.add(col);
            }
        }

        return updateColumns;
    }

    /**
     * 构造查询sql
     *
     * @param column 字段列表
     * @return 查询sql
     */
    abstract protected String makeValues(List<String> column);

    protected String getUpdateSql(List<String> column, String leftTable, String rightTable) {
        String prefixLeft = StringUtils.isBlank(leftTable) ? "" : quoteTable(leftTable) + ".";
        String prefixRight = StringUtils.isBlank(rightTable) ? "" : quoteTable(rightTable) + ".";
        List<String> list = new ArrayList<>();
        for(String col : column) {
            list.add(prefixLeft + quoteColumn(col) + "=" + prefixRight + quoteColumn(col));
        }
        return StringUtils.join(list, ",");
    }

    protected String updateKeySql(Map<String,List<String>> updateKey) {
        List<String> exprList = new ArrayList<>();
        for(Map.Entry<String,List<String>> entry : updateKey.entrySet()) {
            List<String> colList = new ArrayList<>();
            for(String col : entry.getValue()) {
                colList.add("T1." + quoteColumn(col) + "=T2." + quoteColumn(col));
            }
            exprList.add(StringUtils.join(colList, " AND "));
        }
        return StringUtils.join(exprList, " OR ");
    }

    @Override
    public String getInsertStatement(List<String> column, String table) {
        return "INSERT INTO " + quoteTable(table)
                + " (" + quoteColumns(column) + ") values ("
                + StringUtils.repeat("?", ",", column.size()) + ")";
    }

    @Override
    public String getRowNumColumn(String orderBy) {
        return String.format("row_number() over(%s) as FLINKX_ROWNUM", orderBy);
    }

    @Override
    public int getFetchSize(){
        return 1000;
    }

    @Override
    public int getQueryTimeout(){
        return 1000;
    }

    @Override
    public String getDeleteStatementByKey(String primaryKey, String table){
        return "DELETE from " + quoteTable(table) + " WHERE " + quoteColumn(primaryKey) + "=?";
    }

    @Override
    public String getUpdateStatementByKey(List<String> column, String primaryKey, String table){
        List<String> filterColumn = column.stream().filter(col -> !col.equals(primaryKey)).collect(Collectors.toList());
        return "UPDATE " + quoteTable(table) + "SET " +
                filterColumn.stream().map(col -> quoteColumn(col) + "=? ").collect(Collectors.joining(",")) +
                " WHERE " + quoteColumn(primaryKey) + "=?";
    }
}
