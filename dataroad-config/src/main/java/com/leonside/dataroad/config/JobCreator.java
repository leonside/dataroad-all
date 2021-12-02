package com.leonside.dataroad.config;

import com.leonside.dataroad.config.domain.JobConfigs;
import com.leonside.dataroad.core.Job;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author leon
 */
public interface JobCreator extends Serializable {

    List<Job> createJob(JobConfigs jobConfigs) throws Exception;

    List<Job> createJobByPath(String path) throws Exception;
}
