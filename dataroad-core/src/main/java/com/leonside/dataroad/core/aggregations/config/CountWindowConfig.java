package com.leonside.dataroad.core.aggregations.config;

import com.leonside.dataroad.core.component.Validation;
import lombok.Data;

/**
 * @author leon
 */
@Data
public class CountWindowConfig extends BaseWindowConfig implements Validation {

    public int windowSize;

    @Override
    public String windowComponentName() {
        return "countWindowAgg";
    }
}
