package com.leonside.dataroad.plugin.jdbc.writer.outputformat;

import com.leonside.dataroad.common.constant.JobCommonConstant;
import com.leonside.dataroad.common.exception.WriteRecordException;
import com.leonside.dataroad.plugin.jdbc.utils.DbUtil;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
/**
 * @author leon
 */
public class GenericStreamJdbcOutputFormat extends GenericJdbcOutputFormat{

    protected PreparedStatement deletePreparedStatement;

    protected PreparedStatement updatePreparedStatement;

    @Override
    protected void doOpen(int taskNumber, int numTasks){
        super.doOpen(taskNumber, numTasks);

        try {
            String deleteStatementByKey = databaseDialect.getDeleteStatementByKey(primaryKey, jdbcWriterConfig.getTable());
            String updateStatementByKey = databaseDialect.getUpdateStatementByKey(jdbcWriterConfig.getColumn(), primaryKey, jdbcWriterConfig.getTable());
            deletePreparedStatement =  dbConn.prepareStatement(deleteStatementByKey);
            updatePreparedStatement =  dbConn.prepareStatement(updateStatementByKey);
        } catch (SQLException sqe) {
            throw new IllegalArgumentException("open() failed.", sqe);
        }finally {
            DbUtil.commit(dbConn);
        }
    }


    @Override
    protected void analyzePrimaryKeys() throws SQLException {
        ResultSet primaryKeys = dbConn.getMetaData().getPrimaryKeys(null, schema, jdbcWriterConfig.getTable());
        if(primaryKeys.next()){
            primaryKey = primaryKeys.getString(JobCommonConstant.PRIMARYKEY_COLUMN_NAME);
        }else{
            throw new IllegalArgumentException("analyzePrimaryKeys exception, not exsit primaryKey.");
        }
    }

    @Override
    protected void doWriteMultipleRecords() throws Exception {
        throw new UnsupportedOperationException("Stream mode does not support batch write mode");
    }

    @Override
    protected void doWriteSingleRecord(Row row) throws WriteRecordException {

        if(row.getKind() == RowKind.INSERT){
            doInsertSingleRecord(row);
        }else if(row.getKind() == RowKind.DELETE){
            doDeleteSingleRecord(row);
        }else if(row.getKind() == RowKind.UPDATE_AFTER){
            doUpdateSingleRecord(row);
        }else{
            LOG.error("unknrow RowKind type [],row []", row.getKind(),row);
        }

    }

    private void doUpdateSingleRecord(Row row) {
        int index = 0;
        try {
            List<String> filterColumn = jdbcWriterConfig.getColumn().stream().filter(col -> !col.equals(primaryKey)).collect(Collectors.toList());
            for (; index < filterColumn.size(); index++) {
                updatePreparedStatement.setObject(index+1, getField(row,filterColumn.get(index)));
            }
            updatePreparedStatement.setObject(index + 1, getField(row, primaryKey));

            updatePreparedStatement.execute();
            DbUtil.commit(dbConn);
        } catch (Exception e) {
            DbUtil.rollBack(dbConn);
            processWriteException(e, index, row);
        }
    }

    private void doDeleteSingleRecord(Row row) {
        int index = 0;
        try {
            deletePreparedStatement.setObject(1, getField(row, primaryKey));
            deletePreparedStatement.execute();
            DbUtil.commit(dbConn);
        } catch (Exception e) {
            DbUtil.rollBack(dbConn);
            processWriteException(e, index, row);
        }
    }

    private void doInsertSingleRecord(Row row) {
        super.doWriteSingleRecord(row);
    }


}
