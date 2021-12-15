package com.leonside.dataroad.plugin.mysqlstream.writer;

import com.leonside.dataroad.common.enums.WriteMode;
import com.leonside.dataroad.common.exception.JobConfigException;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.plugin.jdbc.DatabaseDialect;
import com.leonside.dataroad.plugin.jdbc.writer.GenericJdbcWriter;
import com.leonside.dataroad.plugin.jdbc.writer.outputformat.GenericJdbcOutputFormatBuilder;
import com.leonside.dataroad.plugin.mysql.MySqlDatabaseDialect;

import java.util.Map;

/**
 * @author leon
 */
public class MysqlStreamJdbcWriter extends GenericJdbcWriter {
    @Override
    protected GenericJdbcOutputFormatBuilder getBuilder() {
        return  new GenericJdbcOutputFormatBuilder(new MysqlStreamJdbcOutputFormat()) ;
    }

    @Override
    public void initialize(FlinkExecuteContext executeContext, Map<String, Object> parameter) {
        super.initialize(executeContext, parameter);
        if(mode != null && !mode.equalsIgnoreCase(WriteMode.STREAM.name())){
            throw new JobConfigException("There is no need to set mode or set stream type for Mysql stream writer ");
        }
        mode = WriteMode.STREAM.name();
    }

    @Override
    protected DatabaseDialect obtainDatabaseDialect() {
        return new MySqlDatabaseDialect();
    }
}
