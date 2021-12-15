package com.leonside.dataroad.plugin.jdbc.writer;

import com.leonside.dataroad.common.constant.ConfigKey;

/**
 * Configuration Keys for JdbcDataWriter
 *
 */
public enum JdbcWriterKey implements ConfigKey {

    KEY_JDBC_URL("jdbcUrl",true, "","JdbcUrl"),
    KEY_TABLE("table",true,"", "表名"),
    KEY_WRITE_MODE("writeMode",false,"", "写入模式"),
    KEY_USERNAME("username",true,"", "用户名"),
    KEY_PASSWORD("password",true,"", "密码"),
    KEY_PRE_SQL("preSql",false,"", "前置SQL"),
    KEY_POST_SQL("postSql",false,"", "后置SQL"),
    KEY_BATCH_SIZE("batchSize",false,"1024", "批量写入时间"),
    KEY_COLUMN("column",false,"", "列名"),
    KEY_INSERT_SQL_MODE("insertSqlMode",false,"", "插入模式");

//    public static final String KEY_FULL_COLUMN = "fullColumn";
//    public static final String KEY_PROPERTIES = "properties";

    private String name;
    private String desc;
    private boolean required;
    private String defaultValue;

    JdbcWriterKey(String name,boolean required, String defaultValue, String desc) {
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
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
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

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
