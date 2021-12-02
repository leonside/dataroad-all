package com.leonside.dataroad.core.aggregations.response;

import com.leonside.dataroad.core.aggregations.AggerationEnum;

import java.util.Map;

/**
 * @author leon
 */
public class Sum<IN,OUT> extends SingleValueAggeration<IN,OUT,Sum> {

    private OUT sum;

    public Sum(Class valueClass) {
        super(valueClass);
    }

    @Override
    public AggerationEnum getType() {
        return AggerationEnum.SUM;
    }

    @Override
    public Map<String,Object> asMap() {
        Map<String,Object> row = getBasicRow();
        row.put(AggerationEnum.SUM.name().toLowerCase(), getValue());
        return row;
    }

    @Override
    public void calculate(IN value) {
        sum = (OUT) getNumberFunction().sum(sum, value);
    }

    @Override
    public Sum merge(Sum aggeration) {
        sum = (OUT) getNumberFunction().sum(sum, aggeration.getValue());
        return this;
    }

    @Override
    public OUT getValue() {
        return sum;
    }

    @Override
    public void init() {
        sum = (OUT) getNumberFunction().create();
    }
}
