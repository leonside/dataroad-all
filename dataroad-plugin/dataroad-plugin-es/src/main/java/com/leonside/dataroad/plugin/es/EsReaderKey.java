package com.leonside.dataroad.plugin.es;

import com.leonside.dataroad.common.constant.ConfigKey;

import java.util.HashMap;

/**
 * @author leon
 */
public enum EsReaderKey implements ConfigKey {
    KEY_ADDRESS("address",true,"", "地址"),
    KEY_USERNAME("username",false,"", "用户名"),
    KEY_PASSWORD("password",false,"", "密码"),
    KEY_TYPE("type",false,"_doc", "type"),
    KEY_INDEX("index",true,"", "index"),
    KEY_BATCH_SIZE("batchSize",false,"10", "批量大小"),
    KEY_TIMEOUT("timeout",false,"", "超时时间"),
    KEY_PATH_PREFIX("pathPrefix",false,"", "路径前缀"),
    KEY_COLUMN("column",true,"", "列名"),
    KEY_QUERY("query",false,"", "查询条件");

    private String name;
    private String desc;
    private String defaultValue;
    private boolean required;

    EsReaderKey(String name, boolean required, String defaultValue, String desc) {
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
