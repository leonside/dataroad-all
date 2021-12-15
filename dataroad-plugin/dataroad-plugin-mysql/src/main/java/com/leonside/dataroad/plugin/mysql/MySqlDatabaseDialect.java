
package com.leonside.dataroad.plugin.mysql;

import com.leonside.dataroad.common.enums.DatabaseType;
import com.leonside.dataroad.plugin.jdbc.BaseDatabaseDialect;
import com.leonside.dataroad.flink.utils.RawTypeConverter;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The class of MySQL database prototype
 *

 */
public class MySqlDatabaseDialect extends BaseDatabaseDialect {

    @Override
    public RawTypeConverter getRawTypeConverter() {
        return MysqlRawTypeConverter::apply;
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.MySQL;
    }

    @Override
    public String getDriverClass() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public String getSqlQueryFields(String tableName) {
        return "SELECT * FROM " + tableName + " LIMIT 0";
    }

    @Override
    public String getSqlQueryColumnFields(List<String> column, String table) {
        return "SELECT " + quoteColumns(column) + " FROM " + quoteTable(table) + " LIMIT 0";
    }

    @Override
    public String getStartQuote() {
        return "`";
    }

    @Override
    public String getEndQuote() {
        return "`";
    }

    @Override
    public String quoteValue(String value, String column) {
        return String.format("\"%s\" as %s",value,column);
    }

    @Override
    public String getReplaceStatement(List<String> column, List<String> fullColumn, String table, Map<String,List<String>> updateKey) {
        return "REPLACE INTO " + quoteTable(table)
                + " (" + quoteColumns(column) + ") values "
                + makeValues(column.size());
    }

    @Override
    public String getUpsertStatement(List<String> column, String table, Map<String,List<String>> updateKey) {
        return "INSERT INTO " + quoteTable(table)
                + " (" + quoteColumns(column) + ") values "
                + makeValues(column.size())
                + " ON DUPLICATE KEY UPDATE "
                + makeUpdatePart(column);
    }

    private String makeUpdatePart (List<String> column) {
        List<String> updateList = new ArrayList<>();
        for(String col : column) {
            String quotedCol = quoteColumn(col);
            updateList.add(quotedCol + "=values(" + quotedCol + ")");
        }
        return StringUtils.join(updateList, ",");
    }

    @Override
    public String getSplitFilter(String columnName) {
        return String.format("%s mod ${N} = ${M}", getStartQuote() + columnName + getEndQuote());
    }

    @Override
    public String getSplitFilterWithTmpTable(String tmpTable, String columnName){
        return String.format("%s.%s mod ${N} = ${M}", tmpTable, getStartQuote() + columnName + getEndQuote());
    }

    @Override
    public String getRowNumColumn(String orderBy) {
        throw new RuntimeException("Not support row_number function");
    }

    private String makeValues(int nCols) {
        return "(" + StringUtils.repeat("?", ",", nCols) + ")";
    }

    @Override
    protected String makeValues(List<String> column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFetchSize(){
        return Integer.MIN_VALUE;
    }

    @Override
    public int getQueryTimeout(){
        return 1000;
    }
}
