package com.leonside.dataroad.common.config;

/**
 * @author leon
 */
public interface ConfigKeyBinder {

    Class<? extends ConfigKey> bindConfigKey();

}
