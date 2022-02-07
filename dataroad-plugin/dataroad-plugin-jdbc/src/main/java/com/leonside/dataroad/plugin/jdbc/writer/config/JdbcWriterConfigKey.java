package com.leonside.dataroad.plugin.jdbc.writer.config;

import com.leonside.dataroad.common.config.ConfigKey;

/**
 * Configuration Keys for JdbcDataWriter
 *
 */
public enum JdbcWriterConfigKey implements ConfigKey {

    KEY_JDBC_URL("jdbcUrl","jdbURL",true, "","jdbcURL"),
    KEY_USERNAME("username","用户名",true,"", "用户名"),
    KEY_PASSWORD("password","密码",true,"", "密码"),

    KEY_TABLE("table","表名",true,"", "表名"),
    KEY_COLUMN("column","列名",false,"", "插入列名，例如：[\"id\",\"name\",\"sfzh\"]"),
    KEY_FULLCOLUMN("fullColumn","完整列名",false,"", "完整列名,非必填"),
    KEY_WRITE_MODE("writeMode","写入模式",false,"", "写入模式，包含INSERT、UPDATE、REPLACE、UPSERT、STREAM"),

    KEY_UPDATEKEY("updateKey","更新键值",false,"", "更新键值"),
    KEY_INSERT_SQL_MODE("insertSqlMode","插入模式",false,"", "插入模式,适用于postgresql"),


    KEY_PRE_SQL("preSql","前置SQL",false,"", "前置SQL，采用数组配置方式，例如：[\"update t1 set t1.status='0'\"]"),
    KEY_POST_SQL("postSql","后置SQL",false,"", "后置SQL，采用数组配置方式，例如：[\"update t1 set t1.status='0'\"]"),
    KEY_BATCH_SIZE("batchSize","批量写入大小",false,"1024", "批量写入大小，默认1024"),
    ;

//    public static final String KEY_FULL_COLUMN = "fullColumn";
//    public static final String KEY_PROPERTIES = "properties";

    private String name;
    private String cnName;
    private String desc;
    private boolean required;
    private String defaultValue;

    JdbcWriterConfigKey(String name, String cnName,boolean required, String defaultValue, String desc) {
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

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
