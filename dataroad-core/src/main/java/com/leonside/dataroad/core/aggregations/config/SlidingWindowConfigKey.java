package com.leonside.dataroad.core.aggregations.config;

import com.leonside.dataroad.common.config.ConfigKey;
import com.leonside.dataroad.common.enums.FieldType;

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

    KEY_KEYBY("keyBy","分组字段",false,"", "数组类型，支持多个字段进行分组，例如：[\"name\",\"sfzh\"]",FieldType.STRING),
    KEY_AGG("agg","聚合字段",true,"", "聚合字段、聚合类型映射关系，例如：{\"age\": [\"stats\"],\"score\": [\"max\"]},其中支持AVG、SUM、COUNT、MAX、MIN、STATS聚合类型",FieldType.OBJECT),
    KEY_TIMESIZE("timeSize","窗口大小",true,"", "窗口大小",FieldType.NUMBER),
    KEY_SLIDESIZE("slideSize","滑动窗口大小",true,"", "滑动窗口大小",FieldType.NUMBER),
    KEY_TIMEUNIT("timeUnit","时间单位",false,"SECONDS", "时间单位，默认秒",FieldType.ENUM),
    KEY_TIMETYPE("timeType","窗口时间类型",false,"process", "窗口时间类型，包含event, process, ingestion,其中默认process",FieldType.ENUM),
    KEY_EVENTTIMECOLUMN("eventTimeColumn","业务时间字段",false,"", "当窗口类型为event，相应设置业务时间字段",FieldType.STRING),
    KEY_OUTOFORDERNESS("outOfOrderness","最大延迟时间",false,"0", "最大延迟时间", FieldType.NUMBER),
    ;
    private String name;
    private String cnName;
    private String desc;
    private boolean required;
    private String defaultValue;
    public FieldType fieldType;
    SlidingWindowConfigKey(String name,String cnName,boolean required,String defaultValue, String desc, FieldType fieldType) {
        this.name = name;
        this.cnName = cnName;
        this.desc = desc;
        this.required = required;
        this.defaultValue = defaultValue;
        this.fieldType = fieldType;
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
