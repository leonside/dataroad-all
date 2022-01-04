package com.leonside.dataroad.plugin.jdbc.sql;

import com.leonside.dataroad.common.domain.MetaColumn;
import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.common.spi.ItemReader;
import com.leonside.dataroad.common.utils.ParameterUtils;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.utils.RawTypeUtils;
import com.leonside.dataroad.plugin.jdbc.reader.GenericJdbcReader;
import com.leonside.dataroad.plugin.jdbc.reader.JdbcReaderKey;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.operators.StreamFlatMap;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
public class SqlTransformerProcessor extends ComponentNameSupport implements ComponentInitialization<FlinkExecuteContext>, ItemProcessor<FlinkExecuteContext, DataStream<Row>,DataStream<Row>> {

    private String sql;
    private String tableName;
//    private List<MetaColumn> metaColumns;

    @Override
    public DataStream<Row> process(FlinkExecuteContext executeContext, DataStream<Row> dataStream) {

        StreamTableEnvironment streamTableEnvironment = executeContext.getOrCreateStreamTableEnvironment();
//todo
//        TypeInformation rowTypeInfo = createRowTypeInfo(executeContext);
//
//        if(rowTypeInfo != null){
//            SingleOutputStreamOperator transformStream = dataStream.transform("RowType TransformStream", rowTypeInfo, new StreamFlatMap<Row,Row>(new FlatMapFunction<Row,Row>() {
//                @Override
//                public void flatMap(Row row, Collector<Row> collector) throws Exception {
//                    collector.collect(row);
//                }
//            }));
//            streamTableEnvironment.createTemporaryView(tableName, transformStream);
//        }else{
            streamTableEnvironment.createTemporaryView(tableName, dataStream);
//        }

        Table table = streamTableEnvironment.sqlQuery(sql);
        return streamTableEnvironment.toDataStream(table);
    }

//    private TypeInformation createRowTypeInfo(FlinkExecuteContext executeContext) {
//        TypeInformation rowTypeInfo = null;
//        ItemReader  reader = (ItemReader) executeContext.getStartJobFlow().getTask().getComponent();
//        if(reader instanceof GenericJdbcReader){
//            if(CollectionUtils.isNotEmpty(metaColumns) && StringUtils.isNotEmpty(metaColumns.get(0).getType())){
//                rowTypeInfo = RawTypeUtils.createRowTypeInfo(((GenericJdbcReader) reader).getDatabaseDialect().getRawTypeConverter(), metaColumns);
//            }
//        }
//        return rowTypeInfo;
//    }

    @Override
    public void initialize(FlinkExecuteContext executeContext, Map<String, Object> parameter) {
        sql = ParameterUtils.getString(parameter, "sql");
        tableName = ParameterUtils.getString(parameter, "tableName");
//        metaColumns = MetaColumn.getMetaColumns(ParameterUtils.getArrayList(parameter, JdbcReaderKey.KEY_COLUMN));
    }
}
