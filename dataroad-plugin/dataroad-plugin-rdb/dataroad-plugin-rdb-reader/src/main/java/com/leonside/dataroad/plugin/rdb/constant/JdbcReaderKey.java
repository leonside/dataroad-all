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

package com.leonside.dataroad.plugin.rdb.constant;

/**
 * Configuration Keys for JdbcDataReader
 */
public enum JdbcReaderKey {

    KEY_JDBC_URL("jdbcUrl", "JdbcUrl",true),
    KEY_SPLIK_KEY("splitPk", "分片键",false),
    KEY_USER_NAME("username", "用户名",true),
    KEY_PASSWORD("password", "密码",true),
    KEY_WHERE("where", "where条件",false),
    KEY_FETCH_SIZE("fetchSize", "fetchSize",false),
    KEY_QUERY_TIME_OUT("queryTimeOut", "查询超时时间",false),
    KEY_REQUEST_ACCUMULATOR_INTERVAL("requestAccumulatorInterval", "Accumulator间隔时间",false),
    KEY_INCRE_COLUMN("increColumn", "增量字段",false),
    KEY_START_LOCATION("startLocation", "开始值",false),
    KEY_CUSTOM_SQL("customSql", "自定义SQL",false),
    KEY_ORDER_BY_COLUMN("orderByColumn", "排序列名",false),
    KEY_USE_MAX_FUNC("useMaxFunc", "用于标记是否保存endLocation位置的一条或多条数据，true：不保存，false(默认)：保存， 某些情况下可能出现最后几条数据被重复记录的情况，可以将此参数配置为true",false),
    KEY_POLLING("polling", "是否轮询",false),
    KEY_POLLING_INTERVAL("pollingInterval", "轮询间隔",false),
    KEY_TABLE("table", "表名",false),
    KEY_COLUMN("column", "列名",false);

    private String name;
    private String desc;
    private boolean required;

    JdbcReaderKey(String name, String desc,boolean required) {
        this.name = name;
        this.desc = desc;
        this.required = required;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
