package com.leonside.dataroad.dashboard.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.leonside.dataroad.common.context.JobSetting;
import com.leonside.dataroad.common.utils.JsonUtil;
import com.leonside.dataroad.config.domain.JobConfigs;
import com.leonside.dataroad.dashboard.domian.JobFlowDesigner;
import com.leonside.dataroad.dashboard.domian.JobFlowSchemas;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @author leon
 */
public class JobFlowConverter {

    private String designerJson;

    private String globalSetting;

    public JobFlowConverter(String designerJson, String globalSetting) {
        this.designerJson = designerJson;
        this.globalSetting = globalSetting;
    }

    public String convert(){

        JobFlowDesigner jobFlowDesigner = paserJSON();

        JobFlowSchemas jobConfigs = toJobConfigs(jobFlowDesigner);

        return JsonUtil.getInstance().writeJson(jobConfigs);
    }

    private JobFlowDesigner paserJSON() {

        JobFlowDesigner jobFlowDesigner = JsonUtil.getInstance().readJson(designerJson, JobFlowDesigner.class);

        if(StringUtils.isNotEmpty(globalSetting)){
            Map jobSetting = JsonUtil.getInstance().readJson(globalSetting, Map.class);
            jobFlowDesigner.setGlobalSettting(jobSetting);
        }


        return jobFlowDesigner;
    }


    private JobFlowSchemas toJobConfigs(JobFlowDesigner jobFlowDesigner){
        JobFlowDesignerWrapper jobFlowDesignerWrapper = new JobFlowDesignerWrapper(jobFlowDesigner);
        return jobFlowDesignerWrapper.toJobConfigs();
    }


}
