package com.leonside.dataroad.plugin.oracle.reader;

import com.leonside.dataroad.common.domain.ColumnType;
import com.leonside.dataroad.plugin.jdbc.reader.inputformat.GenericJdbcInputFormat;
import com.leonside.dataroad.plugin.jdbc.utils.DbUtil;
import org.apache.flink.types.Row;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * @author leon
 */
public class OracleJdbcInputFormat extends GenericJdbcInputFormat {

    @Override
    public Row doNextRecord(Row row) throws IOException {
        if (!hasNext) {
            return null;
        }
        row = Row.withNames();

        try {
            for (int pos = 0; pos < columnCount; pos++) {
                Object obj = resultSet.getObject(pos + 1);
                if(obj != null) {
                    if((obj instanceof java.util.Date
                            || obj.getClass().getSimpleName().toUpperCase().contains("TIMESTAMP")) ) {
                        obj = resultSet.getTimestamp(pos + 1);
                    }
                    obj = DbUtil.clobToString(obj);
                }

                row.setField(metaData.getColumnName(pos + 1), obj);
            }
            return super.doNextRecord(row);
        }catch (Exception e) {
            throw new IOException("Couldn't read data - " + e.getMessage(), e);
        }
    }

    /**
     * 构建时间边界字符串
     * @param location          边界位置(起始/结束)
     * @param incrementColType  增量字段类型
     * @return
     */
    @Override
    protected String getTimeStr(Long location, String incrementColType){
        String timeStr;
        Timestamp ts = new Timestamp(DbUtil.getMillis(location));
        ts.setNanos(DbUtil.getNanos(location));
        timeStr = DbUtil.getNanosTimeStr(ts.toString());

        if(ColumnType.TIMESTAMP.name().equals(incrementColType)){
            //纳秒精度为9位
            timeStr = String.format("TO_TIMESTAMP('%s','YYYY-MM-DD HH24:MI:SS:FF9')", timeStr);
        } else {
            timeStr = timeStr.substring(0, 19);
            timeStr = String.format("TO_DATE('%s','YYYY-MM-DD HH24:MI:SS')", timeStr);
        }

        return timeStr;
    }

}
