package com.leonside.dataroad.core.component;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.config.Validation;
import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.utils.ConfigBeanUtils;
import com.leonside.dataroad.common.utils.ParameterizedTypeUtils;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author leon
 */
public interface ComponentInitialization<T extends ExecuteContext, C extends BaseConfig> extends Validation {

    default void initialize(T executeContext, Map<String, Object> parameter){

        C config = ConfigBeanUtils.newInstance(configClass(), parameter);

        if(config.bindConfigKey() != null){
            ConfigBeanUtils.copyConfig(config, parameter, config.bindConfigKey());
        }

        config.validate();

        doInitialize(executeContext, config);
    }

    void doInitialize(T executeContext, C config);

    default Class<? extends BaseConfig> configClass(){
        Class classGenericType = ParameterizedTypeUtils.getClassGenericType(this.getClass(), ComponentInitialization.class, 1);

        if(BaseConfig.class.isAssignableFrom(classGenericType) ){
            return (Class<? extends BaseConfig>) classGenericType;
        }else{
            return BaseConfig.class;
        }
    }
}
