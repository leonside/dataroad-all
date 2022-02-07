package com.leonside.dataroad.core.aggregations.config;

import com.leonside.dataroad.common.config.ConfigKey;
import com.leonside.dataroad.common.config.Validation;
import lombok.Data;

import java.util.Map;

/**
 * @author leon
 */
@Data
public class CountWindowConfig extends BaseWindowConfig {

    public CountWindowConfig(Map<String, Object> parameter) {
        super(parameter);
    }

    public CountWindowConfig() {
        super(null);
    }

    public int windowSize;

    @Override
    public String windowComponentName() {
        return "countWindowAgg";
    }

    @Override
    public Class<? extends ConfigKey> bindConfigKey() {
        return CountWindowConfigKey.class;
    }
}
