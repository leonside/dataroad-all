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
import com.leonside.dataroad.core.spi.JobEngineProvider;
import com.leonside.dataroad.core.spi.JobExecutionDecider;
import lombok.extern.slf4j.Slf4j;

/**
 * @author leon
 */
@Slf4j
public class JobFlowBuilder extends BaseJobFlowBuilder<JobFlowBuilder> {

    private JobEngineProvider jobEngineCreator = ExtensionLoader.getExtensionLoader(JobEngineProvider.class).getFirstExtension();

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



    public Job build(){

        log.debug(startJobFlow.toString());

        ExecuteContext executeContext = jobBuilder.getExecuteContext();

        return jobEngineCreator.createJob(executeContext, startJobFlow);
    }

}
