## MySQL Stream Reader

### 一、插件名称
类型：**reader**<br/>
名称：**mysqlStreamReader**<br/>
### 二、支持的数据源版本
**MySQL 5.X，并开启Binlog**<br />

### 三、参数说明<br />

- **hostname**
    - 描述：数据库的主机IP
    - 必选：是
    - 字段类型：String
    - 默认值：无

<br/>

- **port**
  - 描述：数据库的端口
  - 必选：是
  - 字段类型：String
  - 默认值：无

<br/>

- **schema**
  - 描述：数据库schema名
  - 必选：是
  - 字段类型：String
  - 默认值：无

<br/>

- **table**
    - 描述：目的表的表名称。
    - 必选：是
    - 字段类型：String
    - 默认值：否

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

## MySQL Stream Writer

### 一、插件名称
类型：**writer**<br/>
名称：**mysqlStreamWriter**<br/>
### 二、支持的数据源版本
**MySQL 5.X**<br />

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

- **batchSize**
  - 描述：一次性批量提交的记录数大小，该值可以极大减少与数据库的网络交互次数，并提升整体吞吐量。但是该值设置过大可能会造成运行进程OOM情况
  - 必选：否
  - 字段类型：int
  - 默认值：1024

<br/>



## 配置示例
### 1、基础配置
```json
{
  "job": {
    "content": [
      {
        "mysqlreader": {
          "type": "reader",
          "pluginName": "mysqlStreamReader",
          "parameter": {
            "username": "root",
            "password": "123",
            "hostname": "10.254.10.31",
            "port": 3306,
            "table": "student",
            "schema": "demo"
          }
        },
        "mywriter": {
          "type": "writer",
          "pluginName": "mysqlStreamWriter",
          "parameter": {
            "username": "root",
            "password": "123",
            "jdbcUrl": "jdbc:mysql://127.0.0.1:3306/demo?useunicode=true&characterEncoding=utf8",
            "table": "student1",
            "preSql": [],
            "postSql": [],
            "batchSize": 1
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
        "isRestore": true,
        "restoreColumnName" : "id",
        "restoreColumnType" : "int"
      }
    }
  }
}
```
