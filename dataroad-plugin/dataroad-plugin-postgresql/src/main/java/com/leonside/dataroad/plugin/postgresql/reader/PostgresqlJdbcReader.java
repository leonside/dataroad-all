package com.leonside.dataroad.plugin.postgresql.reader;

import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.plugin.postgresql.PostgresqlDatabaseDialect;
import com.leonside.dataroad.plugin.postgresql.PostgresqlTypeConverter;
import com.leonside.dataroad.plugin.rdb.DatabaseDialect;
import com.leonside.dataroad.plugin.rdb.inputformat.GenericJdbcInputFormat;
import com.leonside.dataroad.plugin.rdb.inputformat.GenericJdbcInputFormatBuilder;
import com.leonside.dataroad.plugin.rdb.reader.GenericJdbcReader;
import com.leonside.dataroad.plugin.rdb.support.QuerySqlBuilder;
import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.Map;

/**
 * @author leon
 */
public class PostgresqlJdbcReader extends GenericJdbcReader {

    @Override
    public void initialize(FlinkExecuteContext executeContext, Map<String, Object> parameter) {
        super.initialize(executeContext, parameter);
        this.typeConverter = new PostgresqlTypeConverter();
    }

    @Override
    protected GenericJdbcInputFormatBuilder getGenericJdbcInputFormatBuilder() {
        return new GenericJdbcInputFormatBuilder(new PostgresqlJdbcInputFormat());
    }

    @Override
    protected DatabaseDialect obtainDatabaseDialect() {
        return new PostgresqlDatabaseDialect();
    }

    @Override
    public DataStream<Row> read(FlinkExecuteContext executeContext) throws Exception {
        GenericJdbcInputFormatBuilder builder = getGenericJdbcInputFormatBuilder();
        builder.setDriverName(databaseDialect.getDriverClass())
                .setDbUrl(dbUrl)
                .setUsername(username)
                .setPassword(password)
                .setBytes(bytes)
                .setMonitorUrls(monitorUrls)
                .setTable(table)
                .setDatabaseDialect(databaseDialect)
                .setTypeConverter(typeConverter)
                .setMetaColumn(metaColumns)
                .setFetchSize(fetchSize == 0 ? databaseDialect.getFetchSize() : fetchSize)
                .setQueryTimeOut(queryTimeOut == 0 ? databaseDialect.getQueryTimeout() : queryTimeOut)
                .setIncrementConfig(incrementConfig)
                .setSplitKey(splitKey)
                .setNumPartitions(numPartitions)
                .setCustomSql(customSql)
                .setRestoreConfig(restoreConfig);

        PostgresqlQuerySqlBuilder sqlBuilder = new PostgresqlQuerySqlBuilder(this);
        builder.setQuery(sqlBuilder.buildSql());

        GenericJdbcInputFormat format =  builder.finish();
        return createInput(executeContext, format);
    }
}
