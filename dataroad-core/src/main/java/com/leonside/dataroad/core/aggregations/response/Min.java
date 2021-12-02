package com.leonside.dataroad.core.aggregations.response;

import com.leonside.dataroad.core.aggregations.AggerationEnum;

import java.util.Map;

/**
 * @author leon
 */
public class Min<IN,OUT> extends SingleValueAggeration<IN,OUT,Min> {

    private OUT min;

    public Min(Class valueClass) {
        super(valueClass);
    }

    @Override
    public AggerationEnum getType() {
        return AggerationEnum.MIN;
    }

    @Override
    public Map<String,Object> asMap() {
        Map<String,Object> row = getBasicRow();
        row.put(AggerationEnum.MIN.name().toLowerCase(), getValue());
        return row;
    }

    @Override
    public void calculate(IN value) {
        if(this.min == null){
            this.min = (OUT)value;
        }else {
            min = (OUT) getNumberFunction().min(this.min, value);
        }

    }

    @Override
    public Min merge(Min aggeration) {
        min = (OUT) getNumberFunction().max(min, aggeration.getValue());
        return this;
    }

    @Override
    public OUT getValue() {
        return min;
    }

    @Override
    public void init() {
//        min = (OUT) getNumberFunction().create();
    }
}
