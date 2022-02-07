package com.leonside.dataroad.core.builder;

import com.leonside.dataroad.common.exception.JobFlowException;
import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.common.spi.ItemWriter;
import com.leonside.dataroad.core.component.JobExtensionLoader;
import com.leonside.dataroad.core.component.ComponentType;
import com.leonside.dataroad.core.flow.JobFlow;
import com.leonside.dataroad.core.flow.MultiJobFlow;
import com.leonside.dataroad.core.flow.SimpleJobFlow;
import com.leonside.dataroad.core.predicate.*;
import com.leonside.dataroad.core.spi.ItemDeciderProcessor;
import com.leonside.dataroad.core.spi.JobExecutionDecider;
import com.leonside.dataroad.core.spi.JobPredicate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author leon
 */
public class MultiJobFlowBuilder extends BaseJobFlowBuilder<MultiJobFlowBuilder> {

    private JobFlowBuilder jobFlowBuilder;

    private JobExecutionDecider jobExecutionDecider;

    private final Map<JobPredicate, JobFlow> jobFlowDeciders;

    private final ItemDeciderProcessor itemDeciderProcessor;

    private JobPredicate currentState;

    public Map<JobPredicate, JobFlow> getJobFlowDeciders() {
        return jobFlowDeciders;
    }

    public JobFlowBuilder getJobFlowBuilder() {
        return jobFlowBuilder;
    }

    public static MultiJobFlowBuilder newInstance(JobFlowBuilder jobFlowBuilder, JobExecutionDecider decider){
        return new MultiJobFlowBuilder(jobFlowBuilder, decider);
    }

    public MultiJobFlowBuilder(JobFlowBuilder jobFlowBuilder, JobExecutionDecider decider) {
        this(jobFlowBuilder);
        this.jobExecutionDecider = decider;
    }

    public MultiJobFlowBuilder( JobFlowBuilder jobFlowBuilder) {
        this.jobFlowBuilder = jobFlowBuilder;
        this.jobFlowDeciders = new LinkedHashMap<>();
        this.itemDeciderProcessor = JobExtensionLoader.getSingleComponent(ComponentType.decider);
        //initialize ItemDeciderProcessor
        itemDeciderProcessor.initialize(jobFlowDeciders);
    }

    public MultiJobFlowBuilder on(String name){

        if(jobExecutionDecider == null){
            throw new JobFlowException("Job decider must specify");
        }
        return deciderOn(new ExecuteStatusPredicate(name, jobExecutionDecider,jobFlowBuilder.getCurrentJobFlow() ));
    }

    public MultiJobFlowBuilder otherwise(){

//        if(jobExecutionDecider == null){
//            throw new JobFlowException("Job decider must specify");
//        }
        return deciderOn(new OtherwisePredicate());
    }

    private MultiJobFlowBuilder deciderOn(JobPredicate predicate){
        addJobFlowDecider(currentState, currentJobFlow);

        currentState = predicate;
        return this;
    }

    private void initCurrentJobFlow() {
        this.currentJobFlow = null;
    }

    public MultiJobFlowBuilder on(JobPredicate predicate){
        if(jobExecutionDecider != null){
            throw new JobFlowException("Job decider must be null");
        }
        deciderOn(predicate);
        return this;
    }

    private void addJobFlowDecider(JobPredicate predicate, JobFlow currentJobFlow) {
        if(currentJobFlow != null){
            //设置当前JobFlow parent为itemDeciderProcessor
            JobFlow rootParent = currentJobFlow.getRootParent();
            rootParent.setParent(SimpleJobFlow.of(itemDeciderProcessor));

            jobFlowDeciders.putIfAbsent(predicate, rootParent);
            //重置currentJobFlow
            initCurrentJobFlow(); //todo 初始化currentJobFlow
        }
    }

    @Override
    public MultiJobFlowBuilder processor(ItemProcessor processor) {
        if(currentState == null){
            throw new JobFlowException("Must have decider started ");
        }
        return super.processor(processor);
    }

    @Override
    public MultiJobFlowBuilder union() {
        if(currentState == null){
            throw new JobFlowException("Must have decider started ");
        }
        return super.union();
    }

    @Override
    public MultiJobFlowBuilder union(Integer... flowindexs) {
        if(currentState == null){
            throw new JobFlowException("Must have decider started ");
        }
        return super.union(flowindexs);
    }

    @Override
    public MultiJobFlowBuilder writer(ItemWriter writer) {
        if(currentState == null){
            throw new JobFlowException("Must have decider started");
        }
        return super.writer(writer);
    }

    @Override
    protected void addNextJobFlow(JobFlow jobFlow) {
        //todo check jobflow
        super.addNextJobFlow(jobFlow);
    }

    public JobFlowBuilder end(){

        addJobFlowDecider(currentState, currentJobFlow);

        MultiJobFlow multiJobFlow = MultiJobFlow.of(itemDeciderProcessor);

        jobFlowBuilder.flow(multiJobFlow);

        return jobFlowBuilder;
    }



}
