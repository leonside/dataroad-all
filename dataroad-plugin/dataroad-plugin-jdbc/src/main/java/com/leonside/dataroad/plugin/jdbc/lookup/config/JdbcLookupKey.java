package com.leonside.dataroad.plugin.jdbc.lookup.config;

import com.leonside.dataroad.common.constant.ConfigKey;

/**
 * @author leon
 */
public enum JdbcLookupKey implements ConfigKey {
    KEY_CACHE_MAXROWS("cacheMaxrows",false,"10000", "缓存最大记录数"),
    KEY_CACHE_TTL("cacheTtl",false,"60000", "缓存过期时间"),
    KEY_CACHE_TYPE("cacheType",true,"", "缓存类型，包含All、LUR"),

    KEY_JOIN_COLUMNS("joinColumns",true,"", "Join字段"),
    KEY_COLUMNS("columns",false,"", "列名"),
    KEY_USERNAME("username",true,"", "用户名"),
    KEY_PASSWORD("password",true,"", "密码"),
    KEY_JDBCURL("jdbcUrl",true,"", "JDBCURL"),
    KEY_FETCHSIZE("fetchSize",false,"", "fetchSize"),
    KEY_SCHEMA("schema",false,"", "schema"),
    KEY_TABLE("table",false,"", "表"),
    KEY_WHERE("where",false,"", "where条件"),
    KEY_CUSTOMSQL("customSql",false,"", "自定义SQL"),
    KEY_PARALLELISM("parallelism",false,"", "并行度"),
    //默认1
    MAX_TASK_QUEUE_SIZE("taskQueueSize", false, "100000", "队列大小"),
    MAX_DB_CONN_POOL_SIZE_LIMIT("dbConnPoolSize",false,"5", "数据库连接池数"),
    DEFAULT_VERTX_EVENT_LOOP_POOL_SIZE("eventLoopPoolSize", false,"1","EventLoopPoolSize")
    ;

    private String name;
    private String desc;
    private boolean required;
    private String defaultValue;

    JdbcLookupKey(String name,boolean required,String defaultValue, String desc) {
        this.name = name;
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
