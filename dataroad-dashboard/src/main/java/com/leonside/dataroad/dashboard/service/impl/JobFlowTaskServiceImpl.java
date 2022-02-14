package com.leonside.dataroad.dashboard.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leonside.dataroad.common.utils.JsonUtil;
import com.leonside.dataroad.common.utils.StringUtil;
import com.leonside.dataroad.dashboard.configuration.DataroadProperties;
import com.leonside.dataroad.dashboard.domian.*;
import com.leonside.dataroad.dashboard.service.JobFlowService;
import com.leonside.dataroad.dashboard.service.JobFlowTaskService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharSet;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MultivaluedMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

/**
 * @author leon
 */
@Service
@Slf4j
public class JobFlowTaskServiceImpl implements JobFlowTaskService {

    private RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private JobFlowService jobFlowService;
    @Autowired
    private DataroadProperties dataroadProperties;
    @Override
    public JobSubmitReponse submitJobFlow(JobRequestParam jobRequestParam) throws JsonProcessingException {

        //获取JarID, 未上传则提示上传jar
        JobJarsReponse jobJarsReponse = listDataroadJar();
        JobJarsReponse.FileJarInfo dataroadJar = jobJarsReponse.findFirstJarNotNull();

        String jobFlowJson = jobFlowService.loadJobFlowJson(jobRequestParam.getJobId());

        JobSubmitParam newJobParam = JobSubmitParam.builder()
                .setJobId(jobRequestParam.getJobId())
                .setJobFlowJson(jobFlowJson)
                .setDataroadProperties(dataroadProperties)
                .setSavepointPath(jobRequestParam.getSavepointPath())
                .setParallelism(jobRequestParam.getParallelism())
                .build();

        log.debug("ready to submit Job, parameter:" + newJobParam);
        //提交Job
        JobSubmitReponse response = restTemplate.postForObject(dataroadProperties.getJobSubmitURL(dataroadJar.getId()), newJobParam, JobSubmitReponse.class);

        log.debug("Submit Job completed:" + response.toString());

        return response;
    }

    @Override
    public JobUploadReponse uploadDataroadJar() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(dataroadProperties.getJobJarSubmitURL());
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("jarfile", dataroadProperties.getDataroadDistMainJar());
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);

        CloseableHttpResponse response = client.execute(httpPost);
        List<String> responseList = IOUtils.readLines(response.getEntity().getContent(), Charset.forName("UTF-8"));

        JobUploadReponse jobUploadReponse = JsonUtil.getInstance().readJson(StringUtils.join(responseList, ""), JobUploadReponse.class);

        log.debug("upload JobJar completed:" + jobUploadReponse.toString());

        return jobUploadReponse;
    }

    @Override
    public JobJarsReponse listDataroadJar() {
        JobJarsReponse jobJarsReponse = restTemplate.getForObject(dataroadProperties.getJobJarListURL(), JobJarsReponse.class);
        return jobJarsReponse;
    }
}
