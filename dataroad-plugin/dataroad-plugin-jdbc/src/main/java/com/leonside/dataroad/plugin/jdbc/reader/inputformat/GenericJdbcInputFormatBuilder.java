package com.leonside.dataroad.plugin.jdbc.reader.inputformat;

import com.leonside.dataroad.common.constant.JobCommonConstant;
import com.leonside.dataroad.common.domain.MetaColumn;
import com.leonside.dataroad.flink.reader.inputformat.GenericRichInputFormatBuilder;
import com.leonside.dataroad.plugin.jdbc.DatabaseDialect;
import com.leonside.dataroad.plugin.jdbc.reader.config.JdbcReaderConfig;
import com.leonside.dataroad.plugin.jdbc.type.TypeConverterInterface;
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

    public GenericJdbcInputFormatBuilder jdbcReaderConfig(JdbcReaderConfig jdbcReaderConfig) {
         format.jdbcReaderConfig = jdbcReaderConfig;
        return this;
    }

    public GenericJdbcInputFormatBuilder setQuery(String query) {
        format.queryTemplate = query;
        return this;
    }
    public GenericJdbcInputFormatBuilder setTypeConverter(TypeConverterInterface converter){
        format.typeConverter = converter;
        return this;
    }
    public GenericJdbcInputFormatBuilder setDatabaseDialect(DatabaseDialect databaseDialect) {
        format.databaseDialect = databaseDialect;
        return this;
    }

   public GenericJdbcInputFormatBuilder setDriverName(String driverName) {
        format.driverName = driverName;
        return this;
    }

    public GenericJdbcInputFormatBuilder setNumPartitions(int numPartitions){
        format.numPartitions = numPartitions;
        return this;
    }

    public GenericJdbcInputFormatBuilder setProperties(Properties properties){
        format.properties = properties;
        return this;
    }

    @Override
    public boolean validate() {

        if (format.driverName == null) {
            throw new IllegalArgumentException("No driver supplied");
        }

        if (StringUtils.isEmpty(format.jdbcReaderConfig.getSplitKey()) && format.numPartitions > 1){
            throw new IllegalArgumentException("Must specify the split column when the channel is greater than 1");
        }

        if (format.jdbcReaderConfig.getFetchSize() > JobCommonConstant.MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("批量读取条数必须小于[200000]条");
        }

        return true;
    }


}
