package com.leonside.dataroad.plugin.mysqlstream.config;

import com.leonside.dataroad.common.config.ConfigKey;
import com.leonside.dataroad.common.enums.FieldType;

/**
 * @author leon
 */
public enum MysqlStreamReaderConfigKey implements ConfigKey {

    hostname("hostname","IP地址",true,"", "IP地址",FieldType.STRING),
    port("port","端口",true,"", "端口",FieldType.STRING),
    schema("schema","schema",true,"", "库",FieldType.STRING),
    table("table","表名",true,"", "表名",FieldType.STRING),
    username("username","用户名",true,"", "用户名", FieldType.STRING),
    password("password","密码",true,"", "密码",FieldType.STRING),

    ;

    private String name;
    private String cnName;
    private String desc;
    private String defaultValue;
    private boolean required;
    public FieldType fieldType;
    MysqlStreamReaderConfigKey(String name, String cnName,boolean required, String defaultValue, String desc, FieldType fieldType) {
        this.name = name;
        this.cnName = cnName;
        this.defaultValue =defaultValue;
        this.desc = desc;
        this.required = required;
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

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    @Override
    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
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
