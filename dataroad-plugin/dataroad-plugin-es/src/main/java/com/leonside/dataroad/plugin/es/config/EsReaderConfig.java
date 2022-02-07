package com.leonside.dataroad.plugin.es.config;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.config.ConfigKey;
import com.leonside.dataroad.common.config.Validation;
import lombok.Data;

import java.util.Map;

/**
 * @author leon
 */
@Data
public class EsReaderConfig extends BaseConfig  {

    public EsReaderConfig(Map<String,Object> parameter){
        super(parameter);
    }

    private String address;
    private String username;
    private String password;
    private Object query;

    private String index;
    private String indexType;
    private Integer batchSize;

    private Integer timeout;
    private String pathPrefix;

    @Override
    public Class<? extends ConfigKey> bindConfigKey() {
        return EsReaderConfigKey.class;
    }
}
