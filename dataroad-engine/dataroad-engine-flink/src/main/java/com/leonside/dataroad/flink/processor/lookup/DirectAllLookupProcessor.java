package com.leonside.dataroad.flink.processor.lookup;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.common.utils.ConfigBeanUtils;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.core.spi.ItemLookupProcessor;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.processor.lookup.config.BaseLookupConfig;
import com.leonside.dataroad.flink.processor.lookup.config.BaseLookupConfigKey;
import com.leonside.dataroad.flink.processor.lookup.function.DirectAllLookupFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.Map;

/**
 * @author leon
 */
public class DirectAllLookupProcessor extends ComponentNameSupport implements ComponentInitialization<FlinkExecuteContext,BaseLookupConfig>, ItemLookupProcessor<FlinkExecuteContext, DataStream<Row>,DataStream<Row>> {

    private BaseLookupConfig lookupConfig;

    @Override
    public DataStream<Row> process(FlinkExecuteContext executeContext, DataStream<Row> dataStream) {

        DirectAllLookupFunction directAllLookupFunction = new DirectAllLookupFunction.DirectLookupFunctionBuilder()
                .lookupConfig(lookupConfig)
                .build();

        return dataStream.map(directAllLookupFunction);
    }

    @Override
    public void doInitialize(FlinkExecuteContext executeContext, BaseLookupConfig baseConfig) {
        lookupConfig = baseConfig;
    }


//    @Override
//    public Class<? extends BaseConfig> configClass() {
//        return BaseLookupConfig.class;
//    }
}
