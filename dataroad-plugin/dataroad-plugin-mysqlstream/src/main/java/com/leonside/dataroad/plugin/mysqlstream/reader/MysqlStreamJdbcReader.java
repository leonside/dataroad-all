package com.leonside.dataroad.plugin.mysqlstream.reader;

import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.debezium.DebeziumDeserializationSchema;
import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.context.JobSetting;
import com.leonside.dataroad.common.spi.ItemReader;
import com.leonside.dataroad.common.utils.ParameterUtils;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.plugin.mysqlstream.config.MysqlStreamReaderConfig;
import com.leonside.dataroad.plugin.mysqlstream.config.MysqlStreamReaderConfigKey;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.Map;

/**
 * @author leon
 */
public class MysqlStreamJdbcReader extends ComponentNameSupport implements ComponentInitialization<FlinkExecuteContext,MysqlStreamReaderConfig>, ItemReader<FlinkExecuteContext, DataStream<Row>> {

    private JobSetting jobSetting;

    private MysqlStreamReaderConfig mysqlStreamReaderConfig;


    @Override
    public DataStream<Row> read(FlinkExecuteContext executeContext) throws Exception {

        DebeziumSourceFunction<Row> build = MySQLSource.<Row>builder()
                .hostname(mysqlStreamReaderConfig.getHostname())
                .port(mysqlStreamReaderConfig.getPort())
                .databaseList(mysqlStreamReaderConfig.getSchema())
                .tableList(mysqlStreamReaderConfig.getSchema() +"." + mysqlStreamReaderConfig.getTable())
                .username(mysqlStreamReaderConfig.getUsername())
                .password(mysqlStreamReaderConfig.getPassword())
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();

        SingleOutputStreamOperator<Row> singleOutputStreamOperator = executeContext.getEnvironment().addSource(build, TypeInformation.of(Row.class));
        return singleOutputStreamOperator;
    }

    @Override
    public void doInitialize(FlinkExecuteContext executeContext, MysqlStreamReaderConfig config) {
        this.jobSetting = executeContext.getJobSetting();
        this.mysqlStreamReaderConfig = config;
    }

    public static class JsonDebeziumDeserializationSchema implements DebeziumDeserializationSchema<Row> {

        @Override
        public void deserialize(SourceRecord sourceRecord, Collector collector) throws Exception {
            Envelope.Operation op = Envelope.operationFor(sourceRecord);
//            String source = sourceRecord.topic();
//            if( !source.endsWith(".student")){
//                return;
//            }

            Schema schema = sourceRecord.valueSchema();
            Struct value = (Struct) sourceRecord.value();

            if (op != Envelope.Operation.CREATE && op != Envelope.Operation.READ) {
                if (op == Envelope.Operation.DELETE) {
                    Row row = extractBeforeRow(RowKind.DELETE, value, schema);
                    collector.collect(row);
                } else {
                    Row updateAfterRow = extractAfterRow(RowKind.UPDATE_AFTER, value, schema);
                    collector.collect(updateAfterRow);
                }
            } else {
                Row insertRow = extractAfterRow(RowKind.INSERT, value, schema);
                collector.collect(insertRow);
            }
        }



        @Override
        public TypeInformation getProducedType() {
            return null;
        }


        private Row extractAfterRow(RowKind rowKind, Struct value, Schema valueSchema) throws Exception {
            Row row = Row.withNames(rowKind);
            Schema afterSchema = valueSchema.field("after").schema();
            Struct after = value.getStruct("after");
            afterSchema.fields().stream().forEach(it ->{
                row.setField(it.name(), after.get(it));
            });
            return row;
        }

        private Row extractBeforeRow(RowKind rowKind, Struct value, Schema valueSchema) throws Exception {
            Row row = Row.withNames(rowKind);
            Schema afterSchema = valueSchema.field("before").schema();
            Struct after = value.getStruct("before");
            afterSchema.fields().stream().forEach(it ->{
                row.setField(it.name(), after.get(it));
            });
            return row;
        }

    }
}
