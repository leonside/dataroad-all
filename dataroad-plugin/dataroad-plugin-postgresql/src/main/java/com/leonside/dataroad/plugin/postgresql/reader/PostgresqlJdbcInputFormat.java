package com.leonside.dataroad.plugin.postgresql.reader;

import com.leonside.dataroad.plugin.rdb.inputformat.GenericJdbcInputFormat;
import com.leonside.dataroad.plugin.rdb.utils.DbUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.types.Row;

import java.io.IOException;

/**
 * @author leon
 */
public class PostgresqlJdbcInputFormat extends GenericJdbcInputFormat {

    @Override
    public Row doNextRecord(Row row) throws IOException {
        if (!hasNext) {
            return null;
        }
        row = Row.withNames();

        try {
            for (int pos = 0; pos < row.getArity(); pos++) {
                Object obj = resultSet.getObject(pos + 1);
                if(obj != null) {
                    if(CollectionUtils.isNotEmpty(columnTypeList)) {
                        obj = typeConverter.convert(obj, columnTypeList.get(pos));
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
