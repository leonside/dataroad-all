package com.leonside.dataroad.plugin.jdbc.writer.config;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.config.ConfigKey;
import com.leonside.dataroad.common.enums.WriteMode;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@Data
public class JdbcWriterConfig extends BaseConfig {

    protected String jdbcUrl;
    protected String username;
    protected String password;
    protected List<String> column = new ArrayList<>();
    protected List<String> fullColumn = new ArrayList<>();
    protected String table;
    protected List<String> preSql;
    protected List<String> postSql;
    protected int batchSize;
    protected Map<String, List<String>> updateKey;

    public String writeMode = WriteMode.INSERT.name();

    public JdbcWriterConfig(Map<String, Object> parameter) {
        super(parameter);
    }

    @Override
    public Class<? extends ConfigKey> bindConfigKey() {
        return JdbcWriterConfigKey.class;
    }
}
