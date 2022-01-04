package com.leonside.dataroad.core.aggregations.response;

import com.leonside.dataroad.common.constant.JobConfigKeyConstants;
import com.leonside.dataroad.core.aggregations.AggerationEnum;
import com.leonside.dataroad.core.aggregations.NumberFunction;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author leon
 */
@Data
public abstract class Aggeration<IN,OUT,T extends Aggeration> implements Serializable {

    public static final String AGGERATION_KEY_AGGFIELD = "aggBy";
    public static final String AGGERATION_KEY_BEGINTIME = "beginTime";
    public static final String AGGERATION_KEY_ENDTIME = "endTime";
    public static final String AGGERATION_KEY_DUMPTIME = "dumpTime";

    private String aggField;

//    private Date beginTime;
//    private Date endTime;

    private long dumpTime = System.currentTimeMillis();

    private Class valueClass;
    private NumberFunction numberFunction;

    public Aggeration(Class valueClass){
        this.valueClass = valueClass;
        numberFunction = NumberFunction.getForClass(valueClass);
    }

    public abstract void init();

    public abstract AggerationEnum getType();

    public String getAggField(){
        return this.aggField;
    }

    public abstract Map<String,Object> asMap();

    public abstract void calculate(IN value);

    public abstract T merge(T aggeration);

    protected Map<String,Object> getBasicRow() {
        Map<String,Object> basicMap = new LinkedHashMap<>();
        basicMap.put(AGGERATION_KEY_AGGFIELD, getAggField());
        basicMap.put(AGGERATION_KEY_DUMPTIME, getDumpTime());
        return basicMap;
    }


}
