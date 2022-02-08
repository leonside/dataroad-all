package com.leonside.dataroad.core.builder;

import com.google.common.collect.Lists;
import com.leonside.dataroad.core.aggregations.AggerationEnum;
import com.leonside.dataroad.core.aggregations.config.BaseWindowConfig;
import com.leonside.dataroad.core.component.JobExtensionLoader;
import com.leonside.dataroad.core.component.ComponentType;
import com.leonside.dataroad.core.spi.ItemAggregationProcessor;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
public class AggerationBuilder<T extends BaseJobFlowBuilder> {

//    private Window window;

    private T jobFlowBuilder;

    public Map<String, List<AggerationEnum>> aggerations = new HashMap<>();

    private ItemAggregationProcessor aggerationItemProcessor;

    private BaseWindowConfig baseWindowConfig;

    public AggerationBuilder(T jobFlowBuilder,Window window) {
//        this.window = window;
        this.jobFlowBuilder = jobFlowBuilder;
    }

    public AggerationBuilder(T jobFlowBuilder, BaseWindowConfig baseWindowConfig) {
//        this.window = window;
        this.jobFlowBuilder = jobFlowBuilder;
        this.baseWindowConfig = baseWindowConfig;
        this.baseWindowConfig.setAggerations(aggerations);
    }

    public static <T extends BaseJobFlowBuilder> AggerationBuilder<T> newInstance(T jobFlowBuilder, BaseWindowConfig baseWindowConfig){
        return new AggerationBuilder<T>(jobFlowBuilder, baseWindowConfig);
    }

    public AggerationBuilder<T> stats(String field){
        putAggeration(field, AggerationEnum.STATS);
        return this;
    }

    private void putAggeration(String field, AggerationEnum aggerationEnum) {
        List<AggerationEnum> aggerationEnums = aggerations.computeIfAbsent(field, key -> Lists.newArrayList());
        aggerationEnums.add(aggerationEnum);
    }

    public AggerationBuilder<T> avg(String field){
        putAggeration(field, AggerationEnum.AVG);
        return this;
    }

    public AggerationBuilder<T> sum(String field){
        putAggeration(field, AggerationEnum.SUM);
        return this;
    }

    public AggerationBuilder<T> count(String field){
        putAggeration(field, AggerationEnum.COUNT);
        return this;
    }

    public AggerationBuilder<T> max(String field){
        putAggeration(field, AggerationEnum.MAX);
        return this;
    }

    public AggerationBuilder<T> min(String field){
        putAggeration(field, AggerationEnum.MIN);
        return this;
    }

    public AggerationBuilder<T> topHits(String field){
        putAggeration(field, AggerationEnum.TOPHITS);
        return this;
    }

    public T aggeration() {

        aggerationItemProcessor = JobExtensionLoader.getComponent(ComponentType.agg, baseWindowConfig.windowComponentName());
        aggerationItemProcessor.initialize(baseWindowConfig);
        jobFlowBuilder.processor(aggerationItemProcessor);
        return this.jobFlowBuilder;
    }

    @Data
    public static abstract class Window implements Serializable {
        protected String[] keyBy;
    }
    @Data
    public static class CountWindow extends Window{
        private int size;

        public CountWindow(int size) {
            this.size = size;
        }
        public CountWindow(int size, String[] keyby) {
            this.size = size;
            this.keyBy = keyby;
        }
    }
    @Data
    public static class TumblingWindow extends Window{
        private Time size;
        public TumblingWindow(Time size) {
            this.size = size;
        }
        public TumblingWindow(Time size, String[] keyby) {
            this.size = size;
            this.keyBy = keyby;
        }
    }
    @Data
    public static class SlidingWindow extends Window{
        private Time size;
        private Time slide;
        public SlidingWindow(Time size, Time slide) {
            this.size = size;
            this.slide = slide;
        }
        public SlidingWindow(Time size, Time slide, String[] keyby) {
            this.size = size;
            this.slide = slide;
            this.keyBy = keyby;
        }
    }

}
