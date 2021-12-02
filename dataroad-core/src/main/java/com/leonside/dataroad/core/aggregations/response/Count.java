package com.leonside.dataroad.core.aggregations.response;

import com.leonside.dataroad.core.aggregations.AggerationEnum;

import java.util.Map;

/**
 * @author leon
 */
public class Count<IN> extends SingleValueAggeration<IN, Long,Count> {

    private Long count;

    public Count(Class valueClass) {
        super(valueClass);
    }

    @Override
    public AggerationEnum getType() {
        return AggerationEnum.COUNT;
    }

    @Override
    public Map<String,Object> asMap() {
        Map<String,Object> row = getBasicRow();
        row.put(AggerationEnum.COUNT.name().toLowerCase(), getValue());
        return row;
    }

    @Override
    public void calculate(IN value) {
        count = count+1;
    }

    @Override
    public Count merge(Count aggeration) {
        count = count + aggeration.getValue();
        return this;
    }

    @Override
    public Long getValue() {
        return count;
    }

    @Override
    public void init() {
        count = 0L;
    }
}
