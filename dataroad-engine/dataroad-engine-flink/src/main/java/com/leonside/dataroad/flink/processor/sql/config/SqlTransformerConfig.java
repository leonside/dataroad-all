package com.leonside.dataroad.flink.processor.sql.config;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.config.ConfigKey;
import lombok.Data;

import java.util.Map;

/**
 * @author leon
 */
@Data
public class SqlTransformerConfig extends BaseConfig {

    public String sql;

    public String tableName;

    public SqlTransformerConfig(Map<String, Object> parameter) {
        super(parameter);
    }

    @Override
    public Class<? extends ConfigKey> bindConfigKey() {
        return SqlTransformerConfigKey.class;
    }
}
