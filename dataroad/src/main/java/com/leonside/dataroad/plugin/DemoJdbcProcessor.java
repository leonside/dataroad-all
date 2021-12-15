//package com.leonside.dataroad.plugin;
//
//import com.leonside.dataroad.common.spi.ItemProcessor;
//import com.leonside.dataroad.flink.context.FlinkExecuteContext;
//import org.apache.flink.api.common.functions.MapFunction;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
//import org.apache.flink.types.Row;
//
///**
// * @author leon
// */
//public class DemoJdbcProcessor implements ItemProcessor<FlinkExecuteContext, DataStream<Row>, DataStream<Row>> {
//    @Override
//    public String getName() {
//        return "processor1";
//    }
//
//    @Override
//    public DataStream<Row> process(FlinkExecuteContext executeContext, DataStream<Row> rowDataStreamSource) {
//
//        SingleOutputStreamOperator<Row> mapSource = rowDataStreamSource.map(new MapFunction<Row, Row>() {
//            @Override
//            public Row map(Row row) throws Exception {
//                int arity = row.getArity();
//                Row of = Row.of(row.getField(0), String.valueOf(row.getField(1)) + "_CODE", row.getField(2));
//                return of;
//            }
//        });
//        return mapSource;
//    }
//}
