package com.leonside.dataroad.core.aggregations.response;

import com.leonside.dataroad.core.aggregations.AggerationEnum;

import java.util.Map;

/**
 * @author leon
 */
public class Avg<IN> extends SingleValueAggeration<IN,Double,Avg> {

    private Double avg;
    private Count count;
    private Sum sum;

    public Avg(Class valueClass) {
        super(valueClass);
    }

    @Override
    public AggerationEnum getType() {
        return AggerationEnum.AVG;
    }

    @Override
    public Map<String,Object> asMap() {
        Map<String,Object> row = getBasicRow();
        row.put(AggerationEnum.AVG.name().toLowerCase(), getValue());
        return row;
    }

    @Override
    public void calculate(IN value) {
        count.calculate(value);
        sum.calculate(value);

    }

    @Override
    public Avg merge(Avg aggeration) {
        avg = getNumberFunction().avg(avg, aggeration.getValue());;
        return this;
    }

    @Override
    public Double getValue() {
        return getNumberFunction().avgTotal(sum.getValue(), count.getValue());
    }

    @Override
    public void init() {
        avg = 0.0;
        count = new Count(getValueClass());
        sum = new Sum<>(getValueClass());
    }
}
