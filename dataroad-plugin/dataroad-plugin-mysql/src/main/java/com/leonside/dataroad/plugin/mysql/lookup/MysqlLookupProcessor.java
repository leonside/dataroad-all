package com.leonside.dataroad.plugin.mysql.lookup;

import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.common.utils.ConfigBeanUtils;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.processor.lookup.config.BaseLookupConfig;
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

        /*TypeInformation<Row> type = dataStream.getType();

        RowTypeInfo rowTypeInfo = null;
        if(type instanceof ExternalTypeInfo){
            TypeInformation<?> typeInformation = TypeConversions.fromDataTypeToLegacyInfo(((ExternalTypeInfo) type).getDataType());
            TypeInformation[] typeInformations = (TypeInformation[]) ArrayUtils.add(((RowTypeInfo) typeInformation).getFieldTypes(), Types.STRING);
            String[] names = (String[]) ArrayUtils.add(((RowTypeInfo)typeInformation).getFieldNames(), "value");
            rowTypeInfo = new RowTypeInfo(typeInformations, names);
        }else if(type instanceof RowTypeInfo){
            TypeInformation[] typeInformations = (TypeInformation[]) ArrayUtils.add(((RowTypeInfo) type).getFieldTypes(), Types.STRING);
            String[] names = (String[]) ArrayUtils.add(((RowTypeInfo)type).getFieldNames(), "value");
            rowTypeInfo = new RowTypeInfo(typeInformations, names);
        }else{

        }*/

//        SingleOutputStreamOperator<Row> typeInfoTranslateStream = dataStream.transform("typeInfoTranslate", rowTypeInfo, new StreamFlatMap<Row, Row>(new FlatMapFunction<Row, Row>() {
//            @Override
//            public void flatMap(Row row, Collector<Row> collector) throws Exception {
//                collector.collect(row);
//            }
//        }));
        if(lookupConfig.getCacheType().equalsIgnoreCase(BaseLookupConfig.CacheType.all.name())){
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
