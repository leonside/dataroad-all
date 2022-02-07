package com.leonside.dataroad.flink.predicate;

import com.leonside.dataroad.common.exception.ScriptExecuteException;
import com.leonside.dataroad.common.script.ScriptEvaluator;
import com.leonside.dataroad.common.script.ScriptEvaluatorFactory;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.core.spi.JobPredicate;
import com.leonside.dataroad.flink.config.ScriptExpressionConfig;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import org.apache.flink.types.Row;

import java.util.Map;

/**
 * @author leon
 */
public class ExpressionPredicate extends ComponentNameSupport implements ComponentInitialization<FlinkExecuteContext,ScriptExpressionConfig>, JobPredicate<FlinkExecuteContext, Row> {

    private ScriptExpressionConfig scriptExpressionConfig;

    private ScriptEvaluator scriptEvalutor;

    private Map<String,Object> parameter;

    @Override
    public boolean test(FlinkExecuteContext executeContext, Row row) {

        try{
            Object evaluate = scriptEvalutor.evaluate(row, parameter);

            if(evaluate == null || ! (evaluate instanceof Boolean)){
                throw new ScriptExecuteException("Boolean must be returned， check the expression is valid. [" + scriptExpressionConfig.getExpression() + "]");
            }
            return (Boolean)evaluate;
        }catch (Exception exception){
            throw new ScriptExecuteException("Script execution error [" + scriptExpressionConfig.getExpression() + "]",exception);
        }
    }

    @Override
    public void doInitialize(FlinkExecuteContext executeContext, ScriptExpressionConfig config) {
        this.parameter = config.getParameter();
        this.scriptExpressionConfig =  config ;
        scriptEvalutor = ScriptEvaluatorFactory.createScriptEvalutor(scriptExpressionConfig.getLanguage(), scriptExpressionConfig.getExpression());
    }

}
