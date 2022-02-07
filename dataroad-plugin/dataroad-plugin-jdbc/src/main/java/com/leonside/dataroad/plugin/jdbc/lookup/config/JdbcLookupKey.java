package com.leonside.dataroad.plugin.jdbc.lookup.config;

import com.leonside.dataroad.common.config.ConfigKey;

/**
 * @author leon
 */
public enum JdbcLookupKey implements ConfigKey {
    KEY_CACHE_TYPE("cacheType","缓存类型",true,"", "缓存类型，包含all、lru、none"),
    KEY_CACHE_MAXROWS("cacheMaxrows","缓存最大记录数",false,"10000", "缓存最大记录数"),
    KEY_CACHE_TTL("cacheTtl","缓存过期时间",false,"60000", "缓存过期时间"),

    KEY_JDBCURL("jdbcUrl","jdbcURL",true,"", "JDBCURL"),
    KEY_USERNAME("username","用户名",true,"", "用户名"),
    KEY_PASSWORD("password","密码",true,"", "密码"),
    KEY_SCHEMA("schema","schema",false,"", "schema"),
    KEY_TABLE("table","表名",false,"", "支持配置表名方式和配置自定义SQL两种方式，当采用表名配置方式此处必填"),
    KEY_COLUMNS("columns","列名",false,"", "当采用表名配置方式则此处必填，对应维表数据集，例如：[\"code\",\"value\"]\""),
    KEY_WHERE("where","where条件",false,"", "当采用表名配置方式"),
    KEY_CUSTOMSQL("customSql","自定义SQL",false,"", "当采用自定义SQL配置方式，此处必填"),
    KEY_JOIN_COLUMNS("joinColumns","Join字段",true,"", "事实表和维表的Join字段映射，例如：{\"sex\": \"code\"}"),

    KEY_FETCHSIZE("fetchSize","分批获取大小",false,"", "设置每次查询按fetchSize分批获取"),

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

    JdbcLookupKey(String name,String cnName,boolean required,String defaultValue, String desc) {
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
