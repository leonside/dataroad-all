package com.leonside.dataroad.plugin.mysqlstream.reader;

import com.leonside.dataroad.common.constant.ConfigKey;

/**
 * @author leon
 */
public enum MysqlStreamReaderKey implements ConfigKey {

    hostname("hostname",true,"", "主机"),
    port("port",true,"", "端口"),
    schema("schema",true,"", "库"),
    username("username",true,"", "用户名"),
    password("password",true,"", "密码"),
    table("table",true,"", "表名");

    private String name;
    private String desc;
    private String defaultValue;
    private boolean required;

    MysqlStreamReaderKey(String name,boolean required, String defaultValue,String desc) {
        this.name = name;
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
