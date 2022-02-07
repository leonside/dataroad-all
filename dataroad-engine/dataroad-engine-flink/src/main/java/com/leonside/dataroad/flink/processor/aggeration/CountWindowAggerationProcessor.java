package com.leonside.dataroad.flink.processor.aggeration;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.core.aggregations.config.BaseWindowConfig;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameAutoGenerator;
import com.leonside.dataroad.core.spi.ItemAggregationProcessor;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.core.aggregations.config.CountWindowConfig;
import com.leonside.dataroad.flink.processor.aggeration.function.CountWindowAggerationFunction;
import org.apache.commons.lang.ArrayUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

/**
 * @author leon
 */
public class CountWindowAggerationProcessor extends ComponentNameAutoGenerator implements ItemAggregationProcessor<FlinkExecuteContext, DataStream<Row>, DataStream<Row>>, ComponentInitialization<FlinkExecuteContext, CountWindowConfig> {

    private CountWindowConfig countWindowConfig;

    public CountWindowAggerationProcessor() {
        super("CountWindowAggeration-");
    }

    @Override
    public DataStream<Row> process(FlinkExecuteContext executeContext, DataStream<Row> dataStream) {

        CountWindowAggerationFunction countWindowAggerationFunction = CountWindowAggerationFunction.of(countWindowConfig);

        if(ArrayUtils.isNotEmpty(countWindowConfig.getKeyBy())){
            return countWindowAggerationFunction.processKeyByWindow(dataStream);
        }else{
            return countWindowAggerationFunction.processWindow(dataStream);
        }
    }

    @Override
    public void doInitialize(FlinkExecuteContext executeContext, CountWindowConfig baseConfig) {
        countWindowConfig = baseConfig;
    }


    @Override
    public boolean validate() {
        return countWindowConfig.validate();
    }

    @Override
    public void initialize(BaseWindowConfig baseWindowConfig) {
        this.countWindowConfig = (CountWindowConfig) baseWindowConfig;
    }
//    private Map<String,Object> parameter;

//    @Override
//    public void initialize(FlinkExecuteContext executeContext,Map<String, Object> parameter) {
//        this.parameter = parameter;
//
//        Integer windowSize = ParameterUtils.getInteger(parameter, JobConfigKeyConstants.KEY_AGG_WINDOWSIZE);
//        List<?> keyBys = ParameterUtils.getArrayListNullable(parameter, JobConfigKeyConstants.KEY_AGG_KEYBY);
//        List<?> fieldAggs = ParameterUtils.getArrayList(parameter, JobConfigKeyConstants.KEY_AGG_FIELDAGG);
//
//        AggerationBuilder.CountWindow countWindow = CollectionUtils.isNotEmpty(keyBys) ?
//                new AggerationBuilder.CountWindow(windowSize,keyBys.toArray(new String[]{})) : new AggerationBuilder.CountWindow(windowSize);
//
//        Map<String, List<AggerationEnum>> aggerations = new HashMap<>();
//        fieldAggs.stream().forEach(itemMap->{
//            Asserts.notEmpty( ((Map)itemMap), " Aggeration field config can not be null");
//
//            ((Map<String,List>)itemMap).forEach((key,value)->{
//                List<AggerationEnum> aggerationEnums = aggerations.computeIfAbsent(key, value1 -> new ArrayList<>());
//                aggerationEnums.addAll(AggerationEnum.of(value));
//            });
//
//        });
//
//        initialize(countWindow, aggerations);
//
//    }
}
