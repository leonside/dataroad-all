package com.leonside.dataroad.core.builder;


import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.extension.ExtensionLoader;
import com.leonside.dataroad.common.spi.ItemReader;
import com.leonside.dataroad.common.spi.ItemUnionProcessor;
import com.leonside.dataroad.common.utils.Asserts;
import com.leonside.dataroad.core.Job;
import com.leonside.dataroad.core.component.JobExtensionLoader;
import com.leonside.dataroad.core.component.ComponentType;
import com.leonside.dataroad.core.flow.SimpleJobFlow;
import com.leonside.dataroad.core.spi.JobEngineCreator;
import com.leonside.dataroad.core.spi.JobExecutionDecider;

/**
 * @author leon
 */
public class JobFlowBuilder extends BaseJobFlowBuilder<JobFlowBuilder> {

    private JobEngineCreator jobEngineCreator = ExtensionLoader.getExtensionLoader(JobEngineCreator.class).getFirstExtension();

    protected SimpleJobFlow startJobFlow;

    protected JobBuilder jobBuilder;

    public JobFlowBuilder(JobBuilder jobBuilder) {
        this.jobBuilder = jobBuilder;
    }

    public static JobFlowBuilder newInstance(JobBuilder jobBuilder) {
        return new JobFlowBuilder(jobBuilder);
    }

    public JobFlowBuilder reader(ItemReader itemReader) {
        Asserts.notNull(itemReader, "itemReader can not be null");
        SimpleJobFlow jobFlow = SimpleJobFlow.of(itemReader);
        startJobFlow = jobFlow;
        currentJobFlow = jobFlow;
        jobFlows.add(jobFlow);
        return this;
    }

    public MultiJobFlowBuilder decider(JobExecutionDecider decider){
        Asserts.notNull(decider, "decider can not be null");
        MultiJobFlowBuilder multiJobFlowBuilder = MultiJobFlowBuilder.newInstance( this, decider);
        return multiJobFlowBuilder;
    }

    public MultiJobFlowBuilder decider(){
        MultiJobFlowBuilder multiJobFlowBuilder = MultiJobFlowBuilder.newInstance( this,null);
        return multiJobFlowBuilder;
    }

    public JobFlowBuilder union(int... flowindexs){
        ItemUnionProcessor itemUnionProcessor = JobExtensionLoader.getSingleComponent(ComponentType.union);
        itemUnionProcessor.initializeUnionFlowIndex(flowindexs);
        SimpleJobFlow jobFlow = SimpleJobFlow.of(itemUnionProcessor);
        addNextJobFlow(jobFlow);
        return this;
    }

    public JobFlowBuilder union(){
        //todo
        SimpleJobFlow jobFlow = SimpleJobFlow.of(JobExtensionLoader.getSingleComponent(ComponentType.union));
        addNextJobFlow(jobFlow);
        return this;
    }

    public Job build(){

        System.out.printf(startJobFlow.toString());
        //todo SPI  构建flink的job、全局配置的传递？

        ExecuteContext executeContext = jobBuilder.getExecuteContext();

        return jobEngineCreator.create(executeContext, startJobFlow);
    }

}
