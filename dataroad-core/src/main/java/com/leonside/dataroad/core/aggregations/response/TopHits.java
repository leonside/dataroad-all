package com.leonside.dataroad.core.aggregations.response;


import com.leonside.dataroad.core.aggregations.AggerationEnum;

import java.util.Map;

/**
 * @author leon
 */
public class TopHits extends SingleValueAggeration {

    private double count;

    private int topNum;

    public TopHits(Class valueClass) {
        super(valueClass);
    }

    @Override
    public void init() {

    }

    @Override
    public AggerationEnum getType() {
        return AggerationEnum.TOPHITS;
    }

    @Override
    public Map<String,Object> asMap() {
        return null;
    }

    @Override
    public void calculate(Object value) {

    }

    @Override
    public Aggeration merge(Aggeration aggeration) {
        return null;
    }

    @Override
    public Object getValue() {
        return null;
    }
}
