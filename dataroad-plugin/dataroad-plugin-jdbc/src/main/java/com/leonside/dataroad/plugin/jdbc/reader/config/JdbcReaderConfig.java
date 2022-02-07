package com.leonside.dataroad.plugin.jdbc.reader.config;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.domain.MetaColumn;
import com.leonside.dataroad.plugin.jdbc.reader.inputformat.IncrementConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@Data
public class JdbcReaderConfig extends BaseConfig {

    protected String username;
    protected String password;
    protected String jdbcUrl;

    protected String table;
    protected String where;
    protected String customSql;
    protected String orderByColumn;

    protected String splitKey;
    protected int fetchSize;
    protected int queryTimeOut;
    //手动设置
    protected List<MetaColumn> metaColumns;

    protected IncrementConfig incrementConfig;

    public JdbcReaderConfig(Map<String, Object> parameter) {
        super(parameter);
    }

    @Override
    public Class<JdbcReaderConfigKey> bindConfigKey() {
        return JdbcReaderConfigKey.class;
    }
}
