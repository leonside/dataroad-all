package com.leonside.dataroad.plugin.postgresql.writer;

import com.leonside.dataroad.common.enums.WriteMode;
import com.leonside.dataroad.common.exception.WriteRecordException;
import com.leonside.dataroad.plugin.jdbc.writer.outputformat.GenericJdbcOutputFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.types.Row;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;

/**
 * @author leon
 */
public class PostgresqlJdbcOutputFormat extends GenericJdbcOutputFormat {

    private static final String COPY_SQL_TEMPL = "copy %s(%s) from stdin DELIMITER '%s' NULL as '%s'";

    private static final String DEFAULT_FIELD_DELIM = "\001";

    private static final String DEFAULT_NULL_DELIM = "\002";

    private static final String LINE_DELIMITER = "\n";

    private boolean isCopyMode = false;

    /**
     * now just add ext insert mode:copy
     */
    private static final String INSERT_SQL_MODE_TYPE = "copy";

    private String copySql = "";

    private CopyManager copyManager;


    @Override
    protected PreparedStatement prepareTemplates() throws SQLException {

        if(jdbcWriterConfig.getFullColumn() == null || jdbcWriterConfig.getFullColumn().size() == 0) {
            jdbcWriterConfig.setFullColumn(jdbcWriterConfig.getColumn());
        }

        //check is use copy mode for insert
        isCopyMode = checkIsCopyMode(insertSqlMode);
        if (WriteMode.INSERT.name().equalsIgnoreCase(mode) && isCopyMode) {
            copyManager = new CopyManager((BaseConnection) dbConn);
            copySql = String.format(COPY_SQL_TEMPL, jdbcWriterConfig.getTable(), String.join(",", jdbcWriterConfig.getColumn()), DEFAULT_FIELD_DELIM, DEFAULT_NULL_DELIM);
            return null;
        }

        return super.prepareTemplates();
    }

    @Override
    protected void doWriteSingleRecord(Row row) throws WriteRecordException {
        if(!isCopyMode){
            super.doWriteSingleRecord(row);
            return;
        }

        //write with copy
        int index = 0;
        try {
            StringBuilder sb = new StringBuilder();
            int lastIndex = row.getArity() - 1;
            for (; index < row.getArity(); index++) {
                Object rowData = getField(row, this.jdbcWriterConfig.getColumn().get(index));
                if(rowData==null){
                    sb.append(DEFAULT_NULL_DELIM);
                }else{
                    sb.append(rowData);
                }
                if(index != lastIndex){
                    sb.append(DEFAULT_FIELD_DELIM);
                }
            }
            String rowVal = sb.toString();
            if(rowVal.contains("\\")){
                rowVal=  rowVal.replaceAll("\\\\","\\\\\\\\");
            }
            if(rowVal.contains("\r")){
                rowVal=  rowVal.replaceAll("\r","\\\\r");
            }

            if(rowVal.contains("\n")){
                rowVal=  rowVal.replaceAll("\n","\\\\n");
            }
            ByteArrayInputStream bi = new ByteArrayInputStream(rowVal.getBytes(StandardCharsets.UTF_8));
            copyManager.copyIn(copySql, bi);
        } catch (Exception e) {
            processWriteException(e, index, row);
        }
    }

    @Override
    protected void doWriteMultipleRecords() throws Exception {
        if(!isCopyMode){
            super.doWriteMultipleRecords();
            return;
        }

        StringBuilder sb = new StringBuilder(128);
        for (Row row : rows) {
            int lastIndex = row.getArity() - 1;
            StringBuilder tempBuilder = new StringBuilder(128);
            for (int index =0; index < row.getArity(); index++) {
                Object rowData = getField(row, this.jdbcWriterConfig.getColumn().get(index) );
                if(rowData==null){
                    tempBuilder.append(DEFAULT_NULL_DELIM);
                }else{
                    tempBuilder.append(rowData);
                }
                if(index != lastIndex){
                    tempBuilder.append(DEFAULT_FIELD_DELIM);
                }
            }
            // \r \n \ 等特殊字符串需要转义
            String tempData = tempBuilder.toString();
            if(tempData.contains("\\")){
                tempData=  tempData.replaceAll("\\\\","\\\\\\\\");
            }
            if(tempData.contains("\r")){
                tempData=  tempData.replaceAll("\r","\\\\r");
            }

            if(tempData.contains("\n")){
                tempData=  tempData.replaceAll("\n","\\\\n");
            }
            sb.append(tempData).append(LINE_DELIMITER);
        }

        String rowVal = sb.toString();
        ByteArrayInputStream bi = new ByteArrayInputStream(rowVal.getBytes(StandardCharsets.UTF_8));
        copyManager.copyIn(copySql, bi);

        if(restoreConfig.isRestore()){
            rowsOfCurrentTransaction += rows.size();
        }
    }

    @Override
    protected Object getField(Row row, String name) {
        Object field = super.getField(row, name);
        String type = fullColumnMapping.get(name);
        field = typeConverter.convert(field,type);

        return field;
    }

    /**
     * 判断是否为copy模式
     * @param insertMode
     * @return
     */
    private boolean checkIsCopyMode(String insertMode){
        if(StringUtils.isBlank(insertMode)){
            return false;
        }

        if(!INSERT_SQL_MODE_TYPE.equalsIgnoreCase(insertMode)){
            throw new RuntimeException("not support insertSqlMode:" + insertMode);
        }

        return true;
    }

}
