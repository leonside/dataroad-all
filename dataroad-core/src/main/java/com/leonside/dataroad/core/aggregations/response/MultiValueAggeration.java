package com.leonside.dataroad.core.aggregations.response;

import com.leonside.dataroad.core.aggregations.AggerationEnum;

/**
 * @author leon
 */
public abstract class MultiValueAggeration<IN,OUT,T extends Aggeration> extends Aggeration<IN,OUT,T> {

    public MultiValueAggeration(Class valueClass){
        super(valueClass);
    }

    protected abstract OUT getValue(AggerationEnum aggerationEnum);
}
