package com.leonside.dataroad.flink.processor.aggeration.function;

import com.leonside.dataroad.core.aggregations.AggerationEnum;
import com.leonside.dataroad.core.aggregations.response.Aggeration;
import com.leonside.dataroad.flink.aggregation.GenericAggregateFunction;
import com.leonside.dataroad.core.aggregations.config.BaseWindowConfig;
import com.leonside.dataroad.core.aggregations.config.CountWindowConfig;
import com.leonside.dataroad.flink.utils.RowUtils;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author leon
 */
public class CountWindowAggerationFunction implements WindowAggerationFunction<Row>{

    private CountWindowConfig countWindowConfig;

    @Override
    public DataStream<Row> processWindow(DataStream<Row> dataStream) {

        AllWindowedStream<Row, GlobalWindow> allWindowedStream = dataStream.countWindowAll(countWindowConfig.getWindowSize());

        List<SingleOutputStreamOperator<Row>> collect = doAggregate(countWindowConfig, allWindowedStream, dataStream.getType(), dataStream.getExecutionConfig());

        Optional<SingleOutputStreamOperator<Row>> reduceStream = collect.stream().reduce((rowSingleOutputStreamOperator, rowSingleOutputStreamOperator2) -> (SingleOutputStreamOperator<Row>) rowSingleOutputStreamOperator.union(rowSingleOutputStreamOperator2));

        return reduceStream.isPresent() ? reduceStream.get() : null;
    }

    @Override
    public DataStream<Row> processKeyByWindow(DataStream<Row> dataStream) {

        KeyedStream<Row, Object> rowTupleKeyedStream = doKeyby(countWindowConfig, dataStream);

        WindowedStream<Row, Object , GlobalWindow> rowTupleGlobalWindowWindowedStream = rowTupleKeyedStream.countWindow(countWindowConfig.getWindowSize());

        List<DataStream<Row>> collect = doKeyByAggregate(countWindowConfig, rowTupleGlobalWindowWindowedStream, dataStream.getType(), dataStream.getExecutionConfig());

        Optional<DataStream<Row>> reduceStream = collect.stream().reduce((rowSingleOutputStreamOperator, rowSingleOutputStreamOperator2) -> rowSingleOutputStreamOperator.union(rowSingleOutputStreamOperator2));

        return reduceStream.isPresent() ? reduceStream.get() : null;
    }

    private List<SingleOutputStreamOperator<Row>> doAggregate(BaseWindowConfig baseWindowConfig, AllWindowedStream<Row, GlobalWindow> allWindowedStream, TypeInformation<Row> type, ExecutionConfig executionConfig) {
        return baseWindowConfig.getAggerations().entrySet().stream().map(entry -> {
            String key = entry.getKey();
            List<AggerationEnum> aggerationEnums = entry.getValue();
            SingleOutputStreamOperator<Row> aggregate = allWindowedStream.aggregate(new GenericAggregateFunction<Row>(key, type, executionConfig, aggerationEnums),
                    new AllWindowFunction<Aggeration, Row, GlobalWindow>() {
                        @Override
                        public void apply(GlobalWindow window, Iterable<Aggeration> values, Collector<Row> out) throws Exception {
                            values.forEach(it -> {
                                it.setAggField(key);
                                Row row = RowUtils.toRowWithNames(it.asMap());
                                out.collect(row);
                            });
                        }
                    });
            return aggregate;
        }).collect(Collectors.toList());
    }

    private List<DataStream<Row>> doKeyByAggregate(BaseWindowConfig baseWindowConfig, WindowedStream<Row, Object, GlobalWindow> windowedStream, TypeInformation<Row> type, ExecutionConfig executionConfig) {
        return baseWindowConfig.getAggerations().entrySet().stream().map(entry -> {
            List<AggerationEnum> value = entry.getValue();
            String key = entry.getKey();

            SingleOutputStreamOperator<Row> aggregate = windowedStream.aggregate(new GenericAggregateFunction<Row>(entry.getKey(), type, executionConfig, value),
                    new WindowFunction<Aggeration, Row, Object, GlobalWindow>() {
                @Override
                public void apply(Object tuple, GlobalWindow window, Iterable<Aggeration> input, Collector<Row> out) throws Exception {
                    input.forEach(it -> {
                        it.setAggField(key);
                        Row row = RowUtils.toRowWithNames(it.asMap());
                        addKeyByField(baseWindowConfig.getKeyBy(), row, tuple);
                        out.collect(row);
                    });
                }
            });

            return aggregate;
        }).collect(Collectors.toList());
    }

    private KeyedStream<Row, Object> doKeyby(BaseWindowConfig baseWindowConfig, DataStream<Row> assignTimestampsStream) {
        return assignTimestampsStream.keyBy((KeySelector<Row, Object>) value -> {
            Tuple tuple = Tuple.newInstance(baseWindowConfig.getKeyBy().length);
            for (int i = 0; i < baseWindowConfig.getKeyBy().length; i++) {
                tuple.setField(value.getField(baseWindowConfig.getKeyBy()[i]), i);
            }
            return tuple;
        });
    }

    protected void addKeyByField(String[] keyBy, Row row, Object tuple) {
        for (int i = 0; i < keyBy.length; i++) {
            row.setField(keyBy[i], ((Tuple)tuple).getField(i));
        }
    }

    protected void addWindowTimeFiled(TimeWindow window, Row row) {
        row.setField(Aggeration.AGGERATION_KEY_BEGINTIME, new Date(window.getStart()));
        row.setField(Aggeration.AGGERATION_KEY_ENDTIME, new Date(window.getEnd()));
    }

    public void setCountWindowConfig(CountWindowConfig countWindowConfig) {
        this.countWindowConfig = countWindowConfig;
    }

    public static CountWindowAggerationFunction of(CountWindowConfig config){
        CountWindowAggerationFunction countWindowAggerationFunction = new CountWindowAggerationFunction();
        countWindowAggerationFunction.setCountWindowConfig(config);
        return countWindowAggerationFunction;
    }
}
