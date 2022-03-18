## MySQL Lookup维表补全

### 一、插件名称
类型：**lookup**<br/>
名称：**mysqlLookup**<br/>
### 二、支持的数据源版本
**MySQL 5.X**<br />

### 三、参数说明<br />

- **cacheType**
  - 描述：缓存类型，包括all、lru、none
    - all:缓存所有，永不过期
    - lru:按照最近最少使用淘汰规则
    - none:不缓存
  - 必选：是
  - 字段类型：String
  - 默认值：无

<br/>

- **cacheMaxrows**
  - 描述：最大缓存数。当缓存策略配置为lru时，此参数配置生效
  - 必选：否
  - 字段类型：int
  - 默认值：-1

<br/>

- **cacheTtl**
  - 描述：缓存的过期时间，单位毫秒。当缓存策略配置为lru时，此参数配置生效
  - 必选：否
  - 字段类型：int
  - 默认值：无

<br/>

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
  - 注意：当进行维表补全后，即在原先的Row数据集中追加了补全的列，则在Writer写入时候需要在Column配置这种定义增加的列。
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

- **joinColumns**
  - 描述：Join字段映射，配置事实表和维表的Join字段映射，例如：{"sex": "code"}。
  - 必选：是
  - 字段类型：String
  - 默认值：无

<br/>

### 四、配置示例
#### 1、lru缓存策略
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
        "jdbcLookup": {
          "type": "lookup",
          "pluginName": "mysqlLookup",
          "parameter": {
            "cacheType": "lru",
            "cacheMaxrows": 10000,
            "cacheTtl": 180000,
            "username": "root",
            "password": "123",
            "jdbcUrl": "jdbc:mysql://10.254.10.31:3306/demo?useunicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai",
            "table": "dm_sys_code",
            "where": "code_id='dm_sex'",
            "joinColumns": {"sex": "sex_code"},
            "columns": ["sex_code","sex_value"]
          }
        },
        "mysqlWriter2" : {
          "type" : "writer",
          "pluginName" : "mysqlWriter",
          "parameter" : {
            "jdbcUrl" : "jdbc:mysql://0.0.0.1:3306/database?useSSL=false",
            "username" : "username",
            "password" : "password",
            "table" : "student1",
            "column": [
              {"name": "id","type": "int"},
              {"name": "name","type": "string"},
              {"name": "sex","type": "int"},
              {"name": "sex_value","type": "string"}
            ],
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
        "isRestore": false
      }
    }
  }
}
```

#### 2、none缓存策略
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
        "jdbcLookup": {
          "type": "lookup",
          "pluginName": "mysqlLookup",
          "parameter": {
            "cacheType": "nono",
            "username": "root",
            "password": "123",
            "jdbcUrl": "jdbc:mysql://10.254.10.31:3306/demo?useunicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai",
            "table": "dm_sys_code",
            "where": "code_id='dm_sex'",
            "joinColumns": {"sex": "sex_code"},
            "columns": ["sex_code","sex_value"]
          }
        },
        "mysqlWriter2" : {
          "type" : "writer",
          "pluginName" : "mysqlWriter",
          "parameter" : {
            "jdbcUrl" : "jdbc:mysql://0.0.0.1:3306/database?useSSL=false",
            "username" : "username",
            "password" : "password",
            "table" : "student1",
            "column": [
              {"name": "id","type": "int"},
              {"name": "name","type": "string"},
              {"name": "sex","type": "int"},
              {"name": "sex_value","type": "string"}
            ],
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
        "isRestore": false
      }
    }
  }
}
```

#### 3、all缓存策略
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
        "jdbcLookup": {
          "type": "lookup",
          "pluginName": "mysqlLookup",
          "parameter": {
            "cacheType": "all",
            "username": "root",
            "password": "123",
            "jdbcUrl": "jdbc:mysql://10.254.10.31:3306/demo?useunicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai",
            "table": "dm_sys_code",
            "where": "code_id='dm_sex'",
            "joinColumns": {"sex": "sex_code"},
            "columns": ["sex_code","sex_value"]
          }
        },
        "mysqlWriter2" : {
          "type" : "writer",
          "pluginName" : "mysqlWriter",
          "parameter" : {
            "jdbcUrl" : "jdbc:mysql://0.0.0.1:3306/database?useSSL=false",
            "username" : "username",
            "password" : "password",
            "table" : "student1",
            "column": [
              {"name": "id","type": "int"},
              {"name": "name","type": "string"},
              {"name": "sex","type": "int"},
              {"name": "sex_value","type": "string"}
            ],
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
        "isRestore": false
      }
    }
  }
}
```