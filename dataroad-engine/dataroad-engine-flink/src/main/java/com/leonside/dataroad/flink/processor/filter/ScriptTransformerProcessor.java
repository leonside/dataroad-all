package com.leonside.dataroad.flink.processor.filter;

import com.leonside.dataroad.common.exception.ScriptExecuteException;
import com.leonside.dataroad.common.script.ScriptEvaluator;
import com.leonside.dataroad.common.script.ScriptEvaluatorFactory;
import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.flink.config.ScriptExpressionConfig;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.Map;

/**
 * Script Transformer Processor
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

        return dataStream.map(new MapFunction<Row,Row>() {

            @Override
            public Row map(Row row) throws Exception {
                try{
                    scriptEvalutor.evaluate(row, parameter);

                    return row;
                }catch (Exception exception){
                    throw new ScriptExecuteException("Script execution error [" + scriptExpressionConfig.getExpression() + "],language ["+scriptExpressionConfig.getLanguage()+"]",exception);
                }
            }
        });
    }


}
