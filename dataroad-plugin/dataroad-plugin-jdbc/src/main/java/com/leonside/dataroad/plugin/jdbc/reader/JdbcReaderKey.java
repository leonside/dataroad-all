package com.leonside.dataroad.plugin.jdbc.reader;

import com.leonside.dataroad.common.constant.ConfigKey;

/**
 * Configuration Keys for JdbcDataReader
 */
public enum JdbcReaderKey implements ConfigKey {

    KEY_JDBC_URL("jdbcUrl",true,"", "JdbcUrl"),
    KEY_SPLIK_KEY("splitPk",false,"", "分片键"),
    KEY_USER_NAME("username",true,"", "用户名"),
    KEY_PASSWORD("password",true,"", "密码"),
    KEY_WHERE("where",false,"", "where条件"),
    KEY_FETCH_SIZE("fetchSize",false,"0", "fetchSize"),
    KEY_QUERY_TIME_OUT("queryTimeOut",false,"0", "查询超时时间"),
    KEY_REQUEST_ACCUMULATOR_INTERVAL("requestAccumulatorInterval",false,"", "Accumulator间隔时间"),
    KEY_INCRE_COLUMN("increColumn",false,"", "增量字段"),
    KEY_START_LOCATION("startLocation",false,"", "开始值"),
    KEY_CUSTOM_SQL("customSql",false,"", "自定义SQL"),
    KEY_ORDER_BY_COLUMN("orderByColumn",false,"", "排序列名"),
    KEY_USE_MAX_FUNC("useMaxFunc",false,"", "用于标记是否保存endLocation位置的一条或多条数据，true：不保存，false(默认)：保存， 某些情况下可能出现最后几条数据被重复记录的情况，可以将此参数配置为true"),
    KEY_POLLING("polling",false,"", "是否轮询"),
    KEY_POLLING_INTERVAL("pollingInterval",false,"", "轮询间隔"),
    KEY_TABLE("table",false,"", "表名"),
    KEY_COLUMN("column",false,"", "列名");

    private String name;
    private String desc;
    private boolean required;
    private String defaultValue;

    JdbcReaderKey(String name,boolean required,String defaultValue, String desc) {
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
