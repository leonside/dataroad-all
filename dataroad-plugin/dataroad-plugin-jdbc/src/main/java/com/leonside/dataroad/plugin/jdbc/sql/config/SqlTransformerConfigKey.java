package com.leonside.dataroad.plugin.jdbc.sql.config;

import com.leonside.dataroad.common.config.ConfigKey;

/**
 * @author leon
 */
public enum SqlTransformerConfigKey implements ConfigKey {

    KEY_SQL("sql","SQL语句",true,"", "转换SQL语句,例如：select * from t1 where id<10"),
    KEY_TABLENAME("tableName","临时表名",true,"", "临时表名"),
    ;

    private String name;
    private String cnName;
    private String desc;
    private String defaultValue;
    private boolean required;

    SqlTransformerConfigKey(String name,String cnName, boolean required, String defaultValue, String desc) {
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
