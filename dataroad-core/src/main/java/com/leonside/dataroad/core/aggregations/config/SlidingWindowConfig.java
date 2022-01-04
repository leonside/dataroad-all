package com.leonside.dataroad.core.aggregations.config;

import com.leonside.dataroad.core.component.Validation;
import lombok.Data;

/**
 * @author leon
 */
@Data
public class SlidingWindowConfig extends TumblingWindowConfig implements Validation {

    public long slideSize;

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
}
