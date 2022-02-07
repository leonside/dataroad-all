package com.leonside.dataroad.plugin.es.config;

import com.leonside.dataroad.common.config.ConfigKey;

/**
 * @author leon
 */
public enum EsReaderConfigKey implements ConfigKey {
    KEY_ADDRESS("address", "地址",true,"", "ES地址，例如：127.0.0.1:9200"),
    KEY_USERNAME("username","用户名",false,"", "用户名,非必填"),
    KEY_PASSWORD("password","密码",false,"", "密码,非必填"),
    KEY_INDEX("index","索引名",true,"", "索引名称"),
    KEY_TYPE("indexType","Type",true,"_doc", "索引type，默认_doc"),
    KEY_COLUMN("column","列名",true,"", "索引列名集合，例如：[{\"name\": \"id\",\"type\": \"int\"},...]"),

    KEY_BATCH_SIZE("batchSize","批量大小",false,"10", "批量大小,默认10"),
    KEY_TIMEOUT("timeout","超时时间",false,"", "超时时间"),
    KEY_PATH_PREFIX("pathPrefix","路径前缀",false,"", "路径前缀"),
    KEY_QUERY("query","查询条件",false,"", "JSON格式查询条件，详见ES的相关文档");

    private String name;
    private String cnName;
    private String desc;
    private String defaultValue;
    private boolean required;

    EsReaderConfigKey(String name, String cnName, boolean required, String defaultValue, String desc) {
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
