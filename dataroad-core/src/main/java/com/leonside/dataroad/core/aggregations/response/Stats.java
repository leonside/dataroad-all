package com.leonside.dataroad.core.aggregations.response;

import com.leonside.dataroad.core.aggregations.AggerationEnum;

import java.util.Map;

/**
 * @author leon
 */
public class Stats<IN,OUT> extends MultiValueAggeration<IN, OUT,Stats> {

    private Count count;
    private Max max;
    private Min min;
    private Sum sum;
    private Avg avg;

    public Stats(Class valueClass) {
        super(valueClass);
        init();
    }

    @Override
    protected OUT getValue(AggerationEnum aggerationEnum) {
        OUT result = null;
        switch (aggerationEnum){
            case COUNT:
                result = (OUT) count.getValue();
                break;
            case MAX:
                result = (OUT) max.getValue();
                break;
            case MIN:
                result = (OUT) min.getValue();
                break;
            case SUM:
                result = (OUT) sum.getValue();
                break;
            case AVG:
                Double avgValue = getNumberFunction().avgTotal(sum.getValue(), count.getValue());
                result = (OUT) avgValue;
                break;
            default:
                throw new UnsupportedOperationException("unsupported aggerationEnum [" + aggerationEnum +"]");
        }
        return result;
    }

    @Override
    public AggerationEnum getType() {
        return AggerationEnum.STATS;
    }

    @Override
    public Map<String,Object> asMap() {
        Map<String,Object> row = getBasicRow();
        row.put(AggerationEnum.COUNT.name().toLowerCase(), getValue(AggerationEnum.COUNT));
        row.put(AggerationEnum.MAX.name().toLowerCase(), getValue(AggerationEnum.MAX));
        row.put(AggerationEnum.MIN.name().toLowerCase(), getValue(AggerationEnum.MIN));
        row.put(AggerationEnum.SUM.name().toLowerCase(), getValue(AggerationEnum.SUM));
        row.put(AggerationEnum.AVG.name().toLowerCase(), getValue(AggerationEnum.AVG));

        return row;
    }


    @Override
    public void calculate(IN value) {
        count.calculate(value);
        max.calculate(value);
        min.calculate(value);
        sum.calculate(value);
    }

    @Override
    public Stats merge(Stats aggeration) {
        count = count.merge((Count) aggeration.getValue(AggerationEnum.COUNT));
        max = max.merge((Max) aggeration.getValue(AggerationEnum.MAX));
        min = min.merge((Min) aggeration.getValue(AggerationEnum.MIN));
        sum =  sum.merge((Sum)aggeration.getValue(AggerationEnum.SUM));

        return this;
    }

    @Override
    public void init() {
        avg = new Avg(getValueClass());
        count = new Count(getValueClass());
        sum = new Sum<>(getValueClass());
        max = new Max<>(getValueClass());
        min = new Min<>(getValueClass());
    }
}
