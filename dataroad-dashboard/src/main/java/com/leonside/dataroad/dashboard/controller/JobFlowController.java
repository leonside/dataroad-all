package com.leonside.dataroad.dashboard.controller;

import com.leonside.dataroad.common.utils.DateUtil;
import com.leonside.dataroad.common.utils.JsonUtil;
import com.leonside.dataroad.dashboard.converter.JobFlowConverter;
import com.leonside.dataroad.dashboard.domian.JobFlowConfig;
import com.leonside.dataroad.dashboard.domian.ResponseStatus;
import com.leonside.dataroad.dashboard.service.JobFlowService;
import com.leonside.dataroad.dashboard.utils.PageUtils;
import com.leonside.dataroad.dashboard.utils.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author leon
 */
@Slf4j
@RestController
public class JobFlowController {

    private JsonUtil jsonUtil = JsonUtil.getInstance();

    @Autowired
    private JobFlowService jobFlowService;

    @DeleteMapping("/api/jobflowconfig/{id}")
    public ResponseEntity<ResponseStatus> deleteJobFlowConfig(@PathVariable("id") String id) {

        try {
            if(!jobFlowService.containJobFlow(id)){
                return new ResponseEntity("不存在的流程", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            jobFlowService.deleteJobFlow(id);;
        } catch (Exception e) {
            log.error("ioException",e);
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(ResponseStatus.success(), HttpStatus.OK);

    }

//    @PutMapping("/api/jobflowconfig")
//    public ResponseEntity<ResponseStatus> updateJobFlowConfig(@RequestBody JobFlowConfig jobFlowConfig) {
//
//        try {
//            jobFlowService.updateJobFlow(jobFlowConfig);
//        } catch (IOException e) {
//            log.error("ioException",e);
//            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity(ResponseStatus.success(), HttpStatus.OK);
//    }

    @PostMapping("/api/jobflowjson")
    public ResponseEntity<ResponseStatus> updateJobFlowJson(@RequestBody JobFlowConfig jobFlowConfig) {

        try {
            JobFlowConfig loadConfig = jobFlowService.loadJobFlowConfig(jobFlowConfig.getId());
            loadConfig.setDesignerJson(jobFlowConfig.getDesignerJson());
            jobFlowService.updateJobFlow(loadConfig);
        } catch (Exception e) {
            log.error("ioException",e);
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(ResponseStatus.success(), HttpStatus.OK);
    }

    @PutMapping("/api/jobflowconfig")
    public ResponseEntity<ResponseStatus> updateJobFlowConfig(@RequestBody JobFlowConfig jobFlowConfig) {

        try {
             jobFlowService.updateJobFlow(jobFlowConfig);
        } catch (Exception e) {
            log.error("ioException",e);
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(ResponseStatus.success(), HttpStatus.OK);
    }

    @PutMapping("/api/jobflowbaseconfig")
    public ResponseEntity<ResponseStatus> updateJobFlowBaseConfig(@RequestBody JobFlowConfig jobFlowConfig) {

        try {
            jobFlowConfig.checkGlobalSetting();

            jobFlowService.updateBaseJobFlow(jobFlowConfig);
        } catch (Exception e) {
            log.error("ioException",e);
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(ResponseStatus.success(), HttpStatus.OK);
    }

    @PostMapping("/api/jobflowbaseconfig")
    public ResponseEntity<ResponseStatus> saveJobFlowBaseConfig(@RequestBody JobFlowConfig jobFlowConfig) {

        try {
            if(jobFlowService.containJobFlow(jobFlowConfig.getId())){
                return new ResponseEntity("存在重复的流程ID", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            jobFlowConfig.checkGlobalSetting();

            jobFlowConfig.setCreateTime(DateUtil.dateToDateTimeString(new Date()));
            jobFlowService.saveJobFlow(jobFlowConfig);
        } catch (Exception e) {
            log.error("ioException",e);
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(ResponseStatus.success(), HttpStatus.OK);
    }

    @PostMapping("/api/jobflowconfig")
    public ResponseEntity<ResponseStatus> saveJobFlowConfig(@RequestBody JobFlowConfig jobFlowConfig) {

        try {
            if(jobFlowService.containJobFlow(jobFlowConfig.getId())){
                return new ResponseEntity("存在重复的流程ID", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            jobFlowConfig.setCreateTime(DateUtil.dateToDateTimeString(new Date()));
            jobFlowService.saveJobFlow(jobFlowConfig);
        } catch (Exception e) {
            log.error("ioException",e);
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(ResponseStatus.success(), HttpStatus.OK);
    }

    @GetMapping("/api/jobflowconfig")
    public ResponseStatus<List<JobFlowConfig>> jobflowConfig(@RequestParam("page")int page, @RequestParam("isComplete")boolean isComplete, @RequestParam("limit")int limit, HttpServletRequest request) {

        List<JobFlowConfig> jobFlowConfigs = new ArrayList<>();
        try {
            if(isComplete){
                jobFlowConfigs.addAll(jobFlowService.loadJobFlowConfig().stream().filter(jobFlowConfig -> jobFlowConfig.isStatus()).collect(Collectors.toList()));
            }else{
                jobFlowConfigs.addAll(jobFlowService.loadJobFlowConfig());
            }
            Collections.reverse(jobFlowConfigs);
            List<JobFlowConfig> subList = PageUtils.subList(jobFlowConfigs, page, limit);
            return ResponseStatus.success("加载完成", jobFlowConfigs.size(), subList);
        } catch (Exception e) {
            log.error("ioException",e);
            return ResponseStatus.error(e.getMessage());
        }

    }

    @GetMapping("/api/jobflowconfig/{id}")
    public JobFlowConfig jobflowConfig(@PathVariable("id") String id) {

        JobFlowConfig jobFlowConfig = jobFlowService.loadJobFlowConfig(id);

        return jobFlowConfig;
    }

    @PostMapping("/api/showjobflowjson")
    public ResponseStatus showjobflowjson(@RequestBody JobFlowConfig jsonData ) {

        String designerJson = jsonData.getDesignerJson();
        JobFlowConfig jobFlowConfig = jobFlowService.loadJobFlowConfig(jsonData.getId());

        JobFlowConverter jobFlowConverter = jobFlowConfig == null ?
                new JobFlowConverter(designerJson,null)
                : new JobFlowConverter(designerJson, jobFlowConfig.getGolbalSetting());

        String jobFlowJson = jobFlowConverter.convert();

        return ResponseStatus.success(jsonUtil.prettyJson(jsonUtil.readJson(jobFlowJson, Map.class)));
    }

    @GetMapping("/api/jobflowjson/{id}")
    public String loadJobFlowJson(@PathVariable("id") String id) {

        String jobFlowJson = jobFlowService.loadJobFlowJson(id);

        return jsonUtil.prettyJson(jsonUtil.readJson(jobFlowJson, Map.class));
    }

    @GetMapping("/api/jobflowjson/download")
    public void loadJobFlowJsons(@RequestParam("ids") String[] ids, HttpServletResponse response) throws IOException {

        List<String> fileNames = Arrays.stream(ids).map(id -> id + ".json").collect(Collectors.toList());

        String donwloadName = "dataroad-"+ DateUtil.dateToStoreDateTimeString(new Date()) + ".zip";
        File zip = ZipUtils.zip(fileNames.toArray(new String[0]), donwloadName);

        OutputStream os = response.getOutputStream();
        try {
            response.reset();
            response.setHeader("Cache-Control", "private");
            response.setHeader("Pragma", "private");
            response.setContentType("application/x-download;charset=utf-8");
            response.setHeader("Content-disposition", "attachment; filename="+donwloadName);
            os.write(FileUtils.readFileToByteArray(zip));
            os.flush();

        } finally {
            if (os != null) {
                os.flush();
                os.close();
            }
        }

        zip.delete();
    }

}
