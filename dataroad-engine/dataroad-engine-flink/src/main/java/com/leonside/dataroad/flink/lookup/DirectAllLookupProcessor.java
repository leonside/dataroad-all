package com.leonside.dataroad.flink.lookup;

import com.leonside.dataroad.common.constant.ConfigKey;
import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.common.utils.ConfigBeanUtils;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.lookup.config.BaseLookupConfig;
import com.leonside.dataroad.flink.lookup.config.BaseLookupConfigKey;
import com.leonside.dataroad.flink.lookup.function.DirectAllLookupFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.Map;

/**
 * @author leon
 */
public class DirectAllLookupProcessor extends ComponentNameSupport implements ComponentInitialization<FlinkExecuteContext>, ItemProcessor<FlinkExecuteContext, DataStream<Row>,DataStream<Row>> {

    private BaseLookupConfig lookupConfig;

    @Override
    public DataStream<Row> process(FlinkExecuteContext executeContext, DataStream<Row> dataStream) {

        DirectAllLookupFunction directAllLookupFunction = new DirectAllLookupFunction.DirectLookupFunctionBuilder()
                .lookupConfig(lookupConfig)
                .build();

        return dataStream.map(directAllLookupFunction);
    }

    @Override
    public void initialize(FlinkExecuteContext executeContext, Map<String, Object> parameter) {
        lookupConfig = new BaseLookupConfig();
        ConfigBeanUtils.copyConfig(lookupConfig, parameter, BaseLookupConfigKey.class);
    }

    @Override
    public boolean validate() {
        return lookupConfig.validate();
    }
}
