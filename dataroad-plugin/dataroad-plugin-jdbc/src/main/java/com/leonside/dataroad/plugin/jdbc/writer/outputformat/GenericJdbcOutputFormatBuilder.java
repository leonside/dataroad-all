package com.leonside.dataroad.plugin.jdbc.writer.outputformat;


import com.leonside.dataroad.common.enums.WriteMode;
import com.leonside.dataroad.flink.writer.outputformat.GenericRichOutputFormatBuilder;
import com.leonside.dataroad.plugin.jdbc.DatabaseDialect;
import com.leonside.dataroad.plugin.jdbc.type.TypeConverterInterface;
import com.leonside.dataroad.plugin.jdbc.writer.config.JdbcWriterConfig;

import java.util.List;
import java.util.Map;

/**
 */
public class GenericJdbcOutputFormatBuilder extends GenericRichOutputFormatBuilder<GenericJdbcOutputFormat, GenericJdbcOutputFormatBuilder> {

    private GenericJdbcOutputFormat format;

    public GenericJdbcOutputFormatBuilder(GenericJdbcOutputFormat format) {
        super.format = this.format = format;
    }

//    public GenericJdbcOutputFormatBuilder setUsername(String username) {
//        format.username = username;
//        return this;
//    }
//    public GenericJdbcOutputFormatBuilder setPassword(String password) {
//        format.password = password;
//        return this;
//    }
//    public GenericJdbcOutputFormatBuilder setDbUrl(String dbUrl) {
//        format.dbUrl = dbUrl;
//        return this;
//    }
//    public GenericJdbcOutputFormatBuilder setPreSql(List<String> preSql) {
//        format.preSql = preSql;
//        return this;
//    }
//    public GenericJdbcOutputFormatBuilder setPostSql(List<String> postSql) {
//        format.postSql = postSql;
//        return this;
//    }
//    public GenericJdbcOutputFormatBuilder setUpdateKey(Map<String,List<String>> updateKey) {
//        format.updateKey = updateKey;
//        return this;
//    }
//    public GenericJdbcOutputFormatBuilder setTable(String table) {
//        format.table = table;
//        return this;
//    }
//    public GenericJdbcOutputFormatBuilder setColumn(List<String> column) {
//        format.column = column;
//        return this;
//    }
//    public GenericJdbcOutputFormatBuilder setFullColumn(List<String> fullColumn) {
//        format.fullColumn = fullColumn;
//        return this;
//    }

    public GenericJdbcOutputFormatBuilder setJdbcWriterConfig(JdbcWriterConfig jdbcWriterConfig) {
        format.jdbcWriterConfig = jdbcWriterConfig;
        return this;
    }

    public GenericJdbcOutputFormatBuilder setDriverName(String driverName) {
        format.driverName = driverName;
        return this;
    }

    public GenericJdbcOutputFormatBuilder setDatabaseInterface(DatabaseDialect databaseDialect) {
        format.databaseDialect = databaseDialect;
        return this;
    }

//    public GenericJdbcOutputFormatBuilder setProperties(Properties properties){
//        format.properties = properties;
//        return this;
//    }

    public GenericJdbcOutputFormatBuilder setMode(String mode) {
        format.mode = mode;
        return this;
    }

    public GenericJdbcOutputFormatBuilder setTypeConverter(TypeConverterInterface typeConverter ){
        format.typeConverter = typeConverter;
        return this;
    }

    public GenericJdbcOutputFormatBuilder setInsertSqlMode(String insertSqlMode){
        format.insertSqlMode = insertSqlMode;
        return this;
    }


    public GenericJdbcOutputFormatBuilder setSchema(String schema){
        format.setSchema(schema);
        return this;
    }


    @Override
    protected void checkFormat() {
//        if (format.username == null) {
//            LOG.info("Username was not supplied separately.");
//        }
//        if (format.password == null) {
//            LOG.info("Password was not supplied separately.");
//        }
//        if (format.dbUrl == null) {
//            throw new IllegalArgumentException("No dababase URL supplied.");
//        }
        if (format.driverName == null) {
            throw new IllegalArgumentException("No driver supplied");
        }

        if(format.mode.equalsIgnoreCase(WriteMode.STREAM.name()) && format.getBatchInterval() > 1){
            throw new IllegalArgumentException("Batch Size must not greater than 1 when useing Stream Mode");
        }

//        if(format.getRestoreConfig().isRestore() && format.getBatchInterval() == 1){
//            throw new IllegalArgumentException("Batch Size must greater than 1 when checkpoint is open");
//        }
    }

}
