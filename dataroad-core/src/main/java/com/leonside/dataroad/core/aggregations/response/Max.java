package com.leonside.dataroad.core.aggregations.response;

import com.leonside.dataroad.core.aggregations.AggerationEnum;

import java.util.Map;

/**
 * @author leon
 */
public class Max<IN,OUT> extends SingleValueAggeration<IN,OUT,Max> {

    private OUT max;

    public Max(Class valueClass) {
        super(valueClass);
    }

    @Override
    public AggerationEnum getType() {
        return AggerationEnum.MAX;
    }

    @Override
    public Map<String,Object> asMap() {
        Map<String,Object> row = getBasicRow();
        row.put(AggerationEnum.MAX.name().toLowerCase(), getValue());
        return row;
    }

    @Override
    public void calculate(IN value) {
        max = (OUT) getNumberFunction().max(max, value);
    }

    @Override
    public Max merge(Max aggeration) {
        max = (OUT) getNumberFunction().max(max, aggeration.getValue());
        return this;
    }

    @Override
    public OUT getValue() {
        return max;
    }

    @Override
    public void init() {
        max = (OUT) getNumberFunction().create();
    }
}
