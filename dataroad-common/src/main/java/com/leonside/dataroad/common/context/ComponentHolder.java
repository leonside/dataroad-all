package com.leonside.dataroad.common.context;

import lombok.Data;

/**
 * @author leon
 */
@Data
public class ComponentHolder {

    private String type;
    private String pluginName;

    public ComponentHolder(String type, String pluginName){
        this.type = type;
        this.pluginName = pluginName;
    }
}
