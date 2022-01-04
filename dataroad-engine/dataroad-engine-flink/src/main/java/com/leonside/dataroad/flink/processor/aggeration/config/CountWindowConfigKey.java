package com.leonside.dataroad.flink.processor.aggeration.config;

import com.leonside.dataroad.common.constant.ConfigKey;
import com.leonside.dataroad.core.aggregations.AggerationEnum;

import java.util.List;
import java.util.Map;

/**
 *
 * @author leon
 */
public enum CountWindowConfigKey implements ConfigKey {

    KEY_KEYBY("keyBy",false,"", "keyBy，数组类型"),
    KEY_AGG("agg",true,"", "聚合字段及聚合类型配置，数组配置方式，如果keyBy不为空则和keyBy对应"),
    KEY_WINDOWSIZE("windowSize",true,"", "窗口大小"),
    ;

    private String name;
    private String desc;
    private boolean required;
    private String defaultValue;

    CountWindowConfigKey(String name,boolean required,String defaultValue, String desc) {
        this.name = name;
        this.desc = desc;
        this.required = required;
        this.defaultValue = defaultValue;
    }
    @Override
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
