package com.leonside.dataroad.common.config;

import com.leonside.dataroad.common.utils.ConfigBeanUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author leon
 */
@Data
public abstract class BaseConfig implements  ConfigKeyBinder, Validation, Serializable {

    private Map<String,Object> parameter;

    public BaseConfig(Map<String,Object> parameter){
        this.parameter = parameter;
    }
}
