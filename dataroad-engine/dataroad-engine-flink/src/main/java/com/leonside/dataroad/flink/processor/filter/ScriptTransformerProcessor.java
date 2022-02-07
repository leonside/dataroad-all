package com.leonside.dataroad.flink.processor.filter;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.exception.ScriptExecuteException;
import com.leonside.dataroad.common.script.ScriptEvaluator;
import com.leonside.dataroad.common.script.ScriptEvaluatorFactory;
import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.common.utils.Asserts;
import com.leonside.dataroad.common.utils.ConfigBeanUtils;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.common.constant.JobConfigKeyConstants;
import com.leonside.dataroad.flink.config.ScriptExpressionConfigKey;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.config.ScriptExpressionConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.Map;

/**
 * @author leon
 */
public class ScriptTransformerProcessor extends ComponentNameSupport implements ComponentInitialization<FlinkExecuteContext,ScriptExpressionConfig>, ItemProcessor<FlinkExecuteContext, DataStream<Row>,DataStream<Row>> {

    private ScriptExpressionConfig scriptExpressionConfig;

    private Map<String,Object> parameter;

    private ScriptEvaluator scriptEvalutor;

    @Override
    public void doInitialize(FlinkExecuteContext executeContext, ScriptExpressionConfig config) {
        this.parameter = config.getParameter();
        scriptExpressionConfig = config;
        scriptEvalutor = ScriptEvaluatorFactory.createScriptEvalutor(scriptExpressionConfig.getLanguage(), scriptExpressionConfig.getExpression());

    }

    @Override
    public DataStream<Row> process(FlinkExecuteContext executeContext, DataStream<Row> dataStream) {

        return dataStream.filter(new FilterFunction<Row>() {
            @Override
            public boolean filter(Row value) throws Exception {

//                Object age = value.getField("AGE");
//                System.out.println("age: "+ age);
                try{
                    Object evaluate = scriptEvalutor.evaluate(value, parameter);

                    if(evaluate == null || ! (evaluate instanceof Boolean)){
                        throw new ScriptExecuteException("Boolean must be returned， check the expression is valid. [" + scriptExpressionConfig.getExpression() + "]");
                    }
                    return (Boolean)evaluate;
                }catch (Exception exception){
                    throw new ScriptExecuteException("Script execution error [" + scriptExpressionConfig.getExpression() + "]",exception);
                }
            }
        });
    }


}
