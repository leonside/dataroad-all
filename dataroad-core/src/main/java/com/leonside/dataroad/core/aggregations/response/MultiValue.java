package com.leonside.dataroad.core.aggregations.response;

import com.leonside.dataroad.common.utils.Asserts;
import com.leonside.dataroad.core.aggregations.AggerationEnum;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author leon
 */
public class MultiValue<IN,OUT> extends MultiValueAggeration<IN, OUT,MultiValue>{

    private Map<AggerationEnum, SingleValueAggeration> aggerationMap = new ConcurrentHashMap<>();

    private List<AggerationEnum> aggerationEnums;

    public MultiValue(Class valueClass, List<AggerationEnum> aggerationEnumList) {
        super(valueClass);
        Asserts.notNull(aggerationEnumList, " aggerationEnum list can not be null.");
        this.aggerationEnums = aggerationEnumList;
        init();
    }

    @Override
    public void init() {
        aggerationEnums.forEach(it->{
            aggerationMap.put(it, (SingleValueAggeration) Aggerations.getAggeration(getValueClass(), it));
        });
    }

    @Override
    public AggerationEnum getType() {
        return AggerationEnum.MULTI;
    }

    @Override
    public Map<String,Object> asMap() {
        Map<String,Object> row = getBasicRow();
        aggerationMap.forEach((key, value)->{
            row.put(key.name().toLowerCase(), value.getValue());
        });
        return row;
    }

    @Override
    public void calculate(IN inValue) {
        aggerationMap.forEach((key, value)->{
            value.calculate(inValue);
        });
    }

    @Override
    public MultiValue merge(MultiValue aggeration) {
        aggerationMap.forEach((key,value)->{
            value.merge(aggeration);
        });
        return this;
    }

    @Override
    public OUT getValue(AggerationEnum aggerationEnum) {
        return (OUT) aggerationMap.get(aggerationEnum).getValue();
    }
}
