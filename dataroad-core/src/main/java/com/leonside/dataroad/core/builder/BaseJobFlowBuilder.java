package com.leonside.dataroad.core.builder;


import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.common.spi.ItemWriter;
import com.leonside.dataroad.common.utils.Asserts;
import com.leonside.dataroad.core.aggregations.config.CountWindowConfig;
import com.leonside.dataroad.core.aggregations.config.SlidingWindowConfig;
import com.leonside.dataroad.core.aggregations.config.TumblingWindowConfig;
import com.leonside.dataroad.core.flow.JobFlow;
import com.leonside.dataroad.core.flow.SimpleJobFlow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leon
 */
public abstract class BaseJobFlowBuilder<T extends BaseJobFlowBuilder> {

    protected List<JobFlow> jobFlows = new ArrayList<>();

    protected JobFlow currentJobFlow;

    public JobFlow getCurrentJobFlow() {
        return currentJobFlow;
    }

    public AggerationBuilder<T> countWindowByKey(String[] keys, int size){
        CountWindowConfig countWindowConfig = new CountWindowConfig();
        countWindowConfig.setKeyBy(keys);
        countWindowConfig.setWindowSize(size);
        return (AggerationBuilder<T>) AggerationBuilder.newInstance((JobFlowBuilder)this, countWindowConfig);
    }
    //滚动窗口
    public AggerationBuilder<T> tumblingWindowByKey(String[] keys, Time size){
        TumblingWindowConfig tumblingWindowConfig = new TumblingWindowConfig();
        tumblingWindowConfig.setKeyBy(keys);
        tumblingWindowConfig.setTimeUnit(size.getUnit());
        tumblingWindowConfig.setTimeSize(size.getSize());
        return AggerationBuilder.newInstance((T)this,tumblingWindowConfig);
    }
    //滑动窗口
    public AggerationBuilder<T> slidingWindowByKey(String[] keys, Time size, Time slide){
        SlidingWindowConfig slidingWindowConfig = new SlidingWindowConfig();
        slidingWindowConfig.setKeyBy(keys);
        slidingWindowConfig.setTimeSize(size.getSize());
        slidingWindowConfig.setTimeUnit(size.getUnit());
        slidingWindowConfig.setSlideSize(slide.getSize());
        return AggerationBuilder.newInstance((T)this,slidingWindowConfig);
    }

    public AggerationBuilder<T> countWindow(int size){
        return (AggerationBuilder<T>) countWindowByKey(null, size);
    }
    //滚动窗口
    public AggerationBuilder<T> tumblingWindow(Time size){
        return tumblingWindowByKey(null, size);
    }
    //滑动窗口
    public AggerationBuilder<T> slidingWindow(Time size, Time slide){
        return slidingWindowByKey(null, size, slide);
    }

    public T processor(ItemProcessor processor){
        Asserts.notNull(processor, "processor can not be null");
        SimpleJobFlow jobFlow = SimpleJobFlow.of(processor);
        addNextJobFlow(jobFlow);
        return (T)this;
    }

    public T flow(JobFlow jobFlow){
        Asserts.notNull(jobFlow, "jobFlow can not be null");
        addNextJobFlow(jobFlow);
        return (T)this;
    }

    public T writer(ItemWriter writer){
        Asserts.notNull(writer, "itemWriter can not be null");
        SimpleJobFlow jobFlow = SimpleJobFlow.of(writer);
        addNextJobFlow(jobFlow);
        return (T)this;
    }

    protected void addNextJobFlow(JobFlow jobFlow) {
        //todo check jobflow
        if(currentJobFlow != null){
            currentJobFlow.addChildren(jobFlow);
        }
        currentJobFlow = jobFlow;
        jobFlows.add(jobFlow);
    }

}
