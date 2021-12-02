package com.leonside.dataroad.config.job;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.exception.JobFlowException;
import com.leonside.dataroad.common.spi.JobExecutionListener;
import com.leonside.dataroad.config.ComponentFactory;
import com.leonside.dataroad.config.JobCreator;
import com.leonside.dataroad.config.JobSchemaParser;
import com.leonside.dataroad.config.domain.GenericComponentConfig;
import com.leonside.dataroad.config.domain.JobConfig;
import com.leonside.dataroad.config.domain.JobConfigs;
import com.leonside.dataroad.core.Job;
import com.leonside.dataroad.core.builder.JobBuilder;
import com.leonside.dataroad.core.builder.JobFlowBuilder;
import com.leonside.dataroad.core.builder.MultiJobFlowBuilder;
import com.leonside.dataroad.core.predicate.OtherwisePredicate;
import com.leonside.dataroad.core.spi.JobPredicate;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author leon
 */
public class JsonJobCreator implements JobCreator {

    private JobSchemaParser jobSchemaParser;


    public JsonJobCreator(JobSchemaParser jobSchemaParser) {
        this.jobSchemaParser = jobSchemaParser;
    }

    @Override
    public List<Job> createJobByPath(String path) throws Exception {
        return createJob(jobSchemaParser.parserJSONPath(path));
    }

    @Override
    public List<Job> createJob(JobConfigs jobs) throws Exception {
        JobConfig job = jobs.getJob();

        if(CollectionUtils.isEmpty(job.getContent())){
            throw new JobFlowException("job content can not be null.");
        }

        //构建Job节点的上下级树形关系
        job.buildJobFlowRelation();

        List<Job> jobList = new ArrayList<>();

        for (Map<String, GenericComponentConfig> componentConfigMap : job.getContent()) {
            GenericComponentConfig startComponentConfig = componentConfigMap.values().iterator().next();
            ExecuteContext executeContext = ExecuteContext.of(job.getSetting(), null);
            JobBuilder jobBuilder = JobBuilder.newInstance().listener(new JobExecutionListener() {
            }).executeContext(executeContext); //todo
            JobFlowBuilder jobFlowBuilder = jobBuilder.reader(ComponentFactory.getComponent(executeContext, startComponentConfig));

            MultiJobFlowBuilder currentDecider = null;

            doCreateMainFlow(executeContext, jobFlowBuilder, currentDecider, startComponentConfig.getChilds());

            Job buildJob = jobFlowBuilder.build();

            jobList.add(buildJob);
        }

        return jobList;
    }

    private void doCreateMainFlow(ExecuteContext executeContext, JobFlowBuilder jobFlowBuilder, MultiJobFlowBuilder currentDecider, Set<GenericComponentConfig> genericComponentConfigs) {
        if(genericComponentConfigs.size() >= 2){
            GenericComponentConfig lastDeciderFlow = (GenericComponentConfig) genericComponentConfigs.toArray()[genericComponentConfigs.size() - 1];
            lastDeciderFlow.markCurrentDeciderLastFlow();
        }

        for (GenericComponentConfig child : genericComponentConfigs) {
            switch (child.getType()){
                case reader:
                    jobFlowBuilder.reader(ComponentFactory.getComponent(executeContext, child));
                    if(CollectionUtils.isNotEmpty(child.getChilds())){
                        doCreateMainFlow(executeContext,jobFlowBuilder, currentDecider, child.getChilds());
                    }
                    break;
                case writer:
                    jobFlowBuilder.writer(ComponentFactory.getComponent(executeContext, child));
                    if(CollectionUtils.isNotEmpty(child.getChilds())){
                        doCreateMainFlow(executeContext, jobFlowBuilder, currentDecider, child.getChilds());
                    }
                    break;
                case processor:
                case agg:
                    jobFlowBuilder.processor(ComponentFactory.getComponent(executeContext,child));
                    if(CollectionUtils.isNotEmpty(child.getChilds())){
                        doCreateMainFlow(executeContext,jobFlowBuilder, currentDecider, child.getChilds());
                    }
                    break;
                case deciderOn:
                    if(currentDecider == null){
                        currentDecider = jobFlowBuilder.decider();
                    }
                    JobPredicate jobPredicate = ComponentFactory.getJobPredicate(executeContext,child);
                    if(jobPredicate instanceof OtherwisePredicate){
                        currentDecider.otherwise();
                    }else{
                        currentDecider.on(jobPredicate);
                    }
                    doCreateDeciderFlow(executeContext,currentDecider, child);

                    break;
                case union:
                    jobFlowBuilder.union();
                    if(CollectionUtils.isNotEmpty(child.getChilds())){
                        doCreateMainFlow(executeContext,jobFlowBuilder, currentDecider, child.getChilds());
                    }
                    break;
//                case join:
                default:
                    break;
            }
        }

    }

    private void doCreateDeciderFlow(ExecuteContext executeContext, MultiJobFlowBuilder currentDecider, GenericComponentConfig genericComponentConfig) {
        for (GenericComponentConfig child : genericComponentConfig.getChilds()) {
            switch (child.getType()){
                case writer:
                    currentDecider.writer(ComponentFactory.getComponent(executeContext,child));
                    if(CollectionUtils.isNotEmpty(child.getChilds())){
                        doCreateDeciderFlow(executeContext,currentDecider, child);
                    }
                    break;
                case processor:
                    currentDecider.processor(ComponentFactory.getComponent(executeContext,child));
                    if(CollectionUtils.isNotEmpty(child.getChilds())){
                        doCreateDeciderFlow(executeContext, currentDecider, child);
                    }
                    break;
//            case agg:
//                jobFlowBuilder = jobFlowBuilder.(ComponentFactory.getComponent(child));
//                break;
//                case deciderEnd:
//                    currentDecider.end();
//                    currentDecider = null;
//                    if(CollectionUtils.isNotEmpty(child.getChilds())){
//                        doCreateDeciderFlow(currentDecider, child.getChilds());
//                    }
//                    break;

//                case join:
                default:
                    break;
            }
            if(child.isDeciderLastFlow()){
                JobFlowBuilder jobFlowBuilder = currentDecider.getJobFlowBuilder();
                processDeciderEnd(currentDecider);
                doCreateMainFlow(executeContext,jobFlowBuilder, currentDecider,child.getChilds());
            }
        }

    }

    private void processDeciderEnd(MultiJobFlowBuilder currentDecider) {
        //进行分流end处理,并将其设置为null.
        if(currentDecider != null){
            currentDecider.end();
            currentDecider = null;
        }
    }

}
