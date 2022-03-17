## MySQL Reader

### 一、插件名称
类型：**writer**<br/>
名称：**postgresqlReader**<br/>
### 二、支持的数据源版本
**PostgreSql 9.4及以上**<br />

### 三、参数说明<br />


- **jdbcUrl**
  - 描述：针对关系型数据库的jdbc连接字符串
  - 必选：是
  - 字段类型：String
  - 默认值：无

<br/>

- **username**
  - 描述：数据源的用户名
  - 必选：是
  - 字段类型：String
  - 默认值：无

<br/>

- **password**
  - 描述：数据源指定用户名的密码
  - 必选：是
  - 字段类型：String
  - 默认值：无

<br/>

- **table**
  - 描述：目的表的表名称。
  - 必选：插件支持配置表名方式和配置自定义SQL两种方式，当采用表名配置方式此处必填。
  - 字段类型：String
  - 默认值：无

<br/>

- **column**
  - 描述：需要读取的字段。
  - 格式：支持3种格式
    - 1.读取全部字段，如果字段数量很多，可以使用下面的写法：
      ```java
          "column":["*"]
       ```

    - 2.只指定字段名称：
       ```
          "column":["id","name"]
       ```
    - 3.指定具体信息：
      ```json
          "column": [{
            "name": "col",
            "type": "datetime"
          }]
       ```

- 属性说明:
  - name：字段名称
  - type：数据库中的字段类型
- 必选：是
- 字段类型：Array
- 默认值：无

<br/>

- **where**
  - 描述：筛选条件，reader插件根据指定的column、table、where条件拼接SQL，并根据这个SQL进行数据抽取。在实际业务场景中，往往会选择当天的数据进行同步，可以将where条件指定为gmt_create > time。
  - 必选：否
  - 字段类型：String
  - 默认值：无

<br/>

- **customSql**
  - 描述：自定义的查询语句，如果只指定字段不能满足需求时，可通过此参数指定查询的sql，可以是任意复杂的查询语句。
  - 注意：
    - 只能是查询语句，否则会导致任务失败；
    - 查询语句返回的字段需要和column列表里的字段对应；
    - 当指定此参数时，column必须指定具体字段信息，不能以*号代替；
  - 必选：否
  - 字段类型：String
  - 默认值：无

<br/>


- **splitPk**
  - 描述：当speed配置中的channel大于1时指定此参数，Reader插件根据并发数和此参数指定的字段拼接sql，使每个并发读取不同的数据，提升读取速率。
  - 注意:
    - 推荐splitPk使用表主键，因为表主键通常情况下比较均匀，因此切分出来的分片也不容易出现数据热点。
    - 目前splitPk仅支持整形数据切分，`不支持浮点、字符串、日期等其他类型`。
    - **如果channel大于1但是没有配置此参数，任务将置为失败**。
  - 必选：否
  - 字段类型：String
  - 默认值：无

<br/>

- **queryTimeOut**
  - 描述：查询超时时间，单位秒。
  - 注意：当数据量很大，或者从视图查询，或者自定义sql查询时，可通过此参数指定超时时间。
  - 必选：否
  - 字段类型：int
  - 默认值：1000

<br/>

- **fetchSize**
  - 描述：一次性从数据库中读取多少条数据，jdbc默认一次将所有结果都读取到内存中，在数据量很大时可能会造成OOM，设置这个参数可以控制每次读取fetchSize条数据；开启fetchSize需要满足：数据库版本要高于5.0.2、连接参数useCursorFetch=true。
  - 注意：此参数的值不可设置过大，否则会读取超时，导致任务失败。
  - 必选：否
  - 字段类型：int
  - 默认值：Integer.MIN_VALUE

<br/>

- **polling**
  - 描述：是否开启间隔轮询，开启后会根据`pollingInterval`轮询间隔时间周期性的从数据库拉取数据。开启间隔轮询还需配置参数`pollingInterval`，`increColumn`，可以选择配置参数`startLocation`。若不配置参数`startLocation`，任务启动时将会从数据库中查询增量字段最大值作为轮询的开始位置。
  - 必选：否
  - 字段类型：Boolean
  - 默认值：false

<br/>

- **increColumn**
  - 增量字段，，例如：id
  - 必选：否
  - 字段类型：String
  - 默认值：无

<br/>  

- **pollingInterval**
  - 描述：轮询间隔时间，从数据库中拉取数据的间隔时间，默认为5000毫秒。
  - 必选：否
  - 字段类型：long
  - 默认值：5000

<br/>

- **startLocation**
  - 描述：增量查询起始位置
  - 必选：否
  - 字段类型：String
  - 默认值：无

<br/>  

- **useMaxFunc**
  - 描述：用于标记是否保存endLocation位置的一条或多条数据，true：不保存，false(默认)：保存， 某些情况下可能出现最后几条数据被重复记录的情况，可以将此参数配置为true
  - 必选：否
  - 字段类型：Boolean
  - 默认值：false

<br/>

- **requestAccumulatorInterval**
  - 描述：发送查询累加器请求的间隔时间。
  - 必选：否
  - 字段类型：int
  - 默认值：2



