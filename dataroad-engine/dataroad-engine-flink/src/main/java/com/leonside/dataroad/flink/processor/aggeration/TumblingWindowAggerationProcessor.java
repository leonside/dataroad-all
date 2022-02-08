package com.leonside.dataroad.flink.processor.aggeration;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.utils.ConfigBeanUtils;
import com.leonside.dataroad.core.aggregations.config.BaseWindowConfig;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameAutoGenerator;
import com.leonside.dataroad.core.spi.ItemAggregationProcessor;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.core.aggregations.config.TumblingWindowConfig;
import com.leonside.dataroad.core.aggregations.config.TumblingWindowConfigKey;
import com.leonside.dataroad.flink.processor.aggeration.function.TumblingWindowAggerationFunction;
import org.apache.commons.lang.ArrayUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.Map;

/**
 * @author leon
 */
public class TumblingWindowAggerationProcessor extends ComponentNameAutoGenerator implements ItemAggregationProcessor<FlinkExecuteContext, DataStream<Row>, DataStream<Row>>, ComponentInitialization<FlinkExecuteContext,TumblingWindowConfig>  {


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
    public void doInitialize(FlinkExecuteContext executeContext, TumblingWindowConfig config) {
        tumblingWindowConfig = config;

    }

    @Override
    public boolean validate() {
        return tumblingWindowConfig.validate();
    }

    @Override
    public void initialize(BaseWindowConfig baseWindowConfig) {
        this.tumblingWindowConfig = (TumblingWindowConfig) baseWindowConfig;
    }

}
