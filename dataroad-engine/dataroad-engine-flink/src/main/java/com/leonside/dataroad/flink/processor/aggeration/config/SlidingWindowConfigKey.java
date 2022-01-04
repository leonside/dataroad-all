package com.leonside.dataroad.flink.processor.aggeration.config;

import com.leonside.dataroad.common.constant.ConfigKey;

/**
 *  * //      "timeSize":5,
 *  * //              "timeUnit": "SECOND",
 *  * //              "timeType": "event",
 *  * //              "outOfOrderness": 5,
 *  * //              "keyby": ["idcard"],
 *  * //              "agg": [{
 *  * //              "age": ["stats"],
 *  * //              "sex": ["max"]
 *  * //              }]
 * @author leon
 */
public enum SlidingWindowConfigKey implements ConfigKey {

    KEY_KEYBY("keyBy",false,"", "keyBy，数组类型"),
    KEY_AGG("agg",true,"", "聚合字段及聚合类型配置，数组配置方式，如果keyBy不为空则和keyBy对应"),
    KEY_TIMESIZE("timeSize",true,"", "窗口大小"),
    KEY_SLIDESIZE("slideSize",true,"", "滑动窗口大小"),
    KEY_TIMEUNIT("timeUnit",false,"SECONDS", "时间单位"),
    KEY_TIMETYPE("timeType",false,"process", "窗口时间类型"),
    KEY_OUTOFORDERNESS("outOfOrderness",false,"0", "最大延迟时间"),
    ;
    private String name;
    private String desc;
    private boolean required;
    private String defaultValue;

    SlidingWindowConfigKey(String name,boolean required,String defaultValue, String desc) {
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
