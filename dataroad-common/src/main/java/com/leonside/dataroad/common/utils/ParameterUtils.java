package com.leonside.dataroad.common.utils;

import com.leonside.dataroad.common.constant.ConfigKey;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
public class ParameterUtils {

    public static Boolean getBoolean(Map<String,Object> params, ConfigKey configKey){
        return configKey.isRequired() ? getBoolean(params, configKey.getName())
                : getBooleanNullable(params, configKey.getName(), StringUtils.isEmpty(configKey.getDefaultValue()) ? null : Boolean.valueOf(configKey.getDefaultValue()));
    }

    public static Boolean getBooleanNullable(Map<String,Object> params, String key, Boolean defaultValue){
        if(MapUtils.isEmpty(params) || params.get(key) == null){
            return defaultValue;
        }
        return getBoolean(params, key);
    }

    public static Boolean getBoolean(Map<String,Object> params, String key){
        Asserts.notEmpty(params, key + " params must not be null, check whether the configuration is valid.");
        Asserts.notNull(params.get(key), "params key ["+ key +"] must not be null, check whether the configuration is valid.");

        return (Boolean) params.get(key);
    }

    public static Integer getInteger(Map<String,Object> params, ConfigKey configKey){
        return configKey.isRequired() ? getInteger(params, configKey.getName())
                : getIntegerNullable(params, configKey.getName(), StringUtils.isEmpty(configKey.getDefaultValue())? null : Integer.valueOf(configKey.getDefaultValue()));
    }

    public static Integer getInteger(Map<String,Object> params, String key){
        Asserts.notEmpty(params, key + " params must not be null, check whether the configuration is valid.");
        Asserts.notNull(params.get(key), "params key ["+ key +"] must not be null, check whether the configuration is valid.");

        return (Integer) params.get(key);
    }

    public static Integer getIntegerNullable(Map<String,Object> params, String key, Integer defaultValue){
        if(MapUtils.isEmpty(params) || params.get(key) == null){
            return defaultValue;
        }
        return getInteger(params, key);
    }

    public static Integer getIntegerNullable(Map<String,Object> params, String key){
        if(MapUtils.isEmpty(params) || params.get(key) == null){
            return null;
        }
        return getInteger(params, key);
    }

    public static String getString(Map<String,Object> params, ConfigKey configKey){
        return configKey.isRequired() ? getString(params, configKey.getName())
                : getStringNullable(params, configKey.getName(), configKey.getDefaultValue());
    }

    public static String getString(Map<String,Object> params, String key){
        Asserts.notEmpty(params, key + "params must not be null, check whether the configuration is valid.");
        Asserts.notNull(params.get(key), "params key ["+ key +"] must not be null, check whether the configuration is valid.");

        return (String) params.get(key);
    }


    public static String getStringNullable(Map<String,Object> params, String key){
        if(MapUtils.isEmpty(params) || params.get(key) == null){
            return null;
        }
        return getString(params, key);
    }

    public static String getStringNullable(Map<String,Object> params, String key, String defaultValue){
        if(MapUtils.isEmpty(params) || params.get(key) == null){
            return defaultValue;
        }
        return getString(params, key);
    }

    public static List<?> getArrayListNullable(Map<String, Object> parameter, String key) {
        if(MapUtils.isEmpty(parameter) || parameter.get(key) == null){
            return null;
        }
        return getArrayList(parameter, key);
    }

    public static List<?> getArrayList(Map<String,Object> params, ConfigKey configKey){
        return configKey.isRequired() ? getArrayList(params, configKey.getName())
                : getArrayListNullable(params, configKey.getName());
    }

    public static List<?> getArrayList(Map<String,Object> params, String key){
        Asserts.notEmpty(params, key + "params must not be null, check whether the configuration is valid.");
        Asserts.notNull(params.get(key), "params key ["+ key +"] must not be null, check whether the configuration is valid.");

        return (List<?>) params.get(key);
    }
}
