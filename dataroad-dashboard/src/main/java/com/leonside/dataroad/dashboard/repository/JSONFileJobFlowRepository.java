//package com.leonside.dataroad.dashboard.repository;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.leonside.dataroad.common.exception.JobFlowException;
//import com.leonside.dataroad.common.utils.JsonUtil;
//import com.leonside.dataroad.dashboard.configuration.JobflowProperties;
//import com.leonside.dataroad.dashboard.domian.JobFlowConfig;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.io.Charsets;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Repository;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.*;
//
///**
// * @author leon
// */
//@Repository
//public class JSONFileJobFlowRepository implements JobFlowRepository {
//
//    @Autowired
//    private JobflowProperties jobflowProperties;
//
//    private List<JobFlowConfig> jobFlowConfigs = new ArrayList<>();
//
//    private Map<String, JobFlowConfig> jobFlowConfigMap = new HashMap<>();
//
//    @Override
//    public void delete(String id) throws IOException {
//        List<JobFlowConfig> jobFlowConfigs = loadAll();
//
//        if(jobFlowConfigMap.containsKey(id)){
//            JobFlowConfig jobFlowConfig = jobFlowConfigMap.remove(id);
//            jobFlowConfigs.remove(jobFlowConfig);
//
//            createOrWriterFile(getJobflowConfigResource(), JsonUtil.getInstance().writeJson(jobFlowConfigs));
//            deleteFile(getJobflowJsonResource(id));
//        }
//    }
//
//    @Override
//    public void updateJobFlow(JobFlowConfig jobFlowConfig) throws IOException {
//        List<JobFlowConfig> jobFlowConfigs = loadAll();
//
//        jobFlowConfigMap.get(jobFlowConfig.getId()).copy(jobFlowConfig);
//
//        createOrWriterFile(getJobflowConfigResource(), JsonUtil.getInstance().writeJson(jobFlowConfigs));
//        createOrWriterFile(getJobflowJsonResource(jobFlowConfig.getId()), jobFlowConfig.getJobflowJson());
//    }
//
//    @Override
//    public void save(JobFlowConfig jobFlowConfig) throws IOException {
//
//        List<JobFlowConfig> jobFlowConfigs = loadAll();
//
//        if(jobFlowConfigMap.containsKey(jobFlowConfig.getId())){
//            jobFlowConfigMap.get(jobFlowConfig.getId()).copy(jobFlowConfig);
//        }else{
//            jobFlowConfigs.add(jobFlowConfig);
//            jobFlowConfigMap.put(jobFlowConfig.getId(), jobFlowConfig);
//        }
//
//        createOrWriterFile(getJobflowConfigResource(), JsonUtil.getInstance().writeJson(jobFlowConfigs));
//        createOrWriterFile(getJobflowJsonResource(jobFlowConfig.getId()), jobFlowConfig.getJobflowJson());
//    }
//
//    private void createOrWriterFile(ClassPathResource classPathResource, String writeJson) throws IOException {
//        if(StringUtils.isEmpty(writeJson)){
//            return;
//        }
//
//        if(!classPathResource.exists() ){
//            String uri = (new ClassPathResource("/").getURL() + classPathResource.getPath()).substring(6);
//            FileUtils.forceMkdirParent(new File(uri));
//            new File(uri).createNewFile();
//        }
//
//        FileUtils.write(classPathResource.getFile(), JsonUtil.getInstance().writeJson(writeJson));
//    }
//
//    private void deleteFile(ClassPathResource classPathResource) throws IOException {
//        if(classPathResource.exists()){
//            FileUtils.deleteQuietly(classPathResource.getFile());
//        }
//    }
//
//    @Override
//    public JobFlowConfig get(String id) {
//        return jobFlowConfigMap.get(id);
//    }
//
//    private ClassPathResource getJobflowConfigResource(){
//        return new ClassPathResource(jobflowProperties.getJobflowConfigFile());
//    }
//
//    private ClassPathResource getJobflowJsonResource(String id){
//        return new ClassPathResource(jobflowProperties.getJobflowJsonPath() + id + ".json");
//    }
//
//    @Override
//    public List<JobFlowConfig> loadAll() throws IOException {
//
//        if(CollectionUtils.isEmpty(jobFlowConfigs) && getJobflowConfigResource().exists() ){
//            String json = IOUtils.toString(getJobflowConfigResource().getInputStream(), Charsets.UTF_8);
//            jobFlowConfigs = JsonUtil.getInstance().readValue(json, new TypeReference<List<JobFlowConfig>>() {});
//            jobFlowConfigs.stream().forEach(item->{
//                try {
//                    if(getJobflowJsonResource(item.getId()).exists()){
//                        String jobflowJson = IOUtils.toString(getJobflowJsonResource(item.getId()).getInputStream(), Charsets.UTF_8);
//                        item.setJobflowJson(jobflowJson);
//                    }
//                } catch (IOException e) {
//                    throw new JobFlowException("read jobflow json config exception ["+ getJobflowJsonResource(item.getId()) +"]");
//                }
//                jobFlowConfigMap.put(item.getId(),item);
//            });
//        }
//
//        return jobFlowConfigs;
//    }
//
//    @Override
//    public boolean containJobFlow(String id) {
//        return jobFlowConfigMap.containsKey(id);
//    }
//
//
//}
