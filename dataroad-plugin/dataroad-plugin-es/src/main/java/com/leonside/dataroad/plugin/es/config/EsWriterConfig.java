package com.leonside.dataroad.plugin.es.config;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.config.ConfigKey;
import com.leonside.dataroad.common.utils.ParameterUtils;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leon
 */
@Data
public class EsWriterConfig extends BaseConfig {
    public EsWriterConfig(Map<String, Object> parameter) {
        super(parameter);
    }

    public String address;
    public String username;
    public String password;
    public String index;
    public String indexType;
    public int bulkAction;

    public Integer timeout;
    public String pathPrefix;


    @Override
    public Class<? extends ConfigKey> bindConfigKey() {
        return EsWriterConfigKey.class;
    }
}
