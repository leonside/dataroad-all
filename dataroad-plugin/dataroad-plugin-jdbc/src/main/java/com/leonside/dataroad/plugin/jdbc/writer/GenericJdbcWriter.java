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
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.writer.BaseItemWriter;
import com.leonside.dataroad.plugin.jdbc.DatabaseDialect;
import com.leonside.dataroad.plugin.jdbc.type.TypeConverterInterface;
import com.leonside.dataroad.plugin.jdbc.writer.config.JdbcWriterConfig;
import com.leonside.dataroad.plugin.jdbc.writer.outputformat.GenericJdbcOutputFormatBuilder;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.List;


/**
 * The Writer plugin for any database that can be connected via JDBC.
 * <p>
 * Company: www.dtstack.com
 *
 * @author huyifan.zju@163.com
 */
public abstract class GenericJdbcWriter extends BaseItemWriter implements ItemWriter<FlinkExecuteContext, DataStream<Row>>, ComponentInitialization<FlinkExecuteContext, JdbcWriterConfig> {

    protected DatabaseDialect databaseDialect;

    private JdbcWriterConfig jdbcWriterConfig;
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
                .setJdbcWriterConfig(jdbcWriterConfig)
                .setMonitorUrls(monitorUrls)
                .setErrors(errors)
                .setErrorRatio(errorRatio)
//                .setSrcCols(srcCols)
                .setDatabaseInterface(databaseDialect)
                .setMode(mode)
                .setTypeConverter(typeConverter)
                .setRestoreConfig(restoreConfig)
                .setInsertSqlMode(insertSqlMode);

        int writerChannel = executeContext.getJobSetting().getSpeed().getWriterChannel();
        if(writerChannel > 0){
            createOutput(items, builder.finish()).setParallelism(writerChannel);
        }else{
            createOutput(items, builder.finish());
        }

    }

    @Override
    public void doInitialize(FlinkExecuteContext executeContext, JdbcWriterConfig config) {
        super.doInitialize(executeContext, config);

        jdbcWriterConfig = config;

        mode = jdbcWriterConfig.getWriteMode();

        this.databaseDialect = obtainDatabaseDialect();
    }

    protected abstract GenericJdbcOutputFormatBuilder getBuilder();

    protected abstract DatabaseDialect obtainDatabaseDialect();
}
