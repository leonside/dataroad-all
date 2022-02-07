package com.leonside.dataroad.flink.config;

import com.leonside.dataroad.common.config.ConfigKey;

/**
 * @author leon
 */
public enum ScriptExpressionConfigKey implements ConfigKey {

    KEY_LANGUAGE("language","脚本语言",false,"", "脚本语言实现，支持bsh、groovy、javascript、aviator，默认aviator"),
    KEY_EXPRESSION("expression","表达式",false,"", "根据选择的脚本语言，填写对应的表达式"),
    ;

    private String name;
    private String cnName;
    private String desc;
    private boolean required;
    private String defaultValue;

    ScriptExpressionConfigKey(String name, String cnName, boolean required, String defaultValue, String desc) {
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
