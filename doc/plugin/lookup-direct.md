## Direct Lookup基于静态数据补全

### 一、插件名称
类型：**lookup**<br/>
名称：**directLookup**<br/>

### 二、参数说明<br />

- **directData**
    - 描述：静态维表数据集，例如：[{"sex_code":"0", "sex_value": "男"},{"sex_code":"1", "sex_value": "女"}]
    - 必选：是
    - 字段类型：String
    - 默认值：无

<br/>


- **column**
    - 描述：需要读取的字段。只需指定字段，例如："column":["id","name"]
    - 注意：当进行维表补全后，即在原先的Row数据集中追加了补全的列，则在Writer写入时候需要在Column配置这种定义增加的列。
    - 必选：是
    - 字段类型：Array
    - 默认值：无

<br/>

- **joinColumns**
    - 描述：Join字段映射，配置事实表和维表的Join字段映射，例如：{"sex": "code"}。
    - 必选：是
    - 字段类型：String
    - 默认值：无

<br/>

### 三、配置示例
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
          "pluginName": "directLookup",
          "parameter": {
            "directData": [
              {"sex_code":"0", "sex_value": "男"},
              {"sex_code":"1", "sex_value": "女"}
            ],
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
