package com.leonside.dataroad.flink.processor.aggeration.function;

import com.leonside.dataroad.core.aggregations.AggerationEnum;
import com.leonside.dataroad.core.aggregations.response.Aggeration;
import com.leonside.dataroad.core.aggregations.config.BaseWindowConfig;
import com.leonside.dataroad.core.aggregations.config.TumblingWindowConfig;
import com.leonside.dataroad.flink.utils.RowUtils;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author leon
 */
public class TumblingWindowAggerationFunction extends CountWindowAggerationFunction implements WindowAggerationFunction<Row> {

    private TumblingWindowConfig tumblingWindowConfig;

    @Override
    public DataStream<Row> processWindow(DataStream<Row> dataStream) {

        AllWindowedStream<Row, TimeWindow> allWindowedStream = dataStream
                .windowAll(createTumblingWindowAssigner());

        List<SingleOutputStreamOperator<Row>> collect = doAggregate(allWindowedStream, dataStream.getType(), dataStream.getExecutionConfig());

        Optional<SingleOutputStreamOperator<Row>> reduceStream = collect
                .stream()
                .reduce((rowSingleOutputStreamOperator, rowSingleOutputStreamOperator2) -> (SingleOutputStreamOperator<Row>) rowSingleOutputStreamOperator.union(rowSingleOutputStreamOperator2));

        return reduceStream.isPresent() ? reduceStream.get() : null;
    }

    private WindowAssigner createTumblingWindowAssigner(){
        switch (tumblingWindowConfig.getTimeType()){
            case event:
                return TumblingEventTimeWindows.of(Time.of(tumblingWindowConfig.getTimeSize(), tumblingWindowConfig.getTimeUnit()));
            default:
                return TumblingProcessingTimeWindows.of(Time.of(tumblingWindowConfig.getTimeSize(), tumblingWindowConfig.getTimeUnit()));
        }
    }

    @Override
    public DataStream<Row> processKeyByWindow(DataStream<Row> dataStream) {

        DataStream<Row> assignTimestampsStream = doAssignTimestampsAndWatermarks(dataStream);

        KeyedStream<Row, Object> rowTupleKeyedStream = doKeyby(tumblingWindowConfig, assignTimestampsStream);

        WindowedStream<Row, Object, TimeWindow> windowedStream = rowTupleKeyedStream.window(createTumblingWindowAssigner());

        List<DataStream<Row>> collect = doKeyByAggregate(windowedStream, dataStream.getType(), dataStream.getExecutionConfig());

        Optional<DataStream<Row>> reduceStream = collect.stream().reduce((rowSingleOutputStreamOperator, rowSingleOutputStreamOperator2) -> rowSingleOutputStreamOperator.union(rowSingleOutputStreamOperator2));

        return reduceStream.isPresent() ? reduceStream.get() : null;
    }

    protected List<SingleOutputStreamOperator<Row>> doAggregate(AllWindowedStream<Row, TimeWindow> allWindowedStream, TypeInformation<Row> type, ExecutionConfig executionConfig) {
        return tumblingWindowConfig.getAggerations().entrySet().stream().map(entry -> {
            String key = entry.getKey();
            List<AggerationEnum> aggerationEnums = entry.getValue();
            SingleOutputStreamOperator<Row> aggregate = allWindowedStream.aggregate(new GenericAggregateFunction<Row>(key, type, executionConfig, aggerationEnums),
                    new AllWindowFunction<Aggeration, Row, TimeWindow>() {
                        @Override
                        public void apply(TimeWindow window, Iterable<Aggeration> values, Collector<Row> out) throws Exception {
                            values.forEach(it -> {
                                it.setAggField(key);
                                Row row = RowUtils.toRowWithNames(it.asMap());
                                addWindowTimeFiled(window, row);
                                out.collect(row);
                            });
                        }
                    });
            return aggregate;
        }).collect(Collectors.toList());
    }

    protected List<DataStream<Row>> doKeyByAggregate(WindowedStream<Row, Object, TimeWindow> windowedStream, TypeInformation<Row> type, ExecutionConfig executionConfig) {
        return tumblingWindowConfig.getAggerations().entrySet().stream().map(entry -> {
            List<AggerationEnum> value = entry.getValue();
            String key = entry.getKey();

            SingleOutputStreamOperator<Row> aggregate = windowedStream.aggregate(new GenericAggregateFunction<Row>(entry.getKey(), type, executionConfig, value),
                    new WindowFunction<Aggeration, Row, Object, TimeWindow>() {
                        @Override
                        public void apply(Object tuple, TimeWindow window, Iterable<Aggeration> input, Collector<Row> out) throws Exception {
                            input.forEach(it -> {
                                it.setAggField(key);
                                Row row = RowUtils.toRowWithNames(it.asMap());
                                if(tumblingWindowConfig.getKeyBy() != null && tumblingWindowConfig.getKeyBy().length > 0){
                                    addKeyByField(tumblingWindowConfig.getKeyBy(), row, tuple);
                                }
                                addWindowTimeFiled(window, row);
                                out.collect(row);
                            });
                        }
                    });

            return aggregate;
        }).collect(Collectors.toList());
    }

    protected KeyedStream<Row, Object> doKeyby(BaseWindowConfig baseWindowConfig, DataStream<Row> assignTimestampsStream) {
        return assignTimestampsStream.keyBy((KeySelector<Row, Object>) value -> {
                    Tuple tuple = Tuple.newInstance(baseWindowConfig.getKeyBy().length);
                    for (int i = 0; i < baseWindowConfig.getKeyBy().length; i++) {
                        tuple.setField(value.getField(baseWindowConfig.getKeyBy()[i]), i);
                    }
                    return tuple;
                });
    }

    protected DataStream<Row> doAssignTimestampsAndWatermarks(DataStream<Row> dataStream) {

        if(tumblingWindowConfig.timeType == BaseWindowConfig.WindowTimeType.event){
            WatermarkStrategy<Row> rowWatermarkStrategy = WatermarkStrategy
                    .<Row>forBoundedOutOfOrderness(Duration.ofSeconds(tumblingWindowConfig.getOutOfOrderness()))
                    .withTimestampAssigner((event, timestamp) -> ((Date) event.getField(tumblingWindowConfig.getEventTimeColumn())).getTime());

            return dataStream.assignTimestampsAndWatermarks(rowWatermarkStrategy );
        }else{
            return dataStream;
        }
    }

    public void setTumblingWindowConfig(TumblingWindowConfig tumblingWindowConfig) {
        this.tumblingWindowConfig = tumblingWindowConfig;
    }

    public static TumblingWindowAggerationFunction of(TumblingWindowConfig config){
        TumblingWindowAggerationFunction tumblingWindowAggerationFunction = new TumblingWindowAggerationFunction();
        tumblingWindowAggerationFunction.setTumblingWindowConfig(config);
        return tumblingWindowAggerationFunction;
    }
}
