package com.leonside.dataroad.core.predicate;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.core.flow.JobFlow;
import com.leonside.dataroad.core.spi.JobExecutionDecider;
import com.leonside.dataroad.core.spi.JobPredicate;

import java.io.Serializable;

public class ExecuteStatusPredicate implements JobPredicate<ExecuteContext, Object>, Serializable {

    private String name;
    private JobExecutionDecider jobExecutionDecider;
    private JobFlow parentJobFlow;

    public ExecuteStatusPredicate(String name, JobExecutionDecider jobExecutionDecider, JobFlow parentJobFlow){
        this.name = name;
        this.jobExecutionDecider = jobExecutionDecider;
        this.parentJobFlow = parentJobFlow;
    }

    @Override
    public boolean test(ExecuteContext context, Object row) {
        return this.jobExecutionDecider.decide(context, row).ismatch(this.name);
    }

    public JobExecutionDecider getJobExecutionDecider() {
        return jobExecutionDecider;
    }

}