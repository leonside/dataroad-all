package com.leonside.dataroad.dashboard.domian;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leonside.dataroad.common.context.JobSetting;
import com.leonside.dataroad.common.utils.JsonUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author leon
 */
@Getter
@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JobFlowConfig {
    private String id;
    private String description;
    private String golbalSetting;
    private boolean status;
    private String createTime ;
    private String designerJson;

    @JsonIgnore
    @XmlTransient
    private String jobflowJson;

    public boolean checkGlobalSetting(){
        if(StringUtils.isNotEmpty(golbalSetting)){
            JobSetting jobSetting = JsonUtil.getInstance().readJson(golbalSetting, JobSetting.class);
            return jobSetting.validate();
        }
        return true;

    }

    public void copy(JobFlowConfig jobFlowConfig){
        this.description = jobFlowConfig.getDescription();
        if(!this.status){
            this.status = jobFlowConfig.isStatus();
        }
        this.golbalSetting = jobFlowConfig.getGolbalSetting();
        if(StringUtils.isNotEmpty(jobFlowConfig.getCreateTime())){
            this.createTime = jobFlowConfig.getCreateTime();
        }
        if(StringUtils.isNotEmpty(jobFlowConfig.getDesignerJson())){
            this.designerJson = jobFlowConfig.getDesignerJson();
        }
        if(StringUtils.isNotEmpty(jobFlowConfig.getJobflowJson())){
            this.jobflowJson = jobFlowConfig.getJobflowJson();
        }

    }

}
