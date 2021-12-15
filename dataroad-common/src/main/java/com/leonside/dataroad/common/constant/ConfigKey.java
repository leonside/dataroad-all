package com.leonside.dataroad.common.constant;

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
     * 描述
     * @return
     */
     String getDesc();

    /**
     * 默认值
     * @return
     */
     String getDefaultValue();
}
