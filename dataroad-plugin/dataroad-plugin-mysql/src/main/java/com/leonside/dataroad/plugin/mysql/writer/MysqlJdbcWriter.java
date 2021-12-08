package com.leonside.dataroad.plugin.mysql.writer;

import com.leonside.dataroad.common.enums.WriteMode;
import com.leonside.dataroad.plugin.mysql.MySqlDatabaseDialect;
import com.leonside.dataroad.plugin.rbd.outputformat.GenericJdbcOutputFormatBuilder;
import com.leonside.dataroad.plugin.rbd.writer.GenericJdbcWriter;
import com.leonside.dataroad.plugin.rdb.DatabaseDialect;

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
