package com.leonside.dataroad.plugin.postgresql.reader;

import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.plugin.jdbc.DatabaseDialect;
import com.leonside.dataroad.plugin.jdbc.reader.GenericJdbcReader;
import com.leonside.dataroad.plugin.postgresql.PostgresqlDatabaseDialect;
import com.leonside.dataroad.plugin.postgresql.PostgresqlTypeConverter;
import com.leonside.dataroad.plugin.jdbc.reader.inputformat.GenericJdbcInputFormat;
import com.leonside.dataroad.plugin.jdbc.reader.inputformat.GenericJdbcInputFormatBuilder;
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
        if(getJdbcReaderConfig().getFetchSize() == 0){
            getJdbcReaderConfig().setFetchSize(databaseDialect.getFetchSize());
        }
        if(getJdbcReaderConfig().getQueryTimeOut() == 0){
            getJdbcReaderConfig().setQueryTimeOut(databaseDialect.getQueryTimeout());
        }

        GenericJdbcInputFormatBuilder builder = getGenericJdbcInputFormatBuilder();
        builder.setDriverName(databaseDialect.getDriverClass())
                .setBytes(bytes)
                .setMonitorUrls(monitorUrls)
                .setDatabaseDialect(databaseDialect)
                .setTypeConverter(typeConverter)
                .jdbcReaderConfig(getJdbcReaderConfig())
//                .setDbUrl(jdbcUrl)
//                .setUsername(username)
//                .setPassword(password)
//                .setTable(table)
//                .setMetaColumn(metaColumns)
//                .setFetchSize(fetchSize == 0 ? databaseDialect.getFetchSize() : fetchSize)
//                .setQueryTimeOut(queryTimeOut == 0 ? databaseDialect.getQueryTimeout() : queryTimeOut)
//                .setIncrementConfig(incrementConfig)
//                .setSplitKey(splitKey)
//                .setCustomSql(customSql)
                .setNumPartitions(numPartitions)
                .setRestoreConfig(restoreConfig);

        PostgresqlQuerySqlBuilder sqlBuilder = new PostgresqlQuerySqlBuilder(this);
        builder.setQuery(sqlBuilder.buildSql());

        GenericJdbcInputFormat format =  builder.finish();
        return createInput(executeContext, format);
    }
}
