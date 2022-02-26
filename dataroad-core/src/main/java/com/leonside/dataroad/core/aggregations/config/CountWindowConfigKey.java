package com.leonside.dataroad.core.aggregations.config;

import com.leonside.dataroad.common.config.ConfigKey;
import com.leonside.dataroad.common.enums.FieldType;

/**
 *
 * @author leon
 */
public enum CountWindowConfigKey implements ConfigKey {

    KEY_KEYBY("keyBy","分组字段",false,"", "数组类型，支持多个字段进行分组，例如：[\"name\",\"sfzh\"]",FieldType.OBJECT),
    KEY_AGG("agg","聚合字段",true,"", "聚合字段、聚合类型映射关系，例如：{\"age\": [\"stats\"],\"score\": [\"max\"]},其中支持AVG、SUM、COUNT、MAX、MIN、STATS聚合类型",FieldType.OBJECT),
    KEY_WINDOWSIZE("windowSize","窗口大小",true,"", "计数窗口大小",FieldType.NUMBER),
    ;

    private String name;
    private String cnName;
    private String desc;
    private boolean required;
    private String defaultValue;
    public FieldType fieldType;

    CountWindowConfigKey(String name,String cnName,boolean required,String defaultValue, String desc, FieldType fieldType) {
        this.name = name;
        this.cnName = cnName;
        this.desc = desc;
        this.required = required;
        this.defaultValue = defaultValue;
        this.fieldType = fieldType;
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

    @Override
    public String getCnName() {
        return cnName;
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

    @Override
    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }
}
