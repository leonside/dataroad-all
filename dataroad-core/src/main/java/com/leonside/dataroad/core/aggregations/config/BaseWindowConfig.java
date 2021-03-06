package com.leonside.dataroad.core.aggregations.config;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.core.aggregations.AggerationEnum;
import lombok.Data;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@Data
public abstract class BaseWindowConfig extends BaseConfig  {

    public BaseWindowConfig(Map<String, Object> parameter) {
        super(parameter);
    }

    /**
     *  keyBy，支持多个分组字段，如下的agg和keyby数组对应，配置如：
     *   "keyBy": ["idcard","name],
     */
    public String[] keyBy;

    /**
     * "agg": {
     *     "age": ["stats"],
     *     "sex": ["max"]
     *         }
     */
    public Map<String, List<String>> agg;

    /**
     *转换后的agg
     */
    private Map<String, List<AggerationEnum>> aggerations;

    public void setAggerations(Map<String, List<AggerationEnum>> aggerations) {
        this.aggerations = aggerations;
    }

    public Map<String, List<AggerationEnum>> getAggerations(){

        if(aggerations == null){

            Map<String, List<AggerationEnum>> newAggeration = new HashMap<>();

            agg.entrySet().stream().forEach(itemMap->{
                newAggeration.put(itemMap.getKey(),AggerationEnum.of(itemMap.getValue()));
            });

            this.aggerations = newAggeration;
        }
        return aggerations;
    }

    @Override
    public boolean validate() {
        if(MapUtils.isEmpty(agg)){
            throw new IllegalArgumentException("agg config cannot be empty ");
        }
        return super.validate();
    }

    public abstract String windowComponentName();

    public enum WindowTimeType{
        event, process, ingestion
    }
}
