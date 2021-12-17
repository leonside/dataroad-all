package com.leonside.dataroad.plugin.jdbc.lookup.datasource;

import com.google.common.base.CaseFormat;
import io.vertx.ext.jdbc.spi.DataSourceProvider;
import com.alibaba.druid.pool.DruidDataSource;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.spi.DataSourceProvider;

import javax.sql.DataSource;

import java.util.Map;
import java.util.Properties;

public class DruidDataSourceProvider implements DataSourceProvider {

    @Override
    public DataSource getDataSource(JsonObject config) {
        DruidDataSource dataSource = new DruidDataSource();
        Properties props = new Properties();
        for (Map.Entry<String, Object> entry : config) {
            String key = entry.getKey();
            if (!"provider_class".equals(key)) {
                String formattedName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, key);
                props.setProperty(formattedName, entry.getValue().toString());
            }
        }
        dataSource.configFromPropety(props);
        return dataSource;
    }

    @Override
    public void close(DataSource dataSource) {
        if (dataSource instanceof DruidDataSource) {
            ((DruidDataSource) dataSource).close();
        }
    }

    @Override
    public int maximumPoolSize(DataSource dataSource, JsonObject config) {
        if (dataSource instanceof DruidDataSource) {
            return ((DruidDataSource) dataSource).getMaxActive();
        }
        return -1;
    }
}