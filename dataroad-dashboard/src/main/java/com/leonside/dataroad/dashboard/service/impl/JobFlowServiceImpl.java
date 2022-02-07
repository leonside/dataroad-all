package com.leonside.dataroad.dashboard.service.impl;

import com.leonside.dataroad.dashboard.converter.JobFlowConverter;
import com.leonside.dataroad.dashboard.domian.JobFlowConfig;
import com.leonside.dataroad.dashboard.repository.JobFlowRepository;
import com.leonside.dataroad.dashboard.service.JobFlowService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leon
 */
@Service
public class JobFlowServiceImpl implements JobFlowService {

    @Autowired
    private JobFlowRepository jobFlowRepository;

    @Override
    public boolean containJobFlow(String id) {
        return jobFlowRepository.containJobFlow(id);
    }

    @Override
    public void deleteJobFlow(String id) throws Exception {
        jobFlowRepository.delete(id);
    }

    @Override
    public void updateJobFlow(JobFlowConfig jobFlowConfig) throws Exception {

        if(StringUtils.isNotEmpty(jobFlowConfig.getDesignerJson())){
            String jobFlowJson = new JobFlowConverter(jobFlowConfig.getDesignerJson(), jobFlowConfig.getGolbalSetting()).convert();
            jobFlowConfig.setJobflowJson(jobFlowJson);
        }

        if(StringUtils.isNotEmpty(jobFlowConfig.getDesignerJson())){
            jobFlowConfig.setStatus(true);
        }

        jobFlowRepository.updateJobFlow(jobFlowConfig);
    }

    @Override
    public void updateBaseJobFlow(JobFlowConfig jobFlowConfig) throws Exception {

        jobFlowConfig.setDescription(jobFlowConfig.getDescription());
        jobFlowConfig.setGolbalSetting(jobFlowConfig.getGolbalSetting());

        if(StringUtils.isNotEmpty(jobFlowConfig.getDesignerJson())){
            jobFlowConfig.setStatus(true);
        }

        jobFlowRepository.updateJobFlowBase(jobFlowConfig);
    }

    @Override
    public void saveJobFlow(JobFlowConfig jobFlowConfig) throws Exception {

        if(StringUtils.isNotEmpty(jobFlowConfig.getDesignerJson())){
            String jobFlowJson = new JobFlowConverter(jobFlowConfig.getGolbalSetting(), jobFlowConfig.getDesignerJson()).convert();
            jobFlowConfig.setJobflowJson(jobFlowJson);
        }

        jobFlowRepository.save(jobFlowConfig);
    }

    @Override
    public JobFlowConfig loadJobFlowConfig(String id) {
        return jobFlowRepository.get(id);
    }

    @Override
    public String loadJobFlowJson(String id) {
        return loadJobFlowConfig(id).getJobflowJson();
    }

    @Override
    public List<String> loadJobFlowJsons(String[] ids) {
        return Arrays.stream(ids).map(id-> loadJobFlowJson(id)).collect(Collectors.toList());
    }

    @Override
    public List<JobFlowConfig> loadJobFlowConfig() throws Exception {

        return jobFlowRepository.loadAll();
    }

}
