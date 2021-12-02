package com.leonside.dataroad.flink.predicate;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.exception.ScriptExecuteException;
import com.leonside.dataroad.common.script.ScriptEvaluator;
import com.leonside.dataroad.common.script.ScriptEvaluatorFactory;
import com.leonside.dataroad.core.spi.JobPredicate;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameable;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.types.Row;

import java.util.Map;

/**
 * @author leon
 */
public class ExpressionPredicate extends ComponentNameable implements ComponentInitialization<FlinkExecuteContext>, JobPredicate<FlinkExecuteContext, Row> {

    public static final String PARAM_EXPRESSION = "expression";

    public static final String PARAM_LANGUAGE = "language";

    private ScriptEvaluator scriptEvalutor;

    private Map<String,Object> parameter;

    @Override
    public boolean test(FlinkExecuteContext executeContext, Row row) {

        try{
            Object evaluate = scriptEvalutor.evaluate(row, parameter);

            if(evaluate == null || ! (evaluate instanceof Boolean)){
                throw new ScriptExecuteException("Boolean must be returned， check the expression is valid. [" + parameter.get(PARAM_EXPRESSION) + "]");
            }
            return (Boolean)evaluate;
        }catch (Exception exception){
            throw new ScriptExecuteException("Script execution error [" + parameter.get(PARAM_EXPRESSION) + "]",exception);
        }
    }

    @Override
    public void initialize(ExecuteContext executeContext, Map<String, Object> parameter) {
        this.parameter = parameter;
        String language = (String)parameter.get(PARAM_LANGUAGE);
        ScriptEvaluatorFactory.ScriptEngine scriptEngine = StringUtils.isEmpty(language) ?
                ScriptEvaluatorFactory.ScriptEngine.aviator : ScriptEvaluatorFactory.ScriptEngine.valueOf(language);
        scriptEvalutor = ScriptEvaluatorFactory.createScriptEvalutor(scriptEngine, (String) parameter.get(PARAM_EXPRESSION));
    }
}
