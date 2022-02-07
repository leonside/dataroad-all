package com.leonside.dataroad.dashboard.domian;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.List;

/**
 * @author leon
 */
@Getter
@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JobFlowConfigs {

    private List<JobFlowConfig> jobFlowConfigs;

    public static JobFlowConfigs of(List<JobFlowConfig> jobFlowConfigs){
        JobFlowConfigs newconfigs = new JobFlowConfigs();
        newconfigs.setJobFlowConfigs(jobFlowConfigs);
        return newconfigs;
    }

}
