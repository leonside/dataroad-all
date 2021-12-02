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

package com.leonside.dataroad.plugin.rbd.writer;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.spi.ItemWriter;
import com.leonside.dataroad.common.utils.MapParameterUtils;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.plugin.rbd.BaseDataWriter;
import com.leonside.dataroad.plugin.rbd.outputformat.GenericJdbcOutputFormatBuilder;
import com.leonside.dataroad.plugin.rdb.DatabaseDialect;
import com.leonside.dataroad.plugin.rdb.type.TypeConverterInterface;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * The Writer plugin for any database that can be connected via JDBC.
 * <p>
 * Company: www.dtstack.com
 *
 * @author huyifan.zju@163.com
 */
public abstract class GenericJdbcDataWriter extends BaseDataWriter implements ItemWriter<FlinkExecuteContext, DataStream<Row>> {

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
    protected Properties properties;

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
        builder.setDriverName(databaseDialect.getDriverClass());
        builder.setDbUrl(dbUrl);
        builder.setUsername(username);
        builder.setPassword(password);
        builder.setBatchInterval(batchSize);
        builder.setMonitorUrls(monitorUrls);
        builder.setPreSql(preSql);
        builder.setPostSql(postSql);
        builder.setErrors(errors);
        builder.setErrorRatio(errorRatio);
        builder.setDirtyPath(dirtyPath);
        builder.setDirtyHadoopConfig(dirtyHadoopConfig);
        builder.setSrcCols(srcCols);
        builder.setDatabaseInterface(databaseDialect);
        builder.setMode(mode);
        builder.setTable(table);
        builder.setColumn(column);
        builder.setFullColumn(fullColumn);
        builder.setUpdateKey(updateKey);
        builder.setTypeConverter(typeConverter);
        builder.setRestoreConfig(restoreConfig);
        builder.setInsertSqlMode(insertSqlMode);
        builder.setProperties(properties);

        createOutput(items, builder.finish());
    }

    @Override
    public void initialize(ExecuteContext executeContext, Map<String, Object> parameter) {
        super.initialize(executeContext, parameter);
//        "writer": {
//            "name": "mysqlwriter",
//                    "parameter": {
//                "username": "username",
//                        "password": "password",
//                        "connection": [
//                {
//                    "jdbcUrl": "jdbc:mysql://0.0.0.1:3306/database?useSSL=false",
//                        "table": ["table"]
//                }
//            ],
//                "preSql": ["truncate table table"],
//                "postSql": ["update table set user_id = 1;"],
//                "writeMode": "insert",
//                        "column": ["id","user_id","name"],
//                "batchSize": 1024
//            }
//        }
        dbUrl = MapParameterUtils.getString(parameter, JdbcConfigKeys.KEY_JDBC_URL);
        username = MapParameterUtils.getString(parameter, JdbcConfigKeys.KEY_USERNAME);
        password = MapParameterUtils.getString(parameter, JdbcConfigKeys.KEY_PASSWORD);
        table = MapParameterUtils.getString(parameter, JdbcConfigKeys.KEY_TABLE);
        preSql = (List<String>) MapParameterUtils.getArrayListNullable(parameter, JdbcConfigKeys.KEY_PRE_SQL);
        postSql = (List<String>) MapParameterUtils.getArrayListNullable(parameter, JdbcConfigKeys.KEY_POST_SQL);
        batchSize = MapParameterUtils.getIntegerNullable(parameter,JdbcConfigKeys.KEY_BATCH_SIZE, DEFAULT_BATCH_SIZE);
        column = (List<String>) MapParameterUtils.getArrayList(parameter, JdbcConfigKeys.KEY_COLUMN);
        mode = MapParameterUtils.getStringNullable(parameter, JdbcConfigKeys.KEY_WRITE_MODE);

//        updateKey = (Map<String, List<String>>) writerConfig.getParameter().getVal(KEY_UPDATE_KEY);
//        fullColumn = (List<String>) MapParameterUtils.getArrayList(parameter, JdbcConfigKeys.KEY_FULL_COLUMN);

//        insertSqlMode = MapParameterUtils.getString(parameter, JdbcConfigKeys.KEY_INSERT_SQL_MODE);
//        properties = MapParameterUtils.getString(parameter, JdbcConfigKeys.KEY_PROPERTIES);

        this.databaseDialect = obtainDatabaseDialect();
    }


    protected abstract GenericJdbcOutputFormatBuilder getBuilder();

    protected abstract DatabaseDialect obtainDatabaseDialect();
}