### 四、配置示例
#### 1、基础配置
```json
{
  "job": {
    "content": [
      {
        "postgresqlReader1": {
          "type" : "reader",
          "pluginName" : "postgresqlReader",
          "parameter": {
            "jdbcUrl" : "jdbc:postgresql://0.0.0.1:5432/postgres",
            "username" : "username",
            "password" : "password",
            "table" : "student",
            "column": ["*"],
            "customSql": "",
            "where": "id < 100",
            "splitPk": "",
            "queryTimeOut": 1000
          }
        },
        "mysqlWriter2" : {
          "type" : "writer",
          "pluginName" : "mysqlWriter",
          "parameter" : {
            "jdbcUrl" : "jdbc:postgresql://0.0.0.1:5432/postgres",
            "username" : "username",
            "password" : "password",
            "table" : "student1",
            "column" : [ "id", "name", "sex" ],
            "writeMode" : "INSERT"
          }
        }
      }
    ],
    "setting": {
      "name": "myJob",
      "speed": {
        "channel": 1
      },
      "restore": {
        "isRestore": false,
        "restoreColumnName": "",
        "restoreColumnIndex": 0
      }
    }
  }
}
```
#### 2、多通道
```json
{
  "job": {
    "content": [
      {
        "postgresqlReader1": {
          "type" : "reader",
          "pluginName" : "postgresqlReader",
          "parameter": {
            "jdbcUrl" : "jdbc:postgresql://0.0.0.1:5432/postgres",
            "username" : "username",
            "password" : "password",
            "table" : "student",
            "column": ["*"],
            "customSql": "",
            "where": "id < 100",
            "splitPk": "id",
            "queryTimeOut": 1000
          }
        },
        "mysqlWriter2" : {
          "type" : "writer",
          "pluginName" : "mysqlWriter",
          "parameter" : {
            "jdbcUrl" : "jdbc:postgresql://0.0.0.1:5432/postgres",
            "username" : "username",
            "password" : "password",
            "table" : "student1",
            "column" : [ "id", "name", "sex" ],
            "writeMode" : "INSERT"
          }
        }
      }
    ],
    "setting": {
      "name": "myJob",
      "speed": {
        "channel": 2
      },
      "restore": {
        "isRestore": false,
        "restoreColumnName": "",
        "restoreColumnIndex": 0
      }
    }
  }
}
```
#### 3、指定`customSql`
```json
{
  "job": {
    "content": [
      {
        "postgresqlReader1": {
          "type" : "reader",
          "pluginName" : "postgresqlReader",
          "parameter": {
            "jdbcUrl" : "jdbc:postgresql://0.0.0.1:5432/postgres",
            "username" : "username",
            "password" : "password",
            "table" : "",
            "column": ["id","name","sex"],
            "customSql": "select id,name,sex from table where id < 100",
            "splitPk": "",
            "queryTimeOut": 1000
          }
        },
        "mysqlWriter2" : {
          "type" : "writer",
          "pluginName" : "mysqlWriter",
          "parameter" : {
            "jdbcUrl" : "jdbc:postgresql://0.0.0.1:5432/postgres",
            "username" : "username",
            "password" : "password",
            "table" : "student1",
            "column" : [ "id", "name", "sex" ],
            "writeMode" : "INSERT"
          }
        }
      }
    ],
    "setting": {
      "name": "myJob",
      "speed": {
        "channel": 1
      },
      "restore": {
        "isRestore": false,
        "restoreColumnName": "",
        "restoreColumnIndex": 0
      }
    }
  }
}
```
#### 4、增量同步指定`startLocation`
```json
{
  "job": {
    "content": [
      {
        "postgresqlReader1": {
          "type" : "reader",
          "pluginName" : "postgresqlReader",
          "parameter": {
            "jdbcUrl" : "jdbc:postgresql://0.0.0.1:5432/postgres",
            "username" : "username",
            "password" : "password",
            "table" : "student",
            "column": [{
              "name": "id",
              "type": "int"
            },{
              "name": "name",
              "type": "varchar"
            },{
              "name": "sex",
              "type": "int"
            }],
            "where": "id < 100",
            "splitPk": "id",
            "queryTimeOut": 1000,
            "increColumn": "id",
            "startLocation": "20"
          }
        },
        "mysqlWriter2" : {
          "type" : "writer",
          "pluginName" : "mysqlWriter",
          "parameter" : {
            "jdbcUrl" : "jdbc:postgresql://0.0.0.1:5432/postgres",
            "username" : "username",
            "password" : "password",
            "table" : "student1",
            "column" : [ "id", "name", "sex" ],
            "writeMode" : "INSERT"
          }
        }
      }
    ],
    "setting": {
      "name": "myJob",
      "speed": {
        "channel": 2
      },
      "restore": {
        "isRestore": false,
        "restoreColumnName": "",
        "restoreColumnIndex": 0
      }
    }
  }
}
```
#### 5、间隔轮询
```json
{
  "job": {
    "content": [
      {
        "postgresqlReader1": {
          "type" : "reader",
          "pluginName" : "postgresqlReader",
          "parameter": {
            "jdbcUrl" : "jdbc:postgresql://0.0.0.1:5432/postgres",
            "username" : "username",
            "password" : "password",
            "table" : "student",
            "column": [{
              "name": "id",
              "type": "int"
            },{
              "name": "name",
              "type": "varchar"
            },{
              "name": "sex",
              "type": "int"
            }],
            "where": "id < 100",
            "splitPk": "id",
            "queryTimeOut": 1000,
            "increColumn": "id",
            "startLocation": "20",
            "polling": true,
            "pollingInterval": 3000
          }
        },
        "mysqlWriter2" : {
          "type" : "writer",
          "pluginName" : "mysqlWriter",
          "parameter" : {
            "jdbcUrl" : "jdbc:postgresql://0.0.0.1:5432/postgres",
            "username" : "username",
            "password" : "password",
            "table" : "student1",
            "column" : [ "id", "name", "sex" ],
            "writeMode" : "INSERT"
          }
        }
      }
    ],
    "setting": {
      "name": "myJob",
      "speed": {
        "channel": 2
      },
      "restore": {
        "isRestore": false,
        "restoreColumnName": "",
        "restoreColumnIndex": 0
      }
    }
  }
}
```
