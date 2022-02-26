package com.leonside.dataroad.flink.processor.sql.config;

import com.leonside.dataroad.common.config.ConfigKey;
import com.leonside.dataroad.common.enums.FieldType;

/**
 * @author leon
 */
public enum SqlTransformerConfigKey implements ConfigKey {

    KEY_SQL("sql","SQL语句",true,"", "转换SQL语句,例如：select * from t1 where id<10",FieldType.OBJECT),
    KEY_TABLENAME("tableName","临时表名",true,"", "临时表名", FieldType.STRING),
    ;

    private String name;
    private String cnName;
    private String desc;
    private String defaultValue;
    private boolean required;
    public FieldType fieldType;
    SqlTransformerConfigKey(String name,String cnName, boolean required, String defaultValue, String desc, FieldType fieldType) {
        this.name = name;
        this.cnName = cnName;
        this.defaultValue =defaultValue;
        this.desc = desc;
        this.required = required;
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
