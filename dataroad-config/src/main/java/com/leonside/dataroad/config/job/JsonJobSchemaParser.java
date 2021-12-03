package com.leonside.dataroad.config.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leonside.dataroad.common.constant.JobCommonConstant;
import com.leonside.dataroad.common.constant.JobConfigKeyConstants;
import com.leonside.dataroad.common.utils.JsonUtil;
import com.leonside.dataroad.config.JobSchemaParser;
import com.leonside.dataroad.config.domain.JobConfigs;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author leon
 */
public class JsonJobSchemaParser implements JobSchemaParser {

    @Override
    public JobConfigs parserJSON(String json) throws JsonProcessingException {
        JobConfigs job = JsonUtil.getInstance().readJson(json, JobConfigs.class);
        return job;
    }

    @Override
    public JobConfigs parserJSONPath(String path) throws IOException, URISyntaxException {
        JobConfigs job = null;
        if(path.startsWith(JobCommonConstant.JOBSCHEMA_PATH_PREFIX_CLASSPATH)) {
            path = path.replaceAll(JobCommonConstant.JOBSCHEMA_PATH_PREFIX_CLASSPATH,"");
            URL resource = JsonJobSchemaParser.class.getResource(path);
            job = JsonUtil.getInstance().readJson(new File(resource.toURI()), JobConfigs.class);
        }else if(path.startsWith(JobCommonConstant.JOBSCHEMA_PATH_PREFIX_FILESYSTEM)){
            path = path.replaceAll(JobCommonConstant.JOBSCHEMA_PATH_PREFIX_FILESYSTEM,"");
            job = JsonUtil.getInstance().readJson(new File(path), JobConfigs.class);
        }else{
            throw new UnsupportedOperationException("不支持的文件路径："+ path);
        }
        return job;
    }

}
