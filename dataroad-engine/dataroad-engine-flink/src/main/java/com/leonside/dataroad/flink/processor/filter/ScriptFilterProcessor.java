package com.leonside.dataroad.flink.processor.filter;

import com.leonside.dataroad.common.exception.ScriptExecuteException;
import com.leonside.dataroad.common.script.ScriptEvaluator;
import com.leonside.dataroad.common.script.ScriptEvaluatorFactory;
import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.flink.config.ScriptExpressionConfig;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.Map;

/**
 * Script Filter Processor
 * @author leon
 */
public class ScriptFilterProcessor extends ComponentNameSupport implements ComponentInitialization<FlinkExecuteContext,ScriptExpressionConfig>, ItemProcessor<FlinkExecuteContext, DataStream<Row>,DataStream<Row>> {

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

                try{
//long l = System.currentTimeMillis();
                    Object evaluate = scriptEvalutor.evaluate(value, parameter);
//System.out.println(Thread.currentThread().getId() + "-" + scriptEvalutor + " >>" +scriptExpressionConfig.getExpression() + ",language["+scriptExpressionConfig.getLanguage()+"] cost:"+( System.currentTimeMillis() - l) + ",row:" + value +  ",result:" + evaluate);

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
