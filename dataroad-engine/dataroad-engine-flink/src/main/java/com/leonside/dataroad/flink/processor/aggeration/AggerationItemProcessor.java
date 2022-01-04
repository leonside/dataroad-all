//package com.leonside.dataroad.flink.processor.aggeration;
//
//import com.leonside.dataroad.common.constant.JobConfigKeyConstants;
//import com.leonside.dataroad.common.extension.Activate;
//import com.leonside.dataroad.common.utils.Asserts;
//import com.leonside.dataroad.core.aggregations.AggerationEnum;
//import com.leonside.dataroad.core.aggregations.response.Aggeration;
//import com.leonside.dataroad.core.builder.AggerationBuilder;
//import com.leonside.dataroad.core.builder.Time;
//import com.leonside.dataroad.core.spi.ItemAggregationProcessor;
//import com.leonside.dataroad.core.component.ComponentNameAutoGenerator;
//import com.leonside.dataroad.flink.processor.aggeration.function.GenericAggregateFunction;
//import com.leonside.dataroad.flink.context.FlinkExecuteContext;
//import com.leonside.dataroad.core.aggregations.config.BaseWindowConfig;
//import com.leonside.dataroad.flink.utils.RowUtils;
//import org.apache.commons.lang.ArrayUtils;
//import org.apache.flink.api.common.eventtime.WatermarkGenerator;
//import org.apache.flink.api.common.eventtime.WatermarkGeneratorSupplier;
//import org.apache.flink.api.common.eventtime.WatermarkStrategy;
//import org.apache.flink.api.java.functions.KeySelector;
//import org.apache.flink.api.java.tuple.Tuple;
//import org.apache.flink.api.java.tuple.Tuple2;
//import org.apache.flink.streaming.api.datastream.*;
//import org.apache.flink.streaming.api.functions.AscendingTimestampExtractor;
//import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
//import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
//import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
//import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
//import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
//import org.apache.flink.types.Row;
//import org.apache.flink.util.Collector;
//
//import java.io.Serializable;
//import java.time.Duration;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//
///**
// * @author leon
// */
//@Activate(order = Integer.MIN_VALUE)
//public class AggerationItemProcessor extends ComponentNameAutoGenerator implements ItemAggregationProcessor<FlinkExecuteContext, DataStream<Row>, DataStream<Row>> {
//
//    private AggerationBuilder.Window window;
//
//    public Map<String, List<AggerationEnum>> aggerations;
//
//    public AggerationItemProcessor() {
//        super(JobConfigKeyConstants.COMPONENT_NAME_PREFIX_AGGERATION);
//    }
//
//    @Override
//    public void initialize(AggerationBuilder.Window window, Map<String, List<AggerationEnum>> aggerations) {
//        this.window = window;
//        this.aggerations = aggerations;
//        Asserts.notNull(aggerations, "aggeration type can not be null");
//        Asserts.notNull(window, "aggeration window can not be null");
//    }
//
//    @Override
//    public DataStream<Row> process(FlinkExecuteContext executeContext, DataStream<Row> dataStream) {
//
////        case TOPHITS:
////        case TOTALHITS:
//
//        WindowProcessor windowProcessor = WindowProcessor.createWindowProcessor(window);
//
//        if(ArrayUtils.isEmpty(window.getKeyBy()) ){
//            return windowProcessor.processWindow(dataStream,window,aggerations);
//        }else{
//            return windowProcessor.processKeyByWindow(dataStream, window, aggerations);
//        }
//
//
////        WindowedStream<TrafficDetail, Tuple3, TimeWindow> tumblingWindow = dataStream.window(TumblingEventTimeWindows.of(Time.days(1)));//滚动
////        //2、卡口车流量按天统计（滚动窗口1天）
////        ElasticSearchSink.sink(TopNFunctions.aggCount(tumblingWindow), jobContext.getWriter("flowCountEsWriter"));
////
////        //3、卡口车流量TopN（滑动窗口1天，1小时滚动）
////        WindowedStream<TrafficDetail, Tuple3, TimeWindow> slidingWindow = kkKeyedStream.window(SlidingEventTimeWindows.of(Time.days(1), Time.hours(1)));//滑动
////        SingleOutputStreamOperator<GenericCount> aggCountStream = TopNFunctions.aggCount(slidingWindow);
////        DataStream<GenericTopN> topNStream = TopNFunctions.topN(aggCountStream, 10);
////        ElasticSearchSink.sink(topNStream, jobContext.getWriter("topEsWriter"));
//
//    }
//
//
//    public interface WindowProcessor<T> extends Serializable {
//
//        DataStream<T> processWindow(DataStream<Row> dataStream, AggerationBuilder.Window window, Map<String, List<AggerationEnum>> aggerations);
//
//        DataStream<T> processKeyByWindow(DataStream<Row> dataStream, AggerationBuilder.Window window, Map<String, List<AggerationEnum>> aggerations);
//
//        public static WindowProcessor createWindowProcessor(AggerationBuilder.Window window){
//
//            if(window instanceof AggerationBuilder.CountWindow){
//                return new CountWindowProcessor();
//            }else if(window instanceof AggerationBuilder.SlidingWindow){
//                return new SlidingWindowProcessor();
//            }else if(window instanceof AggerationBuilder.TumblingWindow){
//                return new TumblingWindowProcessor();
//            }else{
//                throw new UnsupportedOperationException("unsupport window["+ window +"]");
//            }
//
//        }
//    }
//
//    public static class TumblingWindowProcessor implements WindowProcessor {
//        @Override
//        public DataStream processWindow(DataStream dataStream, AggerationBuilder.Window window, Map aggerations) {
//            AggerationBuilder.TumblingWindow tumblingWindow = (AggerationBuilder.TumblingWindow) window;
//            Time size = ((AggerationBuilder.TumblingWindow) window).getSize();
//            AllWindowedStream<Row, GlobalWindow> allWindowedStream = dataStream.windowAll(TumblingEventTimeWindows.of(org.apache.flink.streaming.api.windowing.time.Time.of(size.getSize(), size.getUnit())));
//
//            List<SingleOutputStreamOperator<Row>> collect = ((Map<String, List<AggerationEnum>>)aggerations).entrySet().stream().map(entry -> {
//                String key = entry.getKey();
//                List<AggerationEnum> value = entry.getValue();
//                SingleOutputStreamOperator<Row> aggregate = allWindowedStream.aggregate(new GenericAggregateFunction<Row>(key, dataStream.getType(), dataStream.getExecutionConfig(), value),
//                        new AllWindowFunction<Aggeration, Row, GlobalWindow>() {
//                            @Override
//                            public void apply(GlobalWindow window, Iterable<Aggeration> values, Collector<Row> out) throws Exception {
//                                values.forEach(it -> {
//                                    it.setAggField(key);
//                                    out.collect(RowUtils.toRowWithNames(it.asMap()));
//                                });
//                            }
//                        });
//                return aggregate;
//            }).collect(Collectors.toList());
//
//            Optional<SingleOutputStreamOperator<Row>> reduceStream = collect.stream().reduce((rowSingleOutputStreamOperator, rowSingleOutputStreamOperator2) -> (SingleOutputStreamOperator<Row>) rowSingleOutputStreamOperator.union(rowSingleOutputStreamOperator2));
//
//            return reduceStream.isPresent() ? reduceStream.get() : null;
//        }
//
//        @Override
//        public DataStream processKeyByWindow(DataStream dataStream, AggerationBuilder.Window window, Map aggerations) {
//            AggerationBuilder.TumblingWindow tumblingWindow = (AggerationBuilder.TumblingWindow) window;
//
//            KeyedStream<Row, Object> rowTupleKeyedStream = dataStream.assignTimestampsAndWatermarks(
//                    WatermarkStrategy
//                            .<Row>forBoundedOutOfOrderness(Duration.ofSeconds(5))
//                            .withTimestampAssigner((event, timestamp) -> ((Date)event.getField("create_time")).getTime()))
//                    .keyBy((KeySelector<Row, Object>) value -> {
//                Tuple tuple = Tuple.newInstance(tumblingWindow.getKeyBy().length);
//                for (int i = 0; i < tumblingWindow.getKeyBy().length; i++) {
//                    tuple.setField(value.getField(tumblingWindow.getKeyBy()[i]), i);
//                }
//                return tuple;
//            });
//            Time size = ((AggerationBuilder.TumblingWindow) window).getSize();
//            WindowedStream<Row, Object, TimeWindow> windowedStream = rowTupleKeyedStream.window(TumblingEventTimeWindows.of(org.apache.flink.streaming.api.windowing.time.Time.of(size.getSize(), size.getUnit())));
//
//            List<DataStream<Row>> collect = ((Map<String, List<AggerationEnum>>)aggerations).entrySet().stream().map(entry -> {
//                List<AggerationEnum> value = entry.getValue();
//                String key = entry.getKey();
//
//                SingleOutputStreamOperator<Row> aggregate = windowedStream.aggregate(new GenericAggregateFunction<Row>(entry.getKey(), dataStream.getType(), dataStream.getExecutionConfig(), value),
//                        new WindowFunction<Aggeration, Row, Object, TimeWindow>() {
//                    @Override
//                    public void apply(Object tuple, TimeWindow window, Iterable<Aggeration> input, Collector<Row> out) throws Exception {
//                        input.forEach(it -> {
//                            it.setAggField(key);
//                            Row row = RowUtils.toRowWithNames(it.asMap());
//                            for (int i = 0; i < tumblingWindow.getKeyBy().length; i++) {
//                                row.setField(tumblingWindow.getKeyBy()[i], ((Tuple)tuple).getField(i));
//                            }
//                            row.setField(BaseWindowConfig.AGGERATION_KEY_BEGINTIME, new Date(window.getStart()));
//                            row.setField(BaseWindowConfig.AGGERATION_KEY_ENDTIME, new Date(window.getEnd()));
//                            out.collect(row);
//                        });
//                    }
//                });
//
//                return aggregate;
//            }).collect(Collectors.toList());
//
//            Optional<DataStream<Row>> reduceStream = collect.stream().reduce((rowSingleOutputStreamOperator, rowSingleOutputStreamOperator2) -> rowSingleOutputStreamOperator.union(rowSingleOutputStreamOperator2));
//
//            return reduceStream.isPresent() ? reduceStream.get() : null;
//        }
//
//
//    }
//    public static class SlidingWindowProcessor implements WindowProcessor {
//        @Override
//        public DataStream processWindow(DataStream dataStream, AggerationBuilder.Window window, Map aggerations) {
//
//            return null;
//        }
//
//        @Override
//        public DataStream processKeyByWindow(DataStream dataStream, AggerationBuilder.Window window, Map aggerations) {
//            return null;
//        }
//    }
//
//    public static class CountWindowProcessor implements WindowProcessor<Row>{
//
//        @Override
//        public DataStream<Row> processWindow(DataStream<Row> dataStream, AggerationBuilder.Window window, Map<String, List<AggerationEnum>> aggerations) {
//            AggerationBuilder.CountWindow countWindow = (AggerationBuilder.CountWindow) window;
//
//            AllWindowedStream<Row, GlobalWindow> allWindowedStream = dataStream.countWindowAll(countWindow.getSize());
//
//            List<SingleOutputStreamOperator<Row>> collect = aggerations.entrySet().stream().map(entry -> {
//                String key = entry.getKey();
//                List<AggerationEnum> value = entry.getValue();
//                SingleOutputStreamOperator<Row> aggregate = allWindowedStream.aggregate(new GenericAggregateFunction<Row>(key, dataStream.getType(), dataStream.getExecutionConfig(), value),
//                        new AllWindowFunction<Aggeration, Row, GlobalWindow>() {
//                            @Override
//                            public void apply(GlobalWindow window, Iterable<Aggeration> values, Collector<Row> out) throws Exception {
//                                values.forEach(it -> {
//                                    it.setAggField(key);
//                                    out.collect(RowUtils.toRowWithNames(it.asMap()));
//                                });
//                            }
//                        });
//                return aggregate;
//            }).collect(Collectors.toList());
//
//            Optional<SingleOutputStreamOperator<Row>> reduceStream = collect.stream().reduce((rowSingleOutputStreamOperator, rowSingleOutputStreamOperator2) -> (SingleOutputStreamOperator<Row>) rowSingleOutputStreamOperator.union(rowSingleOutputStreamOperator2));
//
//            return reduceStream.isPresent() ? reduceStream.get() : null;
//        }
//
//        @Override
//        public  DataStream<Row> processKeyByWindow(DataStream<Row> dataStream, AggerationBuilder.Window window, Map<String, List<AggerationEnum>> aggerations) {
//
//            AggerationBuilder.CountWindow countWindow = (AggerationBuilder.CountWindow) window;
//
//            KeyedStream<Row, Object> rowTupleKeyedStream = dataStream.keyBy((KeySelector<Row, Object>) value -> {
//                Tuple tuple = Tuple.newInstance(countWindow.getKeyBy().length);
//                for (int i = 0; i < countWindow.getKeyBy().length; i++) {
//                    tuple.setField(value.getField(countWindow.getKeyBy()[i]), i);
//                }
//                return tuple;
//            });
//
//            WindowedStream<Row, Object , GlobalWindow> rowTupleGlobalWindowWindowedStream = rowTupleKeyedStream.countWindow(countWindow.getSize());
//
//            List<DataStream<Row>> collect = aggerations.entrySet().stream().map(entry -> {
//                List<AggerationEnum> value = entry.getValue();
//                String key = entry.getKey();
//
//                SingleOutputStreamOperator<Row> aggregate = rowTupleGlobalWindowWindowedStream.aggregate(new GenericAggregateFunction<Row>(entry.getKey(), dataStream.getType(), dataStream.getExecutionConfig(), value), new WindowFunction<Aggeration, Row, Object, GlobalWindow>() {
//                    @Override
//                    public void apply(Object tuple, GlobalWindow window, Iterable<Aggeration> input, Collector<Row> out) throws Exception {
//                        input.forEach(it -> {
//                            it.setAggField(key);
//                            Row row = RowUtils.toRowWithNames(it.asMap());
//                            for (int i = 0; i < countWindow.getKeyBy().length; i++) {
//                                row.setField(countWindow.getKeyBy()[i], ((Tuple)tuple).getField(i));
//                            }
//                            out.collect(row);
//                        });
//                    }
//                });
//
//                return aggregate;
//            }).collect(Collectors.toList());
//
//            Optional<DataStream<Row>> reduceStream = collect.stream().reduce((rowSingleOutputStreamOperator, rowSingleOutputStreamOperator2) -> rowSingleOutputStreamOperator.union(rowSingleOutputStreamOperator2));
//
//            return reduceStream.isPresent() ? reduceStream.get() : null;
//        }
//
//    }
//}
