/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leonside.dataroad.flink.reader;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.context.RestoreConfig;
import com.leonside.dataroad.common.domain.MetaColumn;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.reader.inputformat.GenericInputFormatSourceFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract specification of Reader Plugin
 *
 */
public abstract class BaseItemReader extends ComponentNameSupport {

    protected StreamExecutionEnvironment env;

    protected int numPartitions = 1;

    protected long bytes = Long.MAX_VALUE;

    protected String monitorUrls;

    protected RestoreConfig restoreConfig;

    protected List<String> srcCols = new ArrayList<>();

//    protected long exceptionIndex;

    public int getNumPartitions() {
        return numPartitions;
    }

    public RestoreConfig getRestoreConfig() {
        return restoreConfig;
    }

    public List<String> getSrcCols() {
        return srcCols;
    }

    public void setSrcCols(List<String> srcCols) {
        this.srcCols = srcCols;
    }

    public void doInitialize(FlinkExecuteContext executeContext, BaseConfig baseConfig) {
        this.env = executeContext.getEnvironment();
        this.numPartitions = Math.max(executeContext.getJobSetting().getSpeed().getChannel(),
                executeContext.getJobSetting().getSpeed().getReaderChannel());
        this.bytes = executeContext.getJobSetting().getSpeed().getBytes();
//        this.monitorUrls = executeContext.getJobSetting().getMonitorUrls();
        this.restoreConfig = executeContext.getJobSetting().getRestore();

        if (restoreConfig.isStream()){
            return;
        }

        if(restoreConfig.isRestore()){
            List columns = (List) baseConfig.getParameter().get("column");
            MetaColumn metaColumn = MetaColumn.getMetaColumn(columns, restoreConfig.getRestoreColumnName());
            if(metaColumn == null){
                throw new RuntimeException("Can not find restore column from json with column name:" + restoreConfig.getRestoreColumnName());
            }
            restoreConfig.setRestoreColumnIndex(metaColumn.getIndex());
            if(StringUtils.isNotEmpty(metaColumn.getType())){
                restoreConfig.setRestoreColumnType(metaColumn.getType());
            }
        }
    }

    protected DataStream<Row> createInput(FlinkExecuteContext executeContext, InputFormat inputFormat) {
        return createInput(executeContext,inputFormat,null, this.getClass().getSimpleName().toLowerCase());
    }

    protected DataStream<Row> createInput(FlinkExecuteContext executeContext, InputFormat inputFormat, TypeInformation typeInfo) {
        return createInput(executeContext,inputFormat,typeInfo,this.getClass().getSimpleName().toLowerCase());
    }


    private DataStream<Row> createInput(FlinkExecuteContext executeContext,InputFormat inputFormat,TypeInformation typeInformation, String sourceName) {
        Preconditions.checkNotNull(sourceName);
        Preconditions.checkNotNull(inputFormat);

        TypeInformation typeInfo = typeInformation != null ? typeInformation : TypeExtractor.getInputFormatTypes(inputFormat);
        GenericInputFormatSourceFunction function = new GenericInputFormatSourceFunction(inputFormat, typeInfo);
        DataStreamSource dataStreamSource = env.addSource(function, sourceName, typeInfo);
        int readerChannel = executeContext.getJobSetting().getSpeed().getReaderChannel();
        return (readerChannel > 0) ? dataStreamSource.setParallelism(readerChannel) : dataStreamSource;
    }

}
