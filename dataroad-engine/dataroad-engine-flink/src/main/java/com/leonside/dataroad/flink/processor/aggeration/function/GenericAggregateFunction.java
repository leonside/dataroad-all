package com.leonside.dataroad.flink.processor.aggeration.function;


import com.leonside.dataroad.core.aggregations.AggerationEnum;
import com.leonside.dataroad.core.aggregations.response.Aggeration;
import com.leonside.dataroad.core.aggregations.response.Aggerations;
import com.leonside.dataroad.flink.utils.FieldAccessorExtendFactory;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.streaming.util.typeutils.FieldAccessor;

import java.util.List;


/**
 * @author leon
 */
public class GenericAggregateFunction<T> implements AggregateFunction<T, Aggeration, Aggeration> {


    private final FieldAccessor<T, Object> fieldAccessor;
    private final TypeSerializer<T> serializer;
    private final boolean isTuple;
    private List<AggerationEnum> aggerationEnums;

    public GenericAggregateFunction(int pos, TypeInformation<T> typeInfo, ExecutionConfig config, List<AggerationEnum> aggerationEnumList) {
        fieldAccessor = FieldAccessorExtendFactory.getAccessor(typeInfo, pos, config);
        this.aggerationEnums = aggerationEnumList;
        if (typeInfo instanceof TupleTypeInfo) {
            isTuple = true;
            serializer = null;
        } else {
            isTuple = false;
            this.serializer = typeInfo.createSerializer(config);
        }
    }

    public GenericAggregateFunction(String field, TypeInformation<T> typeInfo, ExecutionConfig config, List<AggerationEnum> aggerationEnumList) {
        fieldAccessor = FieldAccessorExtendFactory.getAccessor(typeInfo, field, config);
        this.aggerationEnums = aggerationEnumList;
        if (typeInfo instanceof TupleTypeInfo) {
            isTuple = true;
            serializer = null;
        } else {
            isTuple = false;
            this.serializer = typeInfo.createSerializer(config);
        }
    }

    @Override
    public Aggeration createAccumulator() {
        return Aggerations.getAggeration(fieldAccessor.getFieldType().getTypeClass(), aggerationEnums);
    }

    @Override
    public Aggeration add(T value, Aggeration accumulator) {
        accumulator.calculate(fieldAccessor.get(value));
        return accumulator;
    }

    @Override
    public Aggeration getResult(Aggeration accumulator) {
        return accumulator;
    }

    @Override
    public Aggeration merge(Aggeration a, Aggeration b) {
        return a.merge(b);
    }
}
