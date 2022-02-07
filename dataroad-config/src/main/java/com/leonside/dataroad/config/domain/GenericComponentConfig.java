package com.leonside.dataroad.config.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.leonside.dataroad.common.exception.JobConfigException;
import com.leonside.dataroad.core.component.ComponentType;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author leon
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericComponentConfig implements Serializable {

    private ComponentType type;

    private String pluginName;

    private String[] dependencies;

    private Map<String,Object> parameter;

    @JsonIgnore
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Set<GenericComponentConfig> childs = new LinkedHashSet<>();
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonIgnore
    private String name;
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonIgnore
    private boolean deciderLastFlow = false;

    public boolean isDeciderLastFlow() {
        return deciderLastFlow;
    }

    /**
     * 查找当前分支的最后一个环节节点，并标记此状态，用于方便{@link com.leonside.dataroad.core.builder.MultiJobFlowBuilder} 处理End逻辑
     */
    public void markCurrentDeciderLastFlow(){
        if(type != ComponentType.deciderOn){
            throw new JobConfigException("Wrong process configuration, branch process needs to start with deciderOn");
        }

        GenericComponentConfig lastDeciderFlow = getDeciderLastFlow();
        lastDeciderFlow.setDeciderLastFlow(true);
    }


    /**
     * 获取最后一个分支节点
     * @return
     */
    private GenericComponentConfig getDeciderLastFlow(){
        //最后一个分支或遍历存在合并分支，则标识此节点为 分支的最后一个节点
        if(childs.size() == 0 ){
            return this;
        }else{
            GenericComponentConfig next = childs.iterator().next();
            if(next.getDependencies().length >= 2){
                return this;
            }else{
                return next.getDeciderLastFlow();
            }
        }
    }
}
