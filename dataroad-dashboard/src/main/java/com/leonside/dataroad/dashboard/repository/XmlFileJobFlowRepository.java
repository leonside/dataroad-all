package com.leonside.dataroad.dashboard.repository;

import com.alibaba.fastjson.JSONObject;
import com.leonside.dataroad.common.exception.JobFlowException;
import com.leonside.dataroad.common.utils.JsonUtil;
import com.leonside.dataroad.dashboard.converter.JobFlowConverter;
import com.leonside.dataroad.dashboard.domian.JobFlowConfig;
import com.leonside.dataroad.dashboard.domian.JobFlowConfigs;
import com.leonside.dataroad.dashboard.utils.HomeFolderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@Repository
@Slf4j
public class XmlFileJobFlowRepository implements JobFlowRepository {


    private JobFlowConfigs jobFlowConfigs = new JobFlowConfigs();

    private Map<String, JobFlowConfig> jobFlowConfigMap = new HashMap<>();

    private final File file;
    //是否首次启动
    private boolean hasInited = false;

    private JAXBContext jaxbContext;

    private JsonUtil jsonUtil = JsonUtil.getInstance();

    public XmlFileJobFlowRepository(){
        file = new File(HomeFolderUtils.getFilePathInHomeFolder(HomeFolderUtils.STORE_FILE_NAME));

        log.info("Obtain the dataroad home path：" + HomeFolderUtils.getHomeFolder());

        HomeFolderUtils.createHomeFolderIfNotExisted();
        if(file.exists()){
            hasInited = true;
        }
        try {

            jaxbContext = JAXBContext.newInstance(JobFlowConfigs.class);
        } catch (final JAXBException  ex) {
            throw new JobFlowException(ex.getMessage());
        }
    }

    @Override
    public void delete(String id) throws Exception {
        List<JobFlowConfig> jobFlowConfigs = loadAll();

        if(jobFlowConfigMap.containsKey(id)){
            JobFlowConfig jobFlowConfig = jobFlowConfigMap.remove(id);
            jobFlowConfigs.remove(jobFlowConfig);

            createOrWriterXmlFile(file, JobFlowConfigs.of(jobFlowConfigs));
            deleteFile(new File(HomeFolderUtils.getSchemaFilePathInHomeFolder(id + ".json")));
        }
    }

    @Override
    public void updateJobFlow(JobFlowConfig jobFlowConfig) throws Exception {
        List<JobFlowConfig> jobFlowConfigs = loadAll();

        jobFlowConfigMap.get(jobFlowConfig.getId()).copy(jobFlowConfig);

        createOrWriterXmlFile(file, JobFlowConfigs.of(jobFlowConfigs));
        createOrWriterSchemaFile(getSchemaFile(jobFlowConfig.getId()), jobFlowConfig.getJobflowJson());
    }

    @Override
    public void updateJobFlowBase(JobFlowConfig jobFlowConfig) throws Exception {
        List<JobFlowConfig> jobFlowConfigs = loadAll();

        JobFlowConfig loadJobFlowConfig = jobFlowConfigMap.get(jobFlowConfig.getId());
        loadJobFlowConfig.setGolbalSetting(jobFlowConfig.getGolbalSetting());
        loadJobFlowConfig.setDescription(jobFlowConfig.getDescription());

        //更新json值
        if(StringUtils.isNotEmpty(loadJobFlowConfig.getDesignerJson())){
            String jobFlowJson = new JobFlowConverter(loadJobFlowConfig.getDesignerJson(),loadJobFlowConfig.getGolbalSetting() ).convert();
            loadJobFlowConfig.setJobflowJson(jobFlowJson);
        }

        createOrWriterXmlFile(file, JobFlowConfigs.of(jobFlowConfigs));
        createOrWriterSchemaFile(getSchemaFile(jobFlowConfig.getId()), loadJobFlowConfig.getJobflowJson());
    }

