## Oracle Writer

### 一、插件名称
类型：**reader**<br/>
名称：**oracleWriter**<br/>
### 二、支持的数据源版本
**Oracle 9 及以上**<br />
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
    - 必选：是
    - 字段类型：String
    - 默认值：无

<br/>  

- **column**
    - 描述：目的表需要写入数据的字段,字段之间用英文逗号分隔。例如: "column": ["id","name","age"]
    - 必选：是
    - 字段类型：Array
    - 默认值：无

<br/>

- **fullcolumn**
    - 描述：目的表中的所有字段，字段之间用英文逗号分隔。例如: "column": ["id","name","age","hobby"]，如果不配置，将在系统表中获取
    - 必选：否
    - 字段类型：Array
    - 默认值：无

<br/>

- **preSql**
    - 描述：写入数据到目的表前，会先执行这里的一组标准语句。例如：["update student t1 set t1.status='0'"]
    - 必选：否
    - 字段类型：Array
    - 默认值：无

<br/>

- **postSql**
    - 描述：写入数据到目的表后，会执行这里的一组标准语句。例如：["update student t1 set t1.status='0'"]
    - 必选：否
    - 字段类型：Array
    - 默认值：无

<br/>

- **writeMode**
    - 描述：控制写入数据到目标表采用 `insert into` 或者 `replace into` 或者 `ON DUPLICATE KEY UPDATE` 语句。其中当写入模式为update和replace时，默认采用主键作为唯一索引
    - 必选：是
    - 所有选项：INSERT/REPLACE/UPDATE
    - 字段类型：String
    - 默认值：INSERT

<br/>

- **batchSize**
    - 描述：一次性批量提交的记录数大小，该值可以极大减少与数据库的网络交互次数，并提升整体吞吐量。但是该值设置过大可能会造成运行进程OOM情况
    - 必选：否
    - 字段类型：int
    - 默认值：1024

<br/>


### 四、配置示例
#### 1、insert
```json
{
  "job" : {
    "content" : [ {
      "oracleReader1" : {
        "type" : "reader",
        "pluginName" : "oracleReader",
        "parameter" : {
          "jdbcUrl" : "jdbc:oracle:thin:@127.0.0.1:1521:orcl",
          "username" : "username",
          "password" : "password",
          "table" : "student",
          "column": [
            {
              "name": "ID",
              "type": "id"
            },
            {
              "name": "NAME",
              "type": "varchar"
            },
            {
              "name": "SEX",
              "type": "int"
            }
          ]
        }
      },
      "oracleWriter2" : {
        "type" : "writer",
        "pluginName" : "oracleWriter",
        "parameter" : {
          "jdbcUrl" : "jdbc:oracle:thin:@127.0.0.1:1521:orcl",
          "username" : "username",
          "password" : "password",
          "table" : "student1",
          "column" : [ "ID", "NAME", "SEX" ],
          "writeMode" : "INSERT"
        }
      }
    } ],
    "setting": {
      "name": "myJob",
      "speed": {
        "channel": 1
      },
      "restore": {
        "isRestore": false
      }
    }
  }
}
```
#### 2、update
```json
{
  "job" : {
    "content" : [ {
      "oracleReader1" : {
        "type" : "reader",
        "pluginName" : "oracleReader",
        "parameter" : {
          "jdbcUrl" : "jdbc:oracle:thin:@127.0.0.1:1521:orcl",
          "username" : "username",
          "password" : "password",
          "table" : "student",
          "column": [
            {
              "name": "ID",
              "type": "int"
            },
            {
              "name": "NAME",
              "type": "varchar"
            },
            {
              "name": "SEX",
              "type": "int"
            }
          ]
        }
      },
      "oracleWriter2" : {
        "type" : "writer",
        "pluginName" : "oracleWriter",
        "parameter" : {
          "jdbcUrl" : "jdbc:oracle:thin:@127.0.0.1:1521:orcl",
          "username" : "username",
          "password" : "password",
          "table" : "student1",
          "column" : [ "ID", "NAME", "SEX" ],
          "writeMode" : "UPDATE"
        }
      }
    } ],
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
#### 3、replace
```json
{
  "job" : {
    "content" : [ {
      "oracleReader1" : {
        "type" : "reader",
        "pluginName" : "oracleReader",
        "parameter" : {
          "jdbcUrl" : "jdbc:oracle:thin:@127.0.0.1:1521:orcl",
          "username" : "username",
          "password" : "password",
          "table" : "student",
          "column": [
            {
              "name": "ID",
              "type": "int"
            },
            {
              "name": "NAME",
              "type": "varchar"
            },
            {
              "name": "SEX",
              "type": "int"
            }
          ]
        }
      },
      "oracleWriter2" : {
        "type" : "writer",
        "pluginName" : "oracleWriter",
        "parameter" : {
          "jdbcUrl" : "jdbc:oracle:thin:@127.0.0.1:1521:orcl",
          "username" : "username",
          "password" : "password",
          "table" : "student1",
          "column" : [ "ID", "NAME", "SEX" ],
          "writeMode" : "REPLACE"
        }
      }
    } ],
    "setting": {
      "name": "myJob",
      "speed": {
        "channel": 1
      },
      "restore": {
        "isRestore": false
      }
    }
  }
}
```


