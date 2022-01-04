package com.leonside.dataroad.flink.config;

import com.leonside.dataroad.common.constant.ConfigKey;

/**
 * @author leon
 */
public enum ScriptExpressionConfigKey implements ConfigKey {

    KEY_EXPRESSION("expression",true,"", "表达式"),
    KEY_LANGUAGE("language",false,"", "脚本语言实现，默认aviator"),
    ;

    private String name;
    private String desc;
    private boolean required;
    private String defaultValue;

    ScriptExpressionConfigKey(String name, boolean required, String defaultValue, String desc) {
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
