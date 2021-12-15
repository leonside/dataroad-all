package com.leonside.dataroad.plugin.mysql.lookup;

import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.common.utils.ConfigBeanUtils;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.plugin.jdbc.lookup.config.JdbcLookupConfig;
import com.leonside.dataroad.plugin.jdbc.lookup.config.JdbcLookupKey;
import com.leonside.dataroad.plugin.jdbc.lookup.function.JdbcAllLookupFunction;
import com.leonside.dataroad.plugin.mysql.MySqlDatabaseDialect;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.Map;

/**
 * @author leon
 */
public class MysqlLookupProcessor extends ComponentNameSupport implements ComponentInitialization<FlinkExecuteContext>, ItemProcessor<FlinkExecuteContext, DataStream<Row>,DataStream<Row>> {

    private JdbcLookupConfig lookupConfig;

    @Override
    public DataStream<Row> process(FlinkExecuteContext executeContext, DataStream<Row> dataStream) {

        JdbcAllLookupFunction jdbcAllLookupFunction = new JdbcAllLookupFunction.JdbcAllLookupFunctionBuilder()
                .jdbcLookupConfig(lookupConfig)
                .databaseDialect(new MySqlDatabaseDialect())
                .build();

        return dataStream.map(jdbcAllLookupFunction);
    }

    @Override
    public void initialize(FlinkExecuteContext executeContext, Map<String, Object> parameter) {
        lookupConfig = new JdbcLookupConfig();
        ConfigBeanUtils.copyConfig(lookupConfig, parameter, JdbcLookupKey.class);
    }

    @Override
    public boolean validate() {
        return lookupConfig.validate();
    }
}
