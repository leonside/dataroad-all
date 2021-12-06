package com.leonside.dataroad.plugin.oracle.writer;

import com.leonside.dataroad.plugin.oracle.OracleDatabaseDialect;
import com.leonside.dataroad.plugin.rbd.outputformat.GenericJdbcOutputFormatBuilder;
import com.leonside.dataroad.plugin.rbd.writer.GenericJdbcWriter;
import com.leonside.dataroad.plugin.rdb.DatabaseDialect;

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
