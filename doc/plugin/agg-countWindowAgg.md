## countWindowAgg基于计数窗口实现聚合计算

### 一、插件名称
类型：**agg**<br/>
名称：**countWindowAgg**<br/>

### 二、参数说明<br />

- **windowSize**
  - 描述：计数窗口大小，例如： windowSize：100
  - 必选：是
  - 字段类型：int
  - 默认值：无

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
    - 必选：是
    - 字段类型：对象
    - 默认值：无

<br/>



### 四、配置示例

#### 1、单字段聚合计算
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
          "pluginName": "countWindowAgg",
          "parameter": {
            "windowSize": 5,
            "keyby": ["idcard"],
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

#### 2、多字段聚合计算
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
          "pluginName": "countWindowAgg",
          "parameter": {
            "windowSize": 5,
            "keyby": ["idcard"],
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
              {"name": "min","type": "int"},
              {"name": "avg","type": "double"},
              {"name": "count","type": "int"},
              {"name": "sum","type": "int"},
              {"name": "dumpTime","type": "timestamp"}
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
