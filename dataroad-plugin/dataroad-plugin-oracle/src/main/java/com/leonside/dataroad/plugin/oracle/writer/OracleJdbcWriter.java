package com.leonside.dataroad.plugin.oracle.writer;

import com.leonside.dataroad.plugin.jdbc.DatabaseDialect;
import com.leonside.dataroad.plugin.jdbc.writer.GenericJdbcWriter;
import com.leonside.dataroad.plugin.jdbc.writer.outputformat.GenericJdbcOutputFormatBuilder;
import com.leonside.dataroad.plugin.oracle.OracleDatabaseDialect;

/**
 * @author leon
 */
public class OracleJdbcWriter extends GenericJdbcWriter {
    @Override
    protected GenericJdbcOutputFormatBuilder getBuilder() {
        return new GenericJdbcOutputFormatBuilder(new OracleJdbcOutputFormat());
    }

    @Override
    protected DatabaseDialect obtainDatabaseDialect() {
        return new OracleDatabaseDialect();
    }
}
