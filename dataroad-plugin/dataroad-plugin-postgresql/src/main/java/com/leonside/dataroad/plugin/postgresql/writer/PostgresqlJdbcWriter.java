package com.leonside.dataroad.plugin.postgresql.writer;

import com.leonside.dataroad.plugin.jdbc.DatabaseDialect;
import com.leonside.dataroad.plugin.jdbc.writer.GenericJdbcWriter;
import com.leonside.dataroad.plugin.jdbc.writer.outputformat.GenericJdbcOutputFormatBuilder;
import com.leonside.dataroad.plugin.postgresql.PostgresqlDatabaseDialect;

/**
 * @author leon
 */
public class PostgresqlJdbcWriter extends GenericJdbcWriter {
    @Override
    protected GenericJdbcOutputFormatBuilder getBuilder() {
        return new GenericJdbcOutputFormatBuilder(new PostgresqlJdbcOutputFormat());
    }

    @Override
    protected DatabaseDialect obtainDatabaseDialect() {
        return new PostgresqlDatabaseDialect();
    }
}
