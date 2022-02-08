package com.leonside.dataroad.flink.config;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.config.ConfigKey;
import com.leonside.dataroad.common.script.ScriptEvaluatorFactory;
import com.leonside.dataroad.common.config.Validation;
import lombok.Data;

import java.util.Map;

/**
 * @author leon
 */
@Data
public class ScriptExpressionConfig extends BaseConfig {

    public ScriptEvaluatorFactory.ScriptEngine language = ScriptEvaluatorFactory.ScriptEngine.fel;

    public String expression;

    public ScriptExpressionConfig(Map<String, Object> parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {

        return super.validate();
    }

    @Override
    public Class<? extends ConfigKey> bindConfigKey() {
        return ScriptExpressionConfigKey.class;
    }
}
