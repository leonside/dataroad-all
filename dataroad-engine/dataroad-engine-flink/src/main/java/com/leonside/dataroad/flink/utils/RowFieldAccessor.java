package com.leonside.dataroad.flink.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.util.typeutils.FieldAccessor;
import org.apache.flink.types.Row;

/**
 * @author leon
 */
public class RowFieldAccessor<R extends org.apache.flink.types.Row,F> extends FieldAccessor<R,F> {

    public String field;

    public int pos;

    private TypeInformation typeInfo;

    public RowFieldAccessor(String field, TypeInformation typeInfo) {
        this.field = field;
        this.fieldType = ((RowTypeInfo) typeInfo).getTypeAt(field);
    }

    public RowFieldAccessor(int pos, TypeInformation typeInfo) {
        this.pos = pos;
        this.fieldType = ((RowTypeInfo) typeInfo).getTypeAt(pos);
    }

    @Override
    public F get(R record) {
        return StringUtils.isNotEmpty(field) ? (F)record.getField(field) :  (F)record.getField(pos);
    }

    @Override
    public R set(R record, F fieldValue) {
        if(StringUtils.isNotEmpty(field)){
            record.setField(field, fieldValue);
        }else{
            record.setField(pos, fieldValue);
        }
        return record;
    }



}
