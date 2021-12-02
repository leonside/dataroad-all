package com.leonside.dataroad.core.aggregations.response;

import com.leonside.dataroad.common.utils.Asserts;
import com.leonside.dataroad.core.aggregations.AggerationEnum;

import java.util.List;

/**
 * @author leon
 */
public class Aggerations {

    public static Aggeration getAggeration(Class clazz, List<AggerationEnum> aggerationEnums){
        Asserts.notNull(aggerationEnums, "aggeration enum can not be null");
        if(aggerationEnums.size() == 1){
            return getAggeration(clazz, aggerationEnums.get(0));
        }else{
            return new MultiValue<>(clazz, aggerationEnums);
        }
    }

    public static Aggeration getAggeration(Class clazz,AggerationEnum aggerationEnum ){
        Aggeration aggeration = null;
        switch (aggerationEnum){
            case AVG:
                aggeration = new Avg(clazz);
                break;
            case SUM:
                aggeration = new Sum(clazz);
                break;
            case COUNT:
                aggeration = new Count(clazz);
                break;
            case MAX:
                aggeration = new Max(clazz);
                break;
            case MIN:
                aggeration = new Min(clazz);
                break;
            case STATS:
                aggeration = new Stats(clazz);
                break;
            case TOPHITS:
                //todo
                break;
            case TOTALHITS:
                //todo
                break;
            default:
                throw new UnsupportedOperationException("unsupported aggerationEnum ["+ aggerationEnum +"]");
        }
        return aggeration;
    }

}
