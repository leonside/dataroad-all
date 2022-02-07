package com.leonside.dataroad.flink.processor.lookup.config;

import com.leonside.dataroad.common.config.ConfigKey;

/**
 * @author leon
 */
public enum BaseLookupConfigKey implements ConfigKey {


    KEY_DIRECT_DATA("directData","维表数据集",true,"", "例如：[{\"code\":\"0\", \"value\": \"男\"},{\"code\":\"1\", \"value\": \"女\"}]"),
    KEY_COLUMNS("columns","维表列名",true,"", "对应维表数据集，例如：[\"code\",\"value\"]"),
    KEY_JOIN_COLUMNS("joinColumns","Join字段",true,"", "事实表和维表的Join字段映射，例如：{\"sex\": \"code\"}");

    public String name;
    public String cnName;
    public String desc;
    public boolean required;
    public String defaultValue;

    BaseLookupConfigKey(String name,String cnName,boolean required,String defaultValue, String desc) {
        this.name = name;
        this.cnName = cnName;
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
    public String getCnName() {
        return cnName;
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
