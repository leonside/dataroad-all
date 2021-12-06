package com.leonside.dataroad.plugin.oracle.reader;

import com.leonside.dataroad.plugin.oracle.OracleDatabaseDialect;
import com.leonside.dataroad.plugin.rdb.DatabaseDialect;
import com.leonside.dataroad.plugin.rdb.inputformat.GenericJdbcInputFormatBuilder;
import com.leonside.dataroad.plugin.rdb.reader.GenericJdbcReader;

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
