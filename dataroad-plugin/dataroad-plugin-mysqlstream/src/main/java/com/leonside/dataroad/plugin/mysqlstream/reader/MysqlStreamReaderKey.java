package com.leonside.dataroad.plugin.mysqlstream.reader;

/**
 * @author leon
 */
public enum MysqlStreamReaderKey {

    hostname("hostname", "主机",true),
    port("port", "端口",true),
    schema("schema", "库",true),
    username("username", "用户名",true),
    password("password", "密码",true),
    table("table", "表名",true);

    private String name;
    private String desc;
    private boolean required;

    MysqlStreamReaderKey(String name, String desc,boolean required) {
        this.name = name;
        this.desc = desc;
        this.required = required;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
