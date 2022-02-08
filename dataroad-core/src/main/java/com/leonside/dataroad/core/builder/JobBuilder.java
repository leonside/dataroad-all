package com.leonside.dataroad.core.builder;


import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.context.JobSetting;
import com.leonside.dataroad.common.spi.ItemReader;
import com.leonside.dataroad.common.spi.JobExecutionListener;
import com.leonside.dataroad.common.utils.Asserts;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leon
 */
public class JobBuilder {

    private List<JobExecutionListener> jobExecutionListeners = new ArrayList<>();
    private ExecuteContext executeContext;

    public static JobBuilder newInstance(){
        return new JobBuilder();
    }

    public JobBuilder listener(JobExecutionListener jobExecutionListener){
        jobExecutionListeners.add(jobExecutionListener);
        return this;
    }

    public JobFlowBuilder reader(ItemReader itemReader){
        Asserts.notNull(itemReader, "itemReader can not be null");
        JobFlowBuilder jobFlowBuilder = JobFlowBuilder.newInstance(this);
        return jobFlowBuilder.reader(itemReader);
    }

    public ExecuteContext getExecuteContext() {
        return executeContext;
    }

    public JobBuilder executeContext(ExecuteContext executeContext) {
        this.executeContext = executeContext;
        return this;
    }
}
