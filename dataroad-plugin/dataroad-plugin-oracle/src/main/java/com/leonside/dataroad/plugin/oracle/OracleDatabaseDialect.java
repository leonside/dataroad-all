package com.leonside.dataroad.plugin.oracle;

import com.leonside.dataroad.common.enums.DatabaseType;
import com.leonside.dataroad.plugin.jdbc.BaseDatabaseDialect;
import com.leonside.dataroad.flink.utils.RawTypeConverter;
import com.leonside.dataroad.plugin.oracle.converter.OracleRawTypeConverter;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leon
 */
public class OracleDatabaseDialect extends BaseDatabaseDialect {
    @Override
    public String quoteTable(String table) {
//        table = table.replace("\"","");
//        String[] part = table.split("\\.");
//        if(part.length == DB_TABLE_PART_SIZE) {
//            table = getStartQuote() + part[0] + getEndQuote() + "." + getStartQuote() + part[1] + getEndQuote();
//        } else {
//            table = getStartQuote() + table + getEndQuote();
//        }
        return table;
    }

    @Override
    public String quoteColumn(String column) {
        return column;
    }


    @Override
    public RawTypeConverter getRawTypeConverter() {
        return OracleRawTypeConverter::apply;
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.Oracle;
    }

    @Override
    public String getDriverClass() {
        return "oracle.jdbc.driver.OracleDriver";
    }

    @Override
    public String getSqlQueryFields(String tableName) {
        return "SELECT /*+FIRST_ROWS*/ * FROM " + tableName + " WHERE ROWNUM < 1";
    }

    @Override
    public String getSqlQueryColumnFields(List<String> column, String table) {
        return "SELECT /*+FIRST_ROWS*/ " + quoteColumns(column) + " FROM " + quoteTable(table) + " WHERE ROWNUM < 1";
    }

    @Override
    public String quoteValue(String value, String column) {
        return String.format("'%s' as %s",value,column);
    }

    @Override
    public String getSplitFilter(String columnName) {
        return String.format("mod(%s, ${N}) = ${M}", getStartQuote() + columnName + getEndQuote());
    }

    @Override
    public String getSplitFilterWithTmpTable(String tmpTable, String columnName) {
        return String.format("mod(%s.%s, ${N}) = ${M}", tmpTable, getStartQuote() + columnName + getEndQuote());
    }

    @Override
    protected String makeValues(List<String> column) {
        StringBuilder sb = new StringBuilder("SELECT ");
        for(int i = 0; i < column.size(); ++i) {
            if(i != 0) {
                sb.append(",");
            }
            sb.append("? " + quoteColumn(column.get(i)));
        }
        sb.append(" FROM DUAL");
        return sb.toString();
    }

    @Override
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

        return "SELECT " + StringUtils.join(values,",") + " FROM DUAL";
    }

    @Override
    public String getRowNumColumn(String orderBy) {
        return "rownum as FLINKX_ROWNUM";
    }

    @Override
    public int getFetchSize(){
        return 1000;
    }

    @Override
    public int getQueryTimeout(){
        return 3000;
    }
}
