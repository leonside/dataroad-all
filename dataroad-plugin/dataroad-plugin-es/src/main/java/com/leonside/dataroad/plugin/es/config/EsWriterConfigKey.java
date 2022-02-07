package com.leonside.dataroad.plugin.es.config;

import com.leonside.dataroad.common.config.ConfigKey;

/**
 * @author leon
 */
public enum EsWriterConfigKey implements ConfigKey {

    KEY_ADDRESS("address","地址",true,"", "ES地址，例如：127.0.0.1:9200"),
    KEY_USERNAME("username","用户名",false,"", "用户名,非必填"),
    KEY_PASSWORD("password","密码",false,"", "密码,非必填"),
    KEY_INDEX("index","索引名",true,"", "索引名称"),
    KEY_TYPE("indexType","Type",false,"_doc", "索引type，默认_doc"),
    KEY_COLUMN("column","列名",true,"", "索引列名集合，例如：[{\"name\": \"id\",\"type\": \"int\"},...]"),
    KEY_ID_COLUMN("idColumn","主键列",false,"", "主键列，例如：[{\"name\": \"id\",\"type\": \"int\"}]"),

    KEY_BULK_ACTION("bulkAction","批量大小",false,"100", "批量大小,默认100"),
    KEY_TIMEOUT("timeout","超时时间",false,"", "超时时间"),
    KEY_PATH_PREFIX("pathPrefix","路径前缀",false,"", "路径前缀")
        ;

    private String name;
    private String cnName;
    private String desc;
    private String defaultValue;
    private boolean required;

    public static final String KEY_COLUMN_NAME = "name";

    public static final String KEY_COLUMN_TYPE = "type";

    public static final String KEY_ID_COLUMN_NAME = "name";

    public static final String KEY_ID_COLUMN_TYPE = "type";

    public static final String KEY_ID_COLUMN_VALUE = "value";

    EsWriterConfigKey(String name, String  cnName, boolean required, String defaultValue, String desc) {
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
