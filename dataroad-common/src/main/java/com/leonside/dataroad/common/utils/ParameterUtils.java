package com.leonside.dataroad.common.utils;

import com.leonside.dataroad.common.constant.ConfigKey;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.enums.EnumUtils;

import java.util.Iterator;
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

    public static Long getLong(Map<String, Object> parameter, ConfigKey configKey) {
        return configKey.isRequired() ? getLong(parameter, configKey.getName())
                : getLongNullable(parameter, configKey.getName(), StringUtils.isEmpty(configKey.getDefaultValue())? null : Long.valueOf(configKey.getDefaultValue()));
    }

    public static Long getLong(Map<String,Object> params, String key){
        Asserts.notEmpty(params, key + " params must not be null, check whether the configuration is valid.");
        Asserts.notNull(params.get(key), "params key ["+ key +"] must not be null, check whether the configuration is valid.");

        return Long.valueOf(params.get(key).toString()) ;
    }

    public static Long getLongNullable(Map<String,Object> params, String key, Long defaultValue){
        if(MapUtils.isEmpty(params) || params.get(key) == null){
            return defaultValue;
        }
        return getLong(params, key);
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

    public static Enum<?> getEnum(Map<String,Object> params, ConfigKey configKey, Class<?> clazz){
        String enumName = ParameterUtils.getString(params, configKey);
        Enum<?> enumClazz = null;
        if(StringUtils.isEmpty(enumName)){
            return enumClazz;
        }else{
            Object[] enumConstants = clazz.getEnumConstants();
            for (int i = 0; i < enumConstants.length; i++) {
                Object next = enumConstants[i];
                if(next.toString().equalsIgnoreCase(enumName)){
                    enumClazz = (Enum<?>) next;
                    break;
                }
            }
            return enumClazz;
        }
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

    public static String[] getStringArray(Map<String,Object> params, ConfigKey configKey){
        List<?> arrayList = getArrayList(params, configKey);
        if(configKey.isRequired() && CollectionUtils.isEmpty(arrayList)) {
            throw new IllegalArgumentException("params["+configKey.getName()+"] must not be null, check whether the configuration is valid.");
        }
        return configKey.isRequired() ? arrayList.toArray(new String[]{})
                : (CollectionUtils.isEmpty(arrayList) ? null : arrayList.toArray(new String[]{}));
    }

    public static List<?> getArrayList(Map<String,Object> params, String key){
        Asserts.notEmpty(params, key + "params must not be null, check whether the configuration is valid.");
        Asserts.notNull(params.get(key), "params key ["+ key +"] must not be null, check whether the configuration is valid.");

        return (List<?>) params.get(key);
    }


}
