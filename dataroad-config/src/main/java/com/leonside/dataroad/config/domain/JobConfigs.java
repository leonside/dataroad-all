package com.leonside.dataroad.config.domain;

import lombok.Data;

/**
 * @author leon
 */
@Data
public class JobConfigs {

    private JobConfig job;

    public static JobConfigs of(JobConfig job){
        JobConfigs jobConfigs = new JobConfigs();
        jobConfigs.setJob(job);
        return jobConfigs;
    }
}
