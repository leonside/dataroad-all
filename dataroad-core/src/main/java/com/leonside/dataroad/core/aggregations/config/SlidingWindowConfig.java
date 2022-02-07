package com.leonside.dataroad.core.aggregations.config;

import com.leonside.dataroad.common.config.ConfigKey;
import lombok.Data;

import java.util.Map;

/**
 * @author leon
 */
@Data
public class SlidingWindowConfig extends TumblingWindowConfig  {

    public long slideSize;

    public SlidingWindowConfig(Map<String, Object> parameter) {
        super(parameter);
    }

    public SlidingWindowConfig(){
        super(null);
    }


    @Override
    public String windowComponentName() {
        return "slidingWindowAgg";
    }

    @Override
    public boolean validate() {

        if(slideSize <= 0){
            throw new IllegalArgumentException("slideSize must be configured");
        }

        return super.validate();
    }

    @Override
    public Class<? extends ConfigKey> bindConfigKey() {
        return SlidingWindowConfigKey.class;
    }
}
