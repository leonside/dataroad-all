package com.leonside.dataroad.dashboard.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leonside.dataroad.common.exception.JobException;
import com.leonside.dataroad.common.utils.DateUtil;
import com.leonside.dataroad.common.utils.JsonUtil;
import com.leonside.dataroad.dashboard.converter.JobFlowConverter;
import com.leonside.dataroad.dashboard.domian.JobFlowConfig;
import com.leonside.dataroad.dashboard.domian.JobRequestParam;
import com.leonside.dataroad.dashboard.domian.JobUploadReponse;
import com.leonside.dataroad.dashboard.domian.ResponseStatus;
import com.leonside.dataroad.dashboard.service.JobFlowService;
import com.leonside.dataroad.dashboard.service.JobFlowTaskService;
import com.leonside.dataroad.dashboard.utils.PageUtils;
import lombok.extern.slf4j.Slf4j;
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
        }

        return ResponseStatus.success();
    }

    @PostMapping("/api/jobflowtask/{id}/schedule")
    public ResponseStatus scheduleJobFlowTask(@PathVariable("id") String id){
        return null;
    }


}
