package com.leonside.dataroad.plugin.mysql.reader;

import com.leonside.dataroad.plugin.rdb.DatabaseDialect;
import com.leonside.dataroad.plugin.rdb.GenericJdbcInputFormat;
import com.leonside.dataroad.plugin.rdb.GenericJdbcInputFormatBuilder;
import com.leonside.dataroad.plugin.rdb.GenericJdbcReader;

/**
 * @author leon
 */
public class MysqlJdbcReader extends GenericJdbcReader {

//    public MysqlReader(DataTransferConfig config, StreamExecutionEnvironment env) {
//        super(config, env);
//        setDatabaseInterface(new MySqlDatabaseMeta());
//        dbUrl = DbUtil.formatJdbcUrl(dbUrl, Collections.singletonMap("zeroDateTimeBehavior", "convertToNull"));
//    }

    @Override
    protected GenericJdbcInputFormatBuilder getGenericJdbcInputFormatBuilder() {
        return new GenericJdbcInputFormatBuilder(new MysqlJdbcInputFormat());
    }

    @Override
    protected DatabaseDialect obtainDatabaseDialect() {
        return new MySqlDatabaseDialect();
    }

}
