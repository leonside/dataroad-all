package com.leonside.dataroad.flink.processor.aggeration.function;

import com.leonside.dataroad.core.aggregations.config.SlidingWindowConfig;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.windowing.assigners.*;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.Optional;

/**
 * @author leon
 */
public class SlidingWindowAggerationFunction extends TumblingWindowAggerationFunction implements WindowAggerationFunction<Row>{

    private SlidingWindowConfig slidingWindowConfig;

    @Override
    public DataStream<Row> processWindow(DataStream<Row> dataStream) {

        AllWindowedStream<Row, TimeWindow> allWindowedStream = dataStream
                .windowAll(createSlidingWindowAssigner());

        List<SingleOutputStreamOperator<Row>> collect = doAggregate(allWindowedStream, dataStream.getType(), dataStream.getExecutionConfig());

        Optional<SingleOutputStreamOperator<Row>> reduceStream = collect
                .stream()
                .reduce((rowSingleOutputStreamOperator, rowSingleOutputStreamOperator2) -> (SingleOutputStreamOperator<Row>) rowSingleOutputStreamOperator.union(rowSingleOutputStreamOperator2));

        return reduceStream.isPresent() ? reduceStream.get() : null;
    }

    private WindowAssigner createSlidingWindowAssigner(){
        switch (slidingWindowConfig.getTimeType()){
            case event:
                return SlidingEventTimeWindows.of(Time.of(slidingWindowConfig.getTimeSize(), slidingWindowConfig.getTimeUnit()),
                                                    Time.of(slidingWindowConfig.getSlideSize(), slidingWindowConfig.getTimeUnit()));
            default:
                return SlidingProcessingTimeWindows.of(Time.of(slidingWindowConfig.getTimeSize(), slidingWindowConfig.getTimeUnit()),
                                                    Time.of(slidingWindowConfig.getSlideSize(), slidingWindowConfig.getTimeUnit()));
        }
    }

    @Override
    public DataStream<Row> processKeyByWindow(DataStream<Row> dataStream) {

        DataStream<Row> assignTimestampsStream = doAssignTimestampsAndWatermarks(dataStream);

        KeyedStream<Row, Object> rowTupleKeyedStream = doKeyby(slidingWindowConfig, assignTimestampsStream);

        WindowedStream<Row, Object, TimeWindow> windowedStream = rowTupleKeyedStream.window(createSlidingWindowAssigner());

        List<DataStream<Row>> collect = doKeyByAggregate(windowedStream, dataStream.getType(), dataStream.getExecutionConfig());

        Optional<DataStream<Row>> reduceStream = collect.stream().reduce((rowSingleOutputStreamOperator, rowSingleOutputStreamOperator2) -> rowSingleOutputStreamOperator.union(rowSingleOutputStreamOperator2));

        return reduceStream.isPresent() ? reduceStream.get() : null;
    }

    public void setSlidingWindowConfig(SlidingWindowConfig slidingWindowConfig) {
        this.slidingWindowConfig = slidingWindowConfig;
    }

    public static SlidingWindowAggerationFunction of(SlidingWindowConfig slidingWindowConfig){
        SlidingWindowAggerationFunction slidingWindowAggerationFunction = new SlidingWindowAggerationFunction();
        slidingWindowAggerationFunction.setSlidingWindowConfig(slidingWindowConfig);
        slidingWindowAggerationFunction.setTumblingWindowConfig(slidingWindowConfig);
        return slidingWindowAggerationFunction;
    }

}
