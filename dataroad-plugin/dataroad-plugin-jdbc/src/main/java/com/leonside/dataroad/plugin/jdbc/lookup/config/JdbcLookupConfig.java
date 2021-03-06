package com.leonside.dataroad.plugin.jdbc.lookup.config;

import com.leonside.dataroad.common.config.ConfigKey;
import com.leonside.dataroad.common.exception.JobConfigException;
import com.leonside.dataroad.common.config.Validation;
import com.leonside.dataroad.flink.processor.lookup.config.BaseLookupConfig;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author leon
 */
@Data
public class JdbcLookupConfig extends BaseLookupConfig  {
    public JdbcLookupConfig(Map<String, Object> parameter) {
        super(parameter);
    }

    public String username;
    public String password;
    public String jdbcUrl;
    public int fetchSize;
    public String schema;
    public String table;
    public String where;
    public String customSql;
    public Map<String, Object> druidConf = new ConcurrentHashMap<>();

    /** vertx pool size */
    public int asyncPoolSize = 5;
    /** 失败重试次数 */
    public int maxRetryTimes = 3;
    public int errorLogPrintNum = 3;
    public int dbConnPoolSize = 5;
    public int eventLoopPoolSize = 1;
    public int taskQueueSize = 100000;
    public int parallelism = 1;


    @Override
    public boolean validate() {
        if(StringUtils.isEmpty(table) && StringUtils.isEmpty(customSql)){
            throw new JobConfigException("Table and Custom Sql configurations cannot be empty at the same time.");
        }

        return super.validate();
    }

    @Override
    public Class<? extends ConfigKey> bindConfigKey() {
        return JdbcLookupKey.class;
    }
}