    @Override
    public void init(InputStream sampleConfigStream) throws Exception {

        if(hasInited ){
           return;
        }

        log.info("Start and initialize the sample data for the first time");

        JAXBContext jaxbContextSample = JAXBContext.newInstance(JobFlowConfigs.class);
        JobFlowConfigs sampleConfigs = (JobFlowConfigs) jaxbContextSample.createUnmarshaller().unmarshal(sampleConfigStream);

        if(CollectionUtils.isNotEmpty(sampleConfigs.getJobFlowConfigs())){
            createOrWriterXmlFile(file, sampleConfigs);
            sampleConfigs.getJobFlowConfigs().stream().forEach(jobFlowConfig -> {
                try {
                    //更新json值
                    if(StringUtils.isNotEmpty(jobFlowConfig.getDesignerJson())){
                        String jobFlowJson = new JobFlowConverter(jobFlowConfig.getDesignerJson(),jobFlowConfig.getGolbalSetting() ).convert();
                        createOrWriterSchemaFile(getSchemaFile(jobFlowConfig.getId()), jobFlowJson);
                    }
                } catch (IOException e) {
                    log.info("Failed to initialize sample data, JSON:" + jobFlowConfig.getDesignerJson(),e );
                }
            });
        }
        hasInited = true;
        log.info("Initialization of sample data is complete..");
    }


    @Override
    public void save(JobFlowConfig jobFlowConfig) throws Exception {

        List<JobFlowConfig> jobFlowConfigs = loadAll();

        if(jobFlowConfigMap.containsKey(jobFlowConfig.getId())){
            jobFlowConfigMap.get(jobFlowConfig.getId()).copy(jobFlowConfig);
        }else{
            jobFlowConfigs.add(jobFlowConfig);
            jobFlowConfigMap.put(jobFlowConfig.getId(), jobFlowConfig);
        }

        createOrWriterXmlFile(file, JobFlowConfigs.of(jobFlowConfigs));
        createOrWriterSchemaFile(getSchemaFile(jobFlowConfig.getId()), jobFlowConfig.getJobflowJson());
    }

    private File getSchemaFile(String id){
       return new File(HomeFolderUtils.getSchemaFilePathInHomeFolder(id + ".json"));
    }

    private void createOrWriterXmlFile(File file, JobFlowConfigs jobFlowConfigs) throws IOException {
        if (!file.exists()) {
            FileUtils.forceMkdirParent(file);
            file.createNewFile();
        }
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(jobFlowConfigs, file);
        } catch (final JAXBException ex) {
            throw new JobFlowException(ex.getMessage(),ex);
        }
    }

    private void createOrWriterSchemaFile(File schemaFile, String writeJson) throws IOException {
        if(StringUtils.isEmpty(writeJson)){
            return;
        }

        if(!schemaFile.exists() ){
            FileUtils.forceMkdirParent(schemaFile);
            schemaFile.createNewFile();
        }

        FileUtils.write(schemaFile, jsonUtil.prettyJson(jsonUtil.readJson(writeJson, JSONObject.class)), StandardCharsets.UTF_8);
    }

    private void deleteFile(File file) throws IOException {
        if(file.exists()){
            FileUtils.deleteQuietly(file);
        }
    }

    @Override
    public JobFlowConfig get(String id) {
        if(jobFlowConfigMap.isEmpty()){
            try {
                loadAll();
            } catch (Exception e) {
                throw new JobFlowException("load configuration exception",e);
            }
        }
        return jobFlowConfigMap.get(id);
    }


    @Override
    public List<JobFlowConfig> loadAll() throws  Exception {

        if(CollectionUtils.isEmpty(jobFlowConfigs.getJobFlowConfigs()) ){
            if(file.exists()){
                jobFlowConfigs = (JobFlowConfigs) jaxbContext.createUnmarshaller().unmarshal(file);
                jobFlowConfigs.getJobFlowConfigs().stream().forEach(item->{
                    try {
                        File schemaFile = getSchemaFile(item.getId());
                        if(schemaFile.exists()){
                            String jobflowJson = IOUtils.toString(new FileInputStream(schemaFile), Charsets.UTF_8);
                            item.setJobflowJson(jobflowJson);
                        }
                    } catch (IOException e) {
                        throw new JobFlowException("read jobflow json config exception ["+ item.getId()+"]");
                    }
                    jobFlowConfigMap.put(item.getId(),item);
                });
            }else{
                jobFlowConfigs.setJobFlowConfigs(new ArrayList<>());
            }

        }

        return jobFlowConfigs.getJobFlowConfigs();
    }

    @Override
    public boolean containJobFlow(String id) {
        return jobFlowConfigMap.containsKey(id);
    }


}
