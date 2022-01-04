package com.leonside.dataroad.flink.processor.lookup.config;

import com.leonside.dataroad.common.constant.ConfigKey;

/**
 * @author leon
 */
public enum BaseLookupConfigKey implements ConfigKey {


    KEY_DIRECT_DATA("directData",true,"", "表码数据"),
    KEY_JOIN_COLUMNS("joinColumns",true,"", "Join字段"),
    KEY_COLUMNS("columns",true,"", "列名");

    public String name;
    public String desc;
    public boolean required;
    public String defaultValue;

    BaseLookupConfigKey(String name,boolean required,String defaultValue, String desc) {
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
