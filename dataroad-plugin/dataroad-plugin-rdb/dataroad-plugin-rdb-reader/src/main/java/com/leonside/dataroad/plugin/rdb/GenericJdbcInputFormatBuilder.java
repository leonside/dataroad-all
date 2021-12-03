package com.leonside.dataroad.plugin.rdb;

import com.leonside.dataroad.common.constant.JobConfigConstants;
import com.leonside.dataroad.common.domain.MetaColumn;
import com.leonside.dataroad.plugin.rdb.reader.IncrementConfig;
import com.leonside.dataroad.plugin.rdb.type.TypeConverterInterface;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author leon
 */
public class GenericJdbcInputFormatBuilder extends GenericRichInputFormatBuilder<GenericJdbcInputFormat>{

    protected GenericJdbcInputFormat format;

    public GenericJdbcInputFormatBuilder(GenericJdbcInputFormat format) {
        super.format = this.format = format;
    }

    public void setDriverName(String driverName) {
        format.driverName = driverName;
    }

    public void setDbUrl(String dbUrl) {
        format.dbUrl = dbUrl;
    }

    public void setQuery(String query) {
        format.queryTemplate = query;
    }

    public void setUsername(String username) {
        format.username = username;
    }

    public void setPassword(String password) {
        format.password = password;
    }

    public void setTable(String table) {
        format.table = table;
    }

    public void setDatabaseDialect(DatabaseDialect databaseDialect) {
        format.databaseDialect = databaseDialect;
    }

    public void setTypeConverter(TypeConverterInterface converter){
        format.typeConverter = converter;
    }

    public void setMetaColumn(List<MetaColumn> metaColumns){
        format.metaColumns = metaColumns;
    }

    public void setFetchSize(int fetchSize){
        format.fetchSize = fetchSize;
    }

    public void setQueryTimeOut(int queryTimeOut){
        format.queryTimeOut = queryTimeOut;
    }

    public void setSplitKey(String splitKey){
        format.splitKey = splitKey;
    }

    public void setNumPartitions(int numPartitions){
        format.numPartitions = numPartitions;
    }

    public void setCustomSql(String customSql){
        format.customSql = customSql;
    }

    public void setProperties(Properties properties){
        format.properties = properties;
    }

    public void setHadoopConfig(Map<String,Object> dirtyHadoopConfig) {
        format.hadoopConfig = dirtyHadoopConfig;
    }

    public void setIncrementConfig(IncrementConfig incrementConfig){
        format.incrementConfig = incrementConfig;
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

        if (format.fetchSize > JobConfigConstants.MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("批量读取条数必须小于[200000]条");
        }
    }
}
