package com.leonside.dataroad.plugin.mysqlstream.config;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.config.ConfigKey;
import com.leonside.dataroad.common.config.Validation;
import lombok.Data;

import java.util.Map;

/**
 * @author leon
 */
@Data
public class MysqlStreamReaderConfig extends BaseConfig  {

    protected String hostname;
    protected int port;
    protected String schema;

    protected String username;
    protected String password;
    protected String table;

    public MysqlStreamReaderConfig(Map<String, Object> parameter) {
        super(parameter);
    }

    @Override
    public Class<? extends ConfigKey> bindConfigKey() {
        return MysqlStreamReaderConfigKey.class;
    }
}
