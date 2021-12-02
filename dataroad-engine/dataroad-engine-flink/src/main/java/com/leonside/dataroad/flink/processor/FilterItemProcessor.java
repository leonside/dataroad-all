package com.leonside.dataroad.flink.processor;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.exception.ScriptExecuteException;
import com.leonside.dataroad.common.script.ScriptEvaluator;
import com.leonside.dataroad.common.script.ScriptEvaluatorFactory;
import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.common.utils.Asserts;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameable;
import com.leonside.dataroad.common.constant.JobConfigConstants;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.Map;

/**
 * @author leon
 */
public class FilterItemProcessor extends ComponentNameable implements ComponentInitialization<FlinkExecuteContext>, ItemProcessor<FlinkExecuteContext, DataStream<Row>,DataStream<Row>> {

    private Map<String,Object> parameter;

    private ScriptEvaluator scriptEvalutor;

    @Override
    public void initialize(ExecuteContext executeContext, Map<String, Object> parameter) {
        Asserts.notEmpty(parameter, "filterItemProcessor parameter can not be null");
        this.parameter = parameter;
        String language = (String)parameter.get(JobConfigConstants.CONFIG_SCRIPT_LANGUAGE);
        ScriptEvaluatorFactory.ScriptEngine scriptEngine = StringUtils.isEmpty(language) ?
                ScriptEvaluatorFactory.ScriptEngine.aviator : ScriptEvaluatorFactory.ScriptEngine.valueOf(language);
        scriptEvalutor = ScriptEvaluatorFactory.createScriptEvalutor(scriptEngine, (String) parameter.get(JobConfigConstants.CONFIG_SCRIPT_EXPRESSION));
    }

    @Override
    public DataStream<Row> process(FlinkExecuteContext executeContext, DataStream<Row> dataStream) {

        return dataStream.filter(new FilterFunction<Row>() {
            @Override
            public boolean filter(Row value) throws Exception {

                Object age = value.getField("age");
                System.out.println(age);
                try{
                    Object evaluate = scriptEvalutor.evaluate(value, parameter);

                    if(evaluate == null || ! (evaluate instanceof Boolean)){
                        throw new ScriptExecuteException("Boolean must be returned， check the expression is valid. [" + parameter.get(JobConfigConstants.CONFIG_SCRIPT_EXPRESSION) + "]");
                    }
                    return (Boolean)evaluate;
                }catch (Exception exception){
                    throw new ScriptExecuteException("Script execution error [" + parameter.get(JobConfigConstants.CONFIG_SCRIPT_EXPRESSION) + "]",exception);
                }
            }
        });
    }


}
