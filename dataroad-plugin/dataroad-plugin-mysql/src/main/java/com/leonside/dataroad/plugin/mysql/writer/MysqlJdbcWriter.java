package com.leonside.dataroad.plugin.mysql.writer;

import com.leonside.dataroad.plugin.jdbc.DatabaseDialect;
import com.leonside.dataroad.plugin.jdbc.writer.GenericJdbcWriter;
import com.leonside.dataroad.plugin.jdbc.writer.outputformat.GenericJdbcOutputFormatBuilder;
import com.leonside.dataroad.plugin.mysql.MySqlDatabaseDialect;

/**
 * @author leon
 */
public class MysqlJdbcWriter extends GenericJdbcWriter {
    @Override
    protected GenericJdbcOutputFormatBuilder getBuilder() {
        return  new GenericJdbcOutputFormatBuilder(new MysqlJdbcOutputFormat());
    }

    @Override
    protected DatabaseDialect obtainDatabaseDialect() {
        return new MySqlDatabaseDialect();
    }
}
