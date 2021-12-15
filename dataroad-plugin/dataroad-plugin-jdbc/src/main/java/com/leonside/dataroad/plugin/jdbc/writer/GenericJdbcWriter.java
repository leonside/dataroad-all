/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leonside.dataroad.plugin.jdbc.writer;

import com.leonside.dataroad.common.spi.ItemWriter;
import com.leonside.dataroad.common.utils.ParameterUtils;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.writer.BaseItemWriter;
import com.leonside.dataroad.plugin.jdbc.DatabaseDialect;
import com.leonside.dataroad.plugin.jdbc.type.TypeConverterInterface;
import com.leonside.dataroad.plugin.jdbc.writer.outputformat.GenericJdbcOutputFormatBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.Map;


/**
 * The Writer plugin for any database that can be connected via JDBC.
 * <p>
 * Company: www.dtstack.com
 *
 * @author huyifan.zju@163.com
 */
public abstract class GenericJdbcWriter extends BaseItemWriter implements ItemWriter<FlinkExecuteContext, DataStream<Row>> {

    protected DatabaseDialect databaseDialect;
    protected String dbUrl;
    protected String username;
    protected String password;
    protected List<String> column;
    protected String table;
    protected List<String> preSql;
    protected List<String> postSql;
    protected int batchSize;
    protected Map<String, List<String>> updateKey;
    protected List<String> fullColumn;
    protected TypeConverterInterface typeConverter;
//    protected Properties properties;

    /**
     * just for postgresql,use copy replace insert
     */
    protected String insertSqlMode;

    private static final int DEFAULT_BATCH_SIZE = 1024;

    public void setTypeConverterInterface(TypeConverterInterface typeConverter) {
        this.typeConverter = typeConverter;
    }

    public void setDatabaseDialect(DatabaseDialect databaseDialect) {
        this.databaseDialect = databaseDialect;
    }

    @Override
    public void write(FlinkExecuteContext executeContext, DataStream<Row> items) {
        GenericJdbcOutputFormatBuilder builder = getBuilder();
        builder.setDriverName(databaseDialect.getDriverClass())
                .setDbUrl(dbUrl)
                .setUsername(username)
                .setPassword(password)
                .setBatchInterval(batchSize)
                .setMonitorUrls(monitorUrls)
                .setPreSql(preSql)
                .setPostSql(postSql)
                .setErrors(errors)
                .setErrorRatio(errorRatio)
//                .setSrcCols(srcCols)
                .setDatabaseInterface(databaseDialect)
                .setMode(mode)
                .setTable(table)
                .setColumn(column)
                .setFullColumn(fullColumn)
                .setUpdateKey(updateKey)
                .setTypeConverter(typeConverter)
                .setRestoreConfig(restoreConfig)
                .setInsertSqlMode(insertSqlMode);

        createOutput(items, builder.finish());
    }

    @Override
    public void initialize(FlinkExecuteContext executeContext, Map<String, Object> parameter) {
        super.initialize(executeContext, parameter);
        dbUrl = ParameterUtils.getString(parameter, JdbcWriterKey.KEY_JDBC_URL);
        username = ParameterUtils.getString(parameter, JdbcWriterKey.KEY_USERNAME);
        password = ParameterUtils.getString(parameter, JdbcWriterKey.KEY_PASSWORD);
        table = ParameterUtils.getString(parameter, JdbcWriterKey.KEY_TABLE);
        preSql = (List<String>) ParameterUtils.getArrayList(parameter, JdbcWriterKey.KEY_PRE_SQL);
        postSql = (List<String>) ParameterUtils.getArrayList(parameter, JdbcWriterKey.KEY_POST_SQL);
        batchSize = ParameterUtils.getInteger(parameter, JdbcWriterKey.KEY_BATCH_SIZE);
        column = (List<String>) ParameterUtils.getArrayList(parameter, JdbcWriterKey.KEY_COLUMN);
        mode = ParameterUtils.getString(parameter, JdbcWriterKey.KEY_WRITE_MODE);

//        updateKey = (Map<String, List<String>>) writerConfig.getParameter().getVal(KEY_UPDATE_KEY);
//        fullColumn = (List<String>) MapParameterUtils.getArrayList(parameter, JdbcConfigKeys.KEY_FULL_COLUMN);
//        insertSqlMode = MapParameterUtils.getString(parameter, JdbcConfigKeys.KEY_INSERT_SQL_MODE);
//        properties = MapParameterUtils.getString(parameter, JdbcConfigKeys.KEY_PROPERTIES);

        this.databaseDialect = obtainDatabaseDialect();
    }


    protected abstract GenericJdbcOutputFormatBuilder getBuilder();

    protected abstract DatabaseDialect obtainDatabaseDialect();
}
