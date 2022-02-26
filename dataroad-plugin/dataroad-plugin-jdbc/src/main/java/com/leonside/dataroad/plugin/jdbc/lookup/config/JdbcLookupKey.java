package com.leonside.dataroad.plugin.jdbc.lookup.config;

import com.leonside.dataroad.common.config.ConfigKey;
import com.leonside.dataroad.common.enums.FieldType;

/**
 * @author leon
 */
public enum JdbcLookupKey implements ConfigKey {
    KEY_CACHE_TYPE("cacheType","缓存类型",true,"", "缓存类型，包含all、lru、none",FieldType.ENUM),
    KEY_CACHE_MAXROWS("cacheMaxrows","缓存最大记录数",false,"10000", "缓存最大记录数",FieldType.NUMBER),
    KEY_CACHE_TTL("cacheTtl","缓存过期时间",false,"60000", "缓存过期时间",FieldType.NUMBER),

    KEY_JDBCURL("jdbcUrl","jdbcURL",true,"", "JDBCURL",FieldType.STRING),
    KEY_USERNAME("username","用户名",true,"", "用户名",FieldType.STRING),
    KEY_PASSWORD("password","密码",true,"", "密码",FieldType.STRING),
    KEY_SCHEMA("schema","schema",false,"", "schema",FieldType.STRING),
    KEY_TABLE("table","表名",false,"", "支持配置表名方式和配置自定义SQL两种方式，当采用表名配置方式此处必填",FieldType.STRING),
    KEY_COLUMNS("columns","列名",false,"", "当采用表名配置方式则此处必填，对应维表数据集，例如：[\"code\",\"value\"]\"",FieldType.OBJECT),
    KEY_WHERE("where","where条件",false,"", "当采用表名配置方式",FieldType.STRING),
    KEY_CUSTOMSQL("customSql","自定义SQL",false,"", "当采用自定义SQL配置方式，此处必填",FieldType.STRING),
    KEY_JOIN_COLUMNS("joinColumns","Join字段",true,"", "事实表和维表的Join字段映射，例如：{\"sex\": \"code\"}",FieldType.OBJECT),

    KEY_FETCHSIZE("fetchSize","分批获取大小",false,"", "设置每次查询按fetchSize分批获取",FieldType.NUMBER),

//    KEY_PARALLELISM("parallelism","并行度",false,"1", "默认1"),
//    MAX_TASK_QUEUE_SIZE("taskQueueSize","队列大小", false, "100000", "队列大小"),
//    MAX_DB_CONN_POOL_SIZE_LIMIT("dbConnPoolSize","数据库连接池数",false,"5", "数据库连接池数"),
//    DEFAULT_VERTX_EVENT_LOOP_POOL_SIZE("eventLoopPoolSize","eventLoopPoolSize", false,"1","EventLoopPoolSize")
    ;

    private String name;
    private String cnName;
    private String desc;
    private boolean required;
    private String defaultValue;

    public FieldType fieldType;

    JdbcLookupKey(String name,String cnName,boolean required,String defaultValue, String desc, FieldType fieldType) {
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
