package com.leonside.dataroad.plugin.mysqlstream.config;

import com.leonside.dataroad.common.config.ConfigKey;

/**
 * @author leon
 */
public enum MysqlStreamReaderConfigKey implements ConfigKey {

    hostname("hostname","IP地址",true,"", "IP地址"),
    port("port","端口",true,"", "端口"),
    schema("schema","schema",true,"", "库"),
    table("table","表名",true,"", "表名"),
    username("username","用户名",true,"", "用户名"),
    password("password","密码",true,"", "密码"),

    ;

    private String name;
    private String cnName;
    private String desc;
    private String defaultValue;
    private boolean required;

    MysqlStreamReaderConfigKey(String name, String cnName,boolean required, String defaultValue, String desc) {
        this.name = name;
        this.cnName = cnName;
        this.defaultValue =defaultValue;
        this.desc = desc;
        this.required = required;
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
}
