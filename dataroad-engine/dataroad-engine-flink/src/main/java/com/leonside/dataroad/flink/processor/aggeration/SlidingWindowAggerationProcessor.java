package com.leonside.dataroad.flink.processor.aggeration;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.utils.ConfigBeanUtils;
import com.leonside.dataroad.core.aggregations.config.BaseWindowConfig;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameAutoGenerator;
import com.leonside.dataroad.core.spi.ItemAggregationProcessor;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.core.aggregations.config.SlidingWindowConfig;
import com.leonside.dataroad.core.aggregations.config.SlidingWindowConfigKey;
import com.leonside.dataroad.flink.processor.aggeration.function.SlidingWindowAggerationFunction;
import org.apache.commons.lang.ArrayUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.Map;

/**
 * @author leon
 */
public class SlidingWindowAggerationProcessor extends ComponentNameAutoGenerator implements ItemAggregationProcessor<FlinkExecuteContext, DataStream<Row>, DataStream<Row>>, ComponentInitialization<FlinkExecuteContext,SlidingWindowConfig> {

    private SlidingWindowConfig slidingWindowConfig;

    public SlidingWindowAggerationProcessor() {
        super("SlidingWindowAggeration-");
    }

    @Override
    public DataStream<Row> process(FlinkExecuteContext executeContext, DataStream<Row> dataStream) {

        SlidingWindowAggerationFunction slidingWindowAggerationFunction = SlidingWindowAggerationFunction.of(slidingWindowConfig);

        if(ArrayUtils.isNotEmpty(slidingWindowConfig.getKeyBy())){
            return slidingWindowAggerationFunction.processKeyByWindow(dataStream);
        }else{
            return slidingWindowAggerationFunction.processWindow(dataStream);
        }
    }

    @Override
    public void doInitialize(FlinkExecuteContext executeContext, SlidingWindowConfig baseConfig) {
        slidingWindowConfig = baseConfig;
    }

//    @Override
//    public Class<? extends BaseConfig> configClass() {
//        return SlidingWindowConfig.class;
//    }

    @Override
    public boolean validate() {
        return slidingWindowConfig.validate();
    }

    @Override
    public void initialize(BaseWindowConfig baseWindowConfig) {
        this.slidingWindowConfig = (SlidingWindowConfig) baseWindowConfig;
    }
}
