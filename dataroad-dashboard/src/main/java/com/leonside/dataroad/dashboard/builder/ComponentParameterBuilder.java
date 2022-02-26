package com.leonside.dataroad.dashboard.builder;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.config.ConfigKey;
import com.leonside.dataroad.common.enums.FieldType;
import com.leonside.dataroad.common.utils.ConfigBeanUtils;
import com.leonside.dataroad.common.utils.EnumUtils;
import com.leonside.dataroad.dashboard.domian.ComponentParameter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author leon
 */
public class ComponentParameterBuilder {

    private Class<? extends BaseConfig> configClass;

    public ComponentParameterBuilder configClass(Class<? extends BaseConfig> configClass){
        this.configClass = configClass;
        return this;
    }

    public List<ComponentParameter> build() {

        BaseConfig baseConfig = ConfigBeanUtils.newInstance(configClass, null);

        Class configKey = baseConfig.bindConfigKey();

        ConfigKey[] enumConstants = (ConfigKey[]) configKey.getEnumConstants();

        return Arrays.stream(enumConstants).map(item -> {
            ComponentParameter componentParameter = new ComponentParameter();
            componentParameter.setName(item.getName());
            componentParameter.setCnName(item.getCnName());
            componentParameter.setDesc(item.getDesc());
            componentParameter.setRequired(item.isRequired());
            componentParameter.setDefaultValue(item.getDefaultValue());
            componentParameter.setFieldType(item.getFieldType().name());

            Optional<Field> matchField = ConfigBeanUtils.getField(configClass, item.getName());

            if (matchField.isPresent()) {
                if (matchField.get().getType().isEnum()) {
                    componentParameter.setFieldType(FieldType.ENUM.name());
                    String[] enumNameArray = EnumUtils.getEnumNameArray((Class<? extends Enum>) matchField.get().getType());
                    componentParameter.setFieldEnumList(enumNameArray);
//                } else {
//                    if(Map.class.isAssignableFrom(matchField.get().getType())
//                            || List.class.isAssignableFrom(matchField.get().getType())
//                            || String[].class.isAssignableFrom(matchField.get().getType())){
//                        componentParameter.setFieldType("object");
//                    }else{
//                        componentParameter.setFieldType(matchField.get().getType().getSimpleName());
//                    }
                }
            }
            return componentParameter;
        }).collect(Collectors.toList());
    }

    public static ComponentParameterBuilder builder(){
        return new ComponentParameterBuilder();
    }

}
