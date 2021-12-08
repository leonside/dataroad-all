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

import com.leonside.dataroad.common.context.RestoreConfig;
import com.leonside.dataroad.common.domain.MetaColumn;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract specification of Reader Plugin
 *
 */
public abstract class BaseItemReader extends ComponentNameSupport implements ComponentInitialization<FlinkExecuteContext> {

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

    @Override
    public void initialize(FlinkExecuteContext executeContext, Map<String, Object> parameter) {

        this.env = executeContext.getEnvironment();
        this.numPartitions = Math.max(executeContext.getJobSetting().getSpeed().getChannel(),
                executeContext.getJobSetting().getSpeed().getReaderChannel());
        this.bytes = executeContext.getJobSetting().getSpeed().getBytes();
        this.monitorUrls = executeContext.getJobSetting().getMonitorUrls();
        this.restoreConfig = executeContext.getJobSetting().getRestore();

        if (restoreConfig.isStream()){
            return;
        }

        if(restoreConfig.isRestore()){
            List columns = (List) parameter.get("column");//todo config.getJob().getContent().get(0).getReader().getParameter().getColumn();
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


}
