package com.leonside.dataroad.flink.processor.sql;

import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.processor.sql.config.SqlTransformerConfig;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

/**
 * @author leon
 */
public class SqlTransformerProcessor extends ComponentNameSupport implements ComponentInitialization<FlinkExecuteContext,SqlTransformerConfig>, ItemProcessor<FlinkExecuteContext, DataStream<Row>,DataStream<Row>> {

    private SqlTransformerConfig sqlTransformerConfig;

    @Override
    public DataStream<Row> process(FlinkExecuteContext executeContext, DataStream<Row> dataStream) {

        StreamTableEnvironment streamTableEnvironment = executeContext.getOrCreateStreamTableEnvironment();

        streamTableEnvironment.createTemporaryView(sqlTransformerConfig.getTableName(), dataStream);

        Table table = streamTableEnvironment.sqlQuery(sqlTransformerConfig.getSql());

        return streamTableEnvironment.toDataStream(table);
    }


    @Override
    public void doInitialize(FlinkExecuteContext executeContext, SqlTransformerConfig config) {
        sqlTransformerConfig = config;
    }

}
