package com.leonside.dataroad.plugin.mysql.reader;

import com.leonside.dataroad.plugin.mysql.MySqlDatabaseDialect;
import com.leonside.dataroad.plugin.rdb.DatabaseDialect;
import com.leonside.dataroad.plugin.rdb.inputformat.GenericJdbcInputFormatBuilder;
import com.leonside.dataroad.plugin.rdb.reader.GenericJdbcReader;

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
