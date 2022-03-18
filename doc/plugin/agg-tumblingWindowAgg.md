## slidingWindowAgg基于滚动窗口实现聚合计算

### 一、插件名称
类型：**agg**<br/>
名称：**slidingWindowAgg**<br/>

### 二、参数说明<br />

- **timeSize**
    - 描述：时间窗口大小，单位由timeUnit配置
    - 必选：是
    - 字段类型：int
    - 默认值：无

<br/>

- **timeUnit**
    - 描述：时间单位，默认秒
    - 必选：否
    - 字段类型：TimeUnit枚举，包含：HOURS、MINUTES、SECONDS、MILLISECONDS、MICROSECONDS
    - 默认值：秒

<br/>

- **timeType**
    - 描述：窗口时间类型，包含event, process, ingestion,其中默认process
        - event: 事件时间
        - process：处理时间
        - ingestion: 摄入时间
    - 必选：否
    - 字段类型：string
    - 默认值：process

<br/>

- **eventTimeColumn**
    - 描述：当窗口类型为event，相应设置业务事件时间字段。如 eventTimeColumn： "createTime"
    - 必选：当窗口类型为event时必填
    - 字段类型：string
    - 默认值：无

<br/>

- **outOfOrderness**
    - 描述：最大延迟时间，单位由timeUnit配置，用于解决乱序数据场景。当窗口类型为event时此配置生效。
    - 必选：否
    - 字段类型：int
    - 默认值：0

<br/>

- **keyBy**
    - 描述：分组字段，支持多个字段进行分组，例如：["name","sfzh"]
    - 必选：是
    - 字段类型：Array
    - 默认值：无

<br/>

- **agg**
    - 描述：聚合字段、聚合函数映射关系，支持多个聚合字段、多个聚合函数，例如：{"age": ["stats"],"score": ["max","min"]}
    - 支持的聚合函数
        - AVG：平均值
        - SUM：累加值
        - COUNT：统计总数
        - MAX：最大值
        - MIN：最小值
        - STATS:即包含AVG、SUM、COUNT、MAX、MIN几种运行
    - 聚合计算返回结果字段
        - 分组字段
        - aggBy：聚合计算字段
        - 聚合函数（如max、min、avg、count、sum）
        - dumpTime: 计算时间
        - beginTime: 窗口开始时间
        - endTime: 窗口结束时间
    - 必选：是
    - 字段类型：对象
    - 默认值：无

<br/>



### 四、配置示例

#### 1、单字段聚合计算,且默认依据处理时间计算
```json
{
  "job": {
    "content": [
      {
        "mysqlReader1": {
          "type" : "reader",
          "pluginName" : "mysqlReader",
          "parameter": {
            "jdbcUrl" : "jdbc:mysql://0.0.0.1:3306/database?useSSL=false",
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
            }]
          }
        },
        "mywindow": {
          "type": "agg",
          "pluginName": "slidingWindowAgg",
          "parameter": {
            "timeSize":5,
            "slideSize": 2,
            "keyBy": ["idcard"],
            "agg": {
              "sex": ["max"]
            }
          }
        },
        "mywriter": {
          "type": "writer",
          "pluginName": "esWriter",
          "parameter": {
            "address": "bigdata33:9200",
            "index": "student1_agg",
            "username": "",
            "password": "",
            "type": "_doc",
            "bulkAction": 1,
            "timeout": 1000,
            "idColumn": [
              {
                "name": "id",
                "type": "int"
              }
            ],
            "column": [
              {"name": "idcard","type": "string"},
              {"name": "aggBy","type": "string"},
              {"name": "max","type": "int"},
              {"name": "dumpTime","type": "timestamp"}
              {"name": "beginTime","type": "timestamp"}
              {"name": "endTime","type": "timestamp"}
            ]
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
        "isRestore": false
      }
    }
  }
}
```

#### 2、多字段聚合计算，且采用事件时间计算
```json
{
  "job": {
    "content": [
      {
        "mysqlReader1": {
          "type" : "reader",
          "pluginName" : "mysqlReader",
          "parameter": {
            "jdbcUrl" : "jdbc:mysql://0.0.0.1:3306/database?useSSL=false",
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
            }]
          }
        },
        "mywindow": {
          "type": "agg",
          "pluginName": "slidingWindowAgg",
          "parameter": {
            "timeSize":60,
            "timeUnit": "SECONDS",
            "timeType": "event",
            "eventTimeColumn": "create_time",
            "outOfOrderness": 5,
            "keyBy": ["idcard"],
            "agg": {
              "age": ["stats"],
              "sex": ["max"]
            }
          }
        },
        "mywriter": {
          "type": "writer",
          "pluginName": "esWriter",
          "parameter": {
            "address": "bigdata33:9200",
            "index": "student1_agg",
            "username": "",
            "password": "",
            "type": "_doc",
            "bulkAction": 1,
            "timeout": 1000,
            "idColumn": [
              {
                "name": "id",
                "type": "int"
              }
            ],
            "column": [
              {"name": "idcard","type": "string"},
              {"name": "aggBy","type": "string"},
              {"name": "max","type": "int"},
              {"name": "dumpTime","type": "timestamp"}
              {"name": "beginTime","type": "timestamp"}
              {"name": "endTime","type": "timestamp"}
            ]
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
        "isRestore": false
      }
    }
  }
}
```
