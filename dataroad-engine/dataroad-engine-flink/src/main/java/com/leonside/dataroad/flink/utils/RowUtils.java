package com.leonside.dataroad.flink.utils;

import com.leonside.dataroad.common.domain.MetaColumn;
import com.leonside.dataroad.common.exception.JobConfigException;
import com.leonside.dataroad.common.utils.Asserts;
import com.leonside.dataroad.common.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.types.Row;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
public class RowUtils {



    public static String getStringField(Row row, String key){
        Object field = row.getField(key);
        return field == null ? "" : field.toString();
    }

    public static Row combineRowWithNames(Row row,  Row mergeRow, String... ignoreColumns){
        if(mergeRow == null){
            return row;
        }

        Row newRow = Row.withNames(row.getKind());
        row.getFieldNames(true).stream().forEach(fieldName->{
            newRow.setField(fieldName, row.getField(fieldName));
        });

        mergeRow.getFieldNames(true).stream().forEach(fieldName->{
            if(ignoreColumns == null || !ArrayUtils.contains(ignoreColumns, fieldName)){
                newRow.setField(fieldName, mergeRow.getField(fieldName));
            }
        });
        return newRow;
    }

    public static Row toRowWithNames(Map<String,Object> maps){
        Asserts.notEmpty(maps, "Map can not be null.");
        Row row = Row.withNames();
        maps.forEach((key, value)->{
            row.setField(key, value);
        });
        return row;
    }

    public static String row2string(Row row, List<String> columnTypes, String delimiter) {
        // convert row to string
        int cnt = row.getArity();
        StringBuilder sb = new StringBuilder(128);

        int i = 0;
        try {
            for (; i < cnt; ++i) {
                if (i != 0) {
                    sb.append(delimiter);
                }

                Object column = row.getField(i);

                if(column == null) {
                    continue;
                }

                sb.append(StringUtil.col2string(column, columnTypes.get(i)));
            }
        } catch(Exception ex) {
            String msg = "StringUtil.row2string error: when converting field[" + i + "] in Row(" + row + ")";
            throw new JobConfigException(msg, ex);
        }

        return sb.toString();
    }

}
