package com.leonside.dataroad.plugin.jdbc.lookup.config;

import com.leonside.dataroad.common.exception.JobConfigException;
import com.leonside.dataroad.common.utils.StringUtil;
import com.leonside.dataroad.core.component.Validation;
import com.leonside.dataroad.flink.lookup.config.BaseLookupConfig;
import com.leonside.dataroad.plugin.jdbc.lookup.datasource.DruidDataSourceProvider;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author leon
 */
@Data
public class JdbcLookupConfig extends BaseLookupConfig implements Validation , Serializable {



    public int parallelism = 1;
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
    public int dbConnPoolSize;
    public int eventLoopPoolSize;
    public int taskQueueSize;


    @Override
    public boolean validate() {
        if(StringUtils.isEmpty(table) && StringUtils.isEmpty(customSql)){
            throw new JobConfigException("Table and Custom Sql configurations cannot be empty at the same time.");
        }

        return super.validate();
    }

}
