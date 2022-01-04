package com.leonside.dataroad.flink.config;

import com.leonside.dataroad.common.script.ScriptEvaluatorFactory;
import com.leonside.dataroad.core.component.Validation;
import lombok.Data;

/**
 * @author leon
 */
@Data
public class ScriptExpressionConfig implements Validation {

    public ScriptEvaluatorFactory.ScriptEngine language = ScriptEvaluatorFactory.ScriptEngine.aviator;

    public String expression;

    @Override
    public boolean validate() {

        return Validation.super.validate();
    }
}
