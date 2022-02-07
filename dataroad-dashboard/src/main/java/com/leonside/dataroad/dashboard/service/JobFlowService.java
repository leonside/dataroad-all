package com.leonside.dataroad.dashboard.service;

import com.leonside.dataroad.dashboard.domian.JobFlowConfig;

import java.io.IOException;
import java.util.List;

/**
 * @author leon
 */
public interface JobFlowService {

    boolean containJobFlow(String id);

    void saveJobFlow(JobFlowConfig jobFlowConfig) throws Exception;

    JobFlowConfig loadJobFlowConfig(String id);

    String loadJobFlowJson(String id);

    List<String> loadJobFlowJsons(String[] ids);

    List<JobFlowConfig> loadJobFlowConfig() throws Exception;

    void deleteJobFlow(String id) throws Exception;

    void updateJobFlow(JobFlowConfig jobFlowConfig) throws Exception;

    void updateBaseJobFlow(JobFlowConfig jobFlowConfig) throws Exception;
}
