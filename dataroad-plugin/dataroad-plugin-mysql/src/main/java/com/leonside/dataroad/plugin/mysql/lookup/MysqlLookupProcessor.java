package com.leonside.dataroad.plugin.mysql.lookup;

import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.common.utils.ConfigBeanUtils;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.lookup.config.BaseLookupConfig;
import com.leonside.dataroad.plugin.jdbc.lookup.config.JdbcLookupConfig;
import com.leonside.dataroad.plugin.jdbc.lookup.config.JdbcLookupKey;
import com.leonside.dataroad.plugin.jdbc.lookup.function.JdbcAllLookupFunction;
import com.leonside.dataroad.plugin.jdbc.lookup.function.JdbcLruLookupFunction;
import com.leonside.dataroad.plugin.mysql.MySqlDatabaseDialect;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.types.Row;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author leon
 */
public class MysqlLookupProcessor extends ComponentNameSupport implements ComponentInitialization<FlinkExecuteContext>, ItemProcessor<FlinkExecuteContext, DataStream<Row>,DataStream<Row>> {

    private JdbcLookupConfig lookupConfig;

    @Override
    public DataStream<Row> process(FlinkExecuteContext executeContext, DataStream<Row> dataStream) {

        if(lookupConfig.getCacheType().equals(BaseLookupConfig.CacheType.all)){
            JdbcAllLookupFunction jdbcAllLookupFunction = new JdbcAllLookupFunction.JdbcAllLookupFunctionBuilder()
                    .jdbcLookupConfig(lookupConfig)
                    .databaseDialect(new MySqlDatabaseDialect())
                    .build();
            return dataStream.map(jdbcAllLookupFunction);
        }else{
            JdbcLruLookupFunction jdbcLruLookupFunction = new JdbcLruLookupFunction.JdbcLruLookupFunctionBuilder()
                    .jdbcLookupConfig(lookupConfig)
                    .databaseDialect(new MySqlDatabaseDialect())
                    .build();
            //todo 允许乱序：异步返回的结果允许乱序，超时时间，最大容量，超出容量触发反压
            SingleOutputStreamOperator<Row> singleOutputStreamOperator = AsyncDataStream.unorderedWait(dataStream, jdbcLruLookupFunction, 3000L, TimeUnit.MILLISECONDS)
                    .setParallelism(lookupConfig.getParallelism());

            return singleOutputStreamOperator;
        }
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
