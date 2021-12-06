package com.leonside.dataroad.plugin.postgresql.writer;

import com.leonside.dataroad.plugin.postgresql.PostgresqlDatabaseDialect;
import com.leonside.dataroad.plugin.rbd.outputformat.GenericJdbcOutputFormatBuilder;
import com.leonside.dataroad.plugin.rbd.writer.GenericJdbcWriter;
import com.leonside.dataroad.plugin.rdb.DatabaseDialect;

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
