package com.leonside.dataroad.core.aggregations.response;

/**
 * @author leon
 */
public abstract class SingleValueAggeration<IN,OUT,T extends Aggeration> extends Aggeration<IN,OUT,T> {

    public SingleValueAggeration(Class valueClass){
        super(valueClass);
        init();
    }

    protected abstract OUT getValue();
}
