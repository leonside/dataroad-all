package com.leonside.dataroad.plugin.postgresql;

import com.leonside.dataroad.common.enums.DatabaseType;
import com.leonside.dataroad.plugin.rdb.BaseDatabaseDialect;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
public class PostgresqlDatabaseDialect extends BaseDatabaseDialect {
    @Override
    protected String makeValues(List<String> column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getStartQuote() {
        return "";
    }

    @Override
    public String getEndQuote() {
        return "";
    }

    @Override
    public String quoteValue(String value, String column) {
        return String.format("'%s' as %s",value,column);
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.PostgreSQL;
    }

    @Override
    public String getDriverClass() {
        return "org.postgresql.Driver";
    }

    @Override
    public String getSqlQueryFields(String tableName) {
        return String.format("SELECT * FROM %s LIMIT 0",tableName);
    }

    @Override
    public String getSqlQueryColumnFields(List<String> column, String table) {
        String sql = "select attrelid ::regclass as table_name, attname as col_name, atttypid ::regtype as col_type from pg_attribute \n" +
                "where attrelid = '%s' ::regclass and attnum > 0 and attisdropped = 'f'";
        return String.format(sql,table);
    }
    @Override
    public String getUpsertStatement(List<String> column, String table, Map<String,List<String>> updateKey) {
        return "INSERT INTO " + quoteTable(table)
                + " (" + quoteColumns(column) + ") values "
                + makeValues(column.size())
                + " ON CONFLICT (" + StringUtils.join(updateKey.get("key"), ",") + ") DO UPDATE SET "
                + makeUpdatePart(column);
    }

    private String makeUpdatePart (List<String> column) {
        List<String> updateList = new ArrayList<>();
        for(String col : column) {
            String quotedCol = quoteColumn(col);
            updateList.add(quotedCol + "=excluded." + quotedCol);
        }
        return StringUtils.join(updateList, ",");
    }

    @Override
    public String getSplitFilter(String columnName) {
        return String.format(" mod(%s,${N}) = ${M}", getStartQuote() + columnName + getEndQuote());
    }

    @Override
    public String getSplitFilterWithTmpTable(String tmpTable, String columnName) {
        return String.format(" mod(%s.%s,${N}) = ${M}", tmpTable, getStartQuote() + columnName + getEndQuote());
    }

    @Override
    public int getFetchSize(){
        return 1000;
    }

    @Override
    public int getQueryTimeout(){
        return 1000;
    }

    private String makeValues(int nCols) {
        return "(" + StringUtils.repeat("?", ",", nCols) + ")";
    }
}
