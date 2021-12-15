package com.leonside.dataroad.common.utils;

import com.leonside.dataroad.common.constant.ConfigKey;
import com.leonside.dataroad.common.exception.JobConfigException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.enums.EnumUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author leon
 */
public class ConfigBeanUtils {

    public Logger logger = LoggerFactory.getLogger(ConfigBeanUtils.class);

    public static void copyConfig(Object configBean, Map<String,Object> parameter, Class<? extends Enum> configKeyClazz)  {

        Field[] declaredFields = getFields(configBean.getClass());

        Enum[] enumList = configKeyClazz.getEnumConstants();

        for (int i = 0; i < enumList.length; i++) {

            Object next = enumList[i];

            String name = ((ConfigKey)next).getName();

            Optional<Field> field = Arrays.stream(declaredFields).filter(it -> it.getName().equals(name)).findFirst();

            field.ifPresent(it->{
                it.setAccessible(true);
                Object value = getParameterValue(parameter, (ConfigKey)next, it.getType());
                try {
                    if(value != null){
                        it.set(configBean, value);
                    }
                } catch (IllegalAccessException e) {
                    throw new JobConfigException("配置属性拷贝异常",e);
                }
            });

        }

    }

    private static Object getParameterValue(Map<String, Object> parameter, ConfigKey configKey, Class<?> type) {
        if(type == int.class || type == Integer.class){
            return ParameterUtils.getInteger(parameter, configKey);
        }else if(type == boolean.class || type == Boolean.class){
            return ParameterUtils.getBoolean(parameter, configKey);
        }else if(type == String.class ){
            return ParameterUtils.getString(parameter, configKey);
        }else if(List.class.isAssignableFrom(type) ){
            return ParameterUtils.getArrayList(parameter, configKey);
        }else if(type == String[].class ){
            return ParameterUtils.getArrayList(parameter, configKey).toArray(new String[]{});
        }else if(Map.class.isAssignableFrom(type) ){
            Object value = parameter.get(configKey.getName());
            if(value == null && configKey.isRequired()) {
                throw new JobConfigException(configKey.getName() + " must not be null");
            }
            return value;
        }else{
            throw new UnsupportedOperationException("unsupport field type["+ type +"] for " + configKey.getName());
        }
    }

    public static Field[] getFields(Class<?> beanClass) throws SecurityException {
        return getFieldsDirectly(beanClass, true);
    }

    public static Field[] getFieldsDirectly(Class<?> beanClass, boolean withSuperClassFieds) throws SecurityException {
        Field[] allFields = null;

        for(Class searchType = beanClass; searchType != null; searchType = withSuperClassFieds ? searchType.getSuperclass() : null) {
            Field[] declaredFields = searchType.getDeclaredFields();
            if (null == allFields) {
                allFields = declaredFields;
            } else {
                allFields = (Field[]) ArrayUtils.addAll(allFields, declaredFields);
            }
        }

        return allFields;
    }


}
