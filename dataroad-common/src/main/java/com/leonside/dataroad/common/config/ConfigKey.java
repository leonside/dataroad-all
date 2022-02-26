package com.leonside.dataroad.common.config;

import com.leonside.dataroad.common.enums.FieldType;

/**
 * @author leon
 */
public interface ConfigKey {
    /**
     * 是否必填
     * @return
     */
     boolean isRequired();

    /**
     * 名称
     * @return
     */
     String getName();

    /**
     * 获取中文名称
     * @return
     */
    String getCnName();

    /**
     * 描述
     * @return
     */
     String getDesc();

    /**
     * 默认值
     * @return
     */
     String getDefaultValue();

    /**
     * 字段类型，主要用于前端展示
     * @return
     */
    FieldType getFieldType();
}
