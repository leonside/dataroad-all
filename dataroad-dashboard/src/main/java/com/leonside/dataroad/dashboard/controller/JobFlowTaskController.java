package com.leonside.dataroad.dashboard.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leonside.dataroad.common.exception.JobException;
import com.leonside.dataroad.common.exception.JobFlowException;
import com.leonside.dataroad.common.utils.DateUtil;
import com.leonside.dataroad.common.utils.JsonUtil;
import com.leonside.dataroad.dashboard.configuration.DataroadProperties;
import com.leonside.dataroad.dashboard.converter.JobFlowConverter;
import com.leonside.dataroad.dashboard.domian.*;
import com.leonside.dataroad.dashboard.domian.ResponseStatus;
import com.leonside.dataroad.dashboard.service.JobFlowService;
import com.leonside.dataroad.dashboard.service.JobFlowTaskService;
import com.leonside.dataroad.dashboard.utils.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @author leon
 */
@Slf4j
@RestController
public class JobFlowTaskController {

    @Autowired
    private JobFlowTaskService taskService;
    @Autowired
    private DataroadProperties dataroadProperties;

    @GetMapping("/api/dataroadjar/check")
    public ResponseStatus checkDataroadJar(){

        try{
            dataroadProperties.getDataroadDistMainJar();
        }catch (JobFlowException ex){
            log.error("",ex);
            return ResponseStatus.error("不存在的dataroad Jar,请检查dataroad-dist配置是否正确");
        }

        try{
            JobJarsReponse jobJarsReponse = taskService.listDataroadJar();
            List<JobJarsReponse.FileJarInfo> dataroadJars = jobJarsReponse.findDataroadJars();
            if(CollectionUtils.isNotEmpty(dataroadJars)){
                return ResponseStatus.success("服务器上已存在dataroad Jar,是否重新上传？");
            }
        }catch (Exception ex){
            log.error("",ex);
            return ResponseStatus.error("校验dataroad Jar失败");
        }
        return ResponseStatus.success("确定要上传Jar？");
    }
    @PostMapping("/api/dataroadjar/upload")
    public ResponseStatus uploadDataroadJar(){

        JobUploadReponse jobUploadReponse ;
        try {
            jobUploadReponse = taskService.uploadDataroadJar();
        } catch (IOException e) {
            log.error("upload dataroad jar error",e);
            ResponseStatus.success("上传Jar包失败" );
        }

        return ResponseStatus.success("上传Jar包成功" );
    }


    @PostMapping("/api/jobflowtask/submit")
    public ResponseStatus submitJobFlow(JobRequestParam jobRequestParam){

        try {
            taskService.submitJobFlow(jobRequestParam);

        } catch (JobException e) {
            log.error("submit job exception", e);
            return ResponseStatus.error(e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("submit job exception", e);
            return ResponseStatus.error("Job解析异常");
        } catch (Exception e) {
            log.error("submit job exception", e);
            return ResponseStatus.error("任务提交异常");
        }

        return ResponseStatus.success("提交成功");
    }

    @PostMapping("/api/jobflowtask/{id}/schedule")
    public ResponseStatus scheduleJobFlowTask(@PathVariable("id") String id){
        return null;
    }


}
