package com.leonside.dataroad.common.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @author leon
 */
@Data
public class ComponentHolder implements Serializable {

    private String type;
    private String pluginName;

    public ComponentHolder(String type, String pluginName){
        this.type = type;
        this.pluginName = pluginName;
    }
}
