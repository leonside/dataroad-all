package com.leonside.dataroad.plugin.mysql.reader;

import com.leonside.dataroad.plugin.jdbc.DatabaseDialect;
import com.leonside.dataroad.plugin.jdbc.reader.GenericJdbcReader;
import com.leonside.dataroad.plugin.jdbc.reader.inputformat.GenericJdbcInputFormatBuilder;
import com.leonside.dataroad.plugin.mysql.MySqlDatabaseDialect;

/**
 * @author leon
 */
public class MysqlJdbcReader extends GenericJdbcReader {

    @Override
    protected GenericJdbcInputFormatBuilder getGenericJdbcInputFormatBuilder() {
        return new GenericJdbcInputFormatBuilder(new MysqlJdbcInputFormat());
    }

    @Override
    protected DatabaseDialect obtainDatabaseDialect() {
        return new MySqlDatabaseDialect();
    }

}
