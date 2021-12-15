package com.leonside.dataroad.plugin.mysql.reader;

import com.leonside.dataroad.common.utils.DateUtil;
import com.leonside.dataroad.plugin.jdbc.reader.inputformat.GenericJdbcInputFormat;
import com.leonside.dataroad.plugin.jdbc.utils.DbUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.core.io.InputSplit;
import org.apache.flink.types.Row;

import java.io.IOException;

/**
 * @author leon
 */
public class MysqlJdbcInputFormat extends GenericJdbcInputFormat {

    @Override
    public void doOpen(InputSplit inputSplit) throws IOException {
        // 避免result.next阻塞
        if(incrementConfig.isPolling()
                && StringUtils.isEmpty(incrementConfig.getStartLocation())
                && fetchSize == databaseDialect.getFetchSize()){
            fetchSize = 1000;
        }
        super.doOpen(inputSplit);
    }

    @Override
    public Row doNextRecord(Row row) throws IOException {
        if (!hasNext) {
            return null;
        }
        row = Row.withNames();//new Row(columnCount);

        try {
            for (int pos = 0; pos < columnCount; pos++) {
                Object obj = resultSet.getObject(pos + 1);
                if(obj != null) {
                    if(CollectionUtils.isNotEmpty(columnTypeList)) {
                        String columnType = columnTypeList.get(pos);
                        if("year".equalsIgnoreCase(columnType)) {
                            java.util.Date date = (java.util.Date) obj;
                            obj = DateUtil.dateToYearString(date);
                        } else if("tinyint".equalsIgnoreCase(columnType)
                                || "bit".equalsIgnoreCase(columnType)) {
                            if(obj instanceof Boolean) {
                                obj = ((Boolean) obj ? 1 : 0);
                            }
                        }
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
}
