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

package com.leonside.dataroad.plugin.rbd.constant;

/**
 * Configuration Keys for JdbcDataWriter
 *
 */
public enum JdbcWriterKey {

    KEY_JDBC_URL("jdbcUrl", "JdbcUrl",true),
    KEY_TABLE("table", "表名",true),
    KEY_WRITE_MODE("writeMode", "写入模式",false),
    KEY_USERNAME("username", "用户名",true),
    KEY_PASSWORD("password", "密码",true),
    KEY_PRE_SQL("preSql", "前置SQL",false),
    KEY_POST_SQL("postSql", "后置SQL",false),
    KEY_BATCH_SIZE("batchSize", "批量写入时间",false),
    KEY_COLUMN("column", "列名",false),
    KEY_INSERT_SQL_MODE("insertSqlMode", "插入模式",false);

//    public static final String KEY_FULL_COLUMN = "fullColumn";
//    public static final String KEY_PROPERTIES = "properties";

    private String name;
    private String desc;
    private boolean required;

    JdbcWriterKey(String name, String desc,boolean required) {
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
