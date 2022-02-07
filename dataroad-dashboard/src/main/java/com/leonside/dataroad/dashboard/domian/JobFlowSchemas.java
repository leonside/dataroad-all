package com.leonside.dataroad.dashboard.domian;

import lombok.Data;

/**
 * @author leon
 */
@Data
public class JobFlowSchemas {

    private JobFlowSchema job;

    public static JobFlowSchemas of(JobFlowSchema job){
        JobFlowSchemas jobConfigs = new JobFlowSchemas();
        jobConfigs.setJob(job);
        return jobConfigs;
    }
}
