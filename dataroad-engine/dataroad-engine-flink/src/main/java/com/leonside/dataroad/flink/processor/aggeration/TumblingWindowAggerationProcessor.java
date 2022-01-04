package com.leonside.dataroad.flink.processor.aggeration;

import com.leonside.dataroad.common.utils.ConfigBeanUtils;
import com.leonside.dataroad.core.aggregations.config.BaseWindowConfig;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameAutoGenerator;
import com.leonside.dataroad.core.spi.ItemAggregationProcessor;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.core.aggregations.config.TumblingWindowConfig;
import com.leonside.dataroad.flink.processor.aggeration.config.TumblingWindowConfigKey;
import com.leonside.dataroad.flink.processor.aggeration.function.TumblingWindowAggerationFunction;
import org.apache.commons.lang.ArrayUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.Map;

/**
 * @author leon
 */
public class TumblingWindowAggerationProcessor extends ComponentNameAutoGenerator implements ItemAggregationProcessor<FlinkExecuteContext, DataStream<Row>, DataStream<Row>>, ComponentInitialization<FlinkExecuteContext>  {


    private TumblingWindowConfig tumblingWindowConfig;

    public TumblingWindowAggerationProcessor() {
        super("TumblingWindowAggeration-");
    }

    @Override
    public DataStream<Row> process(FlinkExecuteContext executeContext, DataStream<Row> dataStream) {

        TumblingWindowAggerationFunction tumblingWindowAggerationFunction = TumblingWindowAggerationFunction.of(tumblingWindowConfig);

        if(ArrayUtils.isNotEmpty(tumblingWindowConfig.getKeyBy())){
            return tumblingWindowAggerationFunction.processKeyByWindow(dataStream);
        }else{
            return tumblingWindowAggerationFunction.processWindow(dataStream);
        }
    }

    @Override
    public void initialize(FlinkExecuteContext executeContext, Map<String, Object> parameter) {
        tumblingWindowConfig = new TumblingWindowConfig();
        ConfigBeanUtils.copyConfig(tumblingWindowConfig, parameter, TumblingWindowConfigKey.class);

    }

    @Override
    public boolean validate() {
        return tumblingWindowConfig.validate();
    }

    @Override
    public void initialize(BaseWindowConfig baseWindowConfig) {
        this.tumblingWindowConfig = (TumblingWindowConfig) baseWindowConfig;
    }

//    private Map<String,Object> parameter;
//
//    @Override
//    public void initialize(FlinkExecuteContext executeContext,Map<String, Object> parameter) {
//        this.parameter = parameter;
//
////        Integer windowSize = ParameterUtils.getInteger(parameter, JobConfigKeyConstants.KEY_AGG_WINDOWSIZE);
//        List<?> keyBys = ParameterUtils.getArrayListNullable(parameter, JobConfigKeyConstants.KEY_AGG_KEYBY);
//        List<?> fieldAggs = ParameterUtils.getArrayList(parameter, JobConfigKeyConstants.KEY_AGG_FIELDAGG);
//
//        AggerationBuilder.TumblingWindow countWindow = CollectionUtils.isNotEmpty(keyBys) ?
//                new AggerationBuilder.TumblingWindow(Time.of(5, TimeUnit.SECONDS),keyBys.toArray(new String[]{}))
//                : new AggerationBuilder.TumblingWindow(Time.of(5, TimeUnit.SECONDS));
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
