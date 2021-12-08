package com.leonside.dataroad.plugin.rdb.inputformat;

import com.leonside.dataroad.common.constant.JobCommonConstant;
import com.leonside.dataroad.common.domain.MetaColumn;
import com.leonside.dataroad.flink.inputformat.GenericRichInputFormatBuilder;
import com.leonside.dataroad.plugin.rdb.DatabaseDialect;
import com.leonside.dataroad.plugin.rdb.type.TypeConverterInterface;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Properties;

/**
 * @author leon
 */
public class GenericJdbcInputFormatBuilder extends GenericRichInputFormatBuilder<GenericJdbcInputFormat, GenericJdbcInputFormatBuilder> {

    protected GenericJdbcInputFormat format;

    public GenericJdbcInputFormatBuilder(GenericJdbcInputFormat format) {
        super.format = this.format = format;
    }

    public GenericJdbcInputFormatBuilder setDriverName(String driverName) {
        format.driverName = driverName;
        return this;
    }

    public GenericJdbcInputFormatBuilder setDbUrl(String dbUrl) {
        format.dbUrl = dbUrl;
        return this;
    }

    public GenericJdbcInputFormatBuilder setQuery(String query) {
        format.queryTemplate = query;
        return this;
    }

    public GenericJdbcInputFormatBuilder setUsername(String username) {
        format.username = username;
        return this;
    }

    public GenericJdbcInputFormatBuilder setPassword(String password) {
        format.password = password;
        return this;
    }

    public GenericJdbcInputFormatBuilder setTable(String table) {
        format.table = table;
        return this;
    }

    public GenericJdbcInputFormatBuilder setDatabaseDialect(DatabaseDialect databaseDialect) {
        format.databaseDialect = databaseDialect;
        return this;
    }

    public GenericJdbcInputFormatBuilder setTypeConverter(TypeConverterInterface converter){
        format.typeConverter = converter;
        return this;
    }

    public GenericJdbcInputFormatBuilder setMetaColumn(List<MetaColumn> metaColumns){
        format.metaColumns = metaColumns;
        return this;
    }

    public GenericJdbcInputFormatBuilder setFetchSize(int fetchSize){
        format.fetchSize = fetchSize;
        return this;
    }

    public GenericJdbcInputFormatBuilder setQueryTimeOut(int queryTimeOut){
        format.queryTimeOut = queryTimeOut;
        return this;
    }

    public GenericJdbcInputFormatBuilder setSplitKey(String splitKey){
        format.splitKey = splitKey;
        return this;
    }

    public GenericJdbcInputFormatBuilder setNumPartitions(int numPartitions){
        format.numPartitions = numPartitions;
        return this;
    }

    public GenericJdbcInputFormatBuilder setCustomSql(String customSql){
        format.customSql = customSql;
        return this;
    }

    public GenericJdbcInputFormatBuilder setProperties(Properties properties){
        format.properties = properties;
        return this;
    }

    public GenericJdbcInputFormatBuilder setIncrementConfig(IncrementConfig incrementConfig){
        format.incrementConfig = incrementConfig;
        return this;
    }

    @Override
    protected void checkFormat() {

        if (format.username == null) {
            LOG.info("Username was not supplied separately.");
        }

        if (format.password == null) {
            LOG.info("Password was not supplied separately.");
        }

        if (format.dbUrl == null) {
            throw new IllegalArgumentException("No database URL supplied");
        }

        if (format.driverName == null) {
            throw new IllegalArgumentException("No driver supplied");
        }

        if (StringUtils.isEmpty(format.splitKey) && format.numPartitions > 1){
            throw new IllegalArgumentException("Must specify the split column when the channel is greater than 1");
        }

        if (format.fetchSize > JobCommonConstant.MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("批量读取条数必须小于[200000]条");
        }
    }
}
