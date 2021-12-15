package com.leonside.dataroad.plugin.oracle.reader;

import com.leonside.dataroad.plugin.jdbc.DatabaseDialect;
import com.leonside.dataroad.plugin.jdbc.reader.GenericJdbcReader;
import com.leonside.dataroad.plugin.jdbc.reader.inputformat.GenericJdbcInputFormatBuilder;
import com.leonside.dataroad.plugin.oracle.OracleDatabaseDialect;

/**
 * @author leon
 */
public class OracleJdbcReader extends GenericJdbcReader {
    @Override
    protected GenericJdbcInputFormatBuilder getGenericJdbcInputFormatBuilder() {
        return new GenericJdbcInputFormatBuilder(new OracleJdbcInputFormat());
    }

    @Override
    protected DatabaseDialect obtainDatabaseDialect() {
        return new OracleDatabaseDialect();
    }
}
