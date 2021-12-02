package com.leonside.dataroad.flink.utils;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.util.typeutils.FieldAccessor;
import org.apache.flink.streaming.util.typeutils.FieldAccessorFactory;

/**
 * @author leon
 */
public class FieldAccessorExtendFactory {

    public static <T, F> FieldAccessor<T, F> getAccessor(
            TypeInformation<T> typeInfo, String field, ExecutionConfig config) {

        if(typeInfo instanceof RowTypeInfo){
            return (FieldAccessor<T, F>) new RowFieldAccessor(field, typeInfo);
        }else{
            return org.apache.flink.streaming.util.typeutils.FieldAccessorFactory.getAccessor(typeInfo, field,config);
        }
    }

    public static <T, F> FieldAccessor<T, F> getAccessor(
            TypeInformation<T> typeInfo, int pos, ExecutionConfig config) {

        if(typeInfo instanceof RowTypeInfo){
            return (FieldAccessor<T, F>) new RowFieldAccessor(pos, typeInfo);
        }else{
            return FieldAccessorFactory.getAccessor(typeInfo, pos,config);
        }
    }
}
