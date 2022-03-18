# ElasticSearch Writer

## 一、插件名称
类型：**writer**<br/>
名称：**esWriter**
## 二、支持的数据源版本
**Elasticsearch 6.X**<br />
## 三、参数说明<br />

- **address**
    - 描述：Elasticsearch地址，单个节点地址采用host:port形式，多个节点的地址用逗号连接
    - 必选：是
    - 字段类型：String
    - 默认值：无
      <br />



- **username**
    - 描述：Elasticsearch认证用户名
    - 必选：否
    - 字段类型：String
    - 默认值：无
      <br />


- **password**
    - 描述：Elasticsearch认证密码
    - 必选：否
    - 字段类型：String
    - 默认值：无
      <br />


- **index**
    - 描述：Elasticsearch 索引值
    - 必选：是
    - 字段类型：String
    - 默认值：无
      <br />


- **indexType**
    - 描述：Elasticsearch 索引类型
    - 必选：是
    - 字段类型：String
    - 默认值：无
      <br />


- **column**
    - 描述：写入elasticsearch的若干个列，每列形式如下
```
  {
      "name": "列名",
      "type": "列类型"
  }
```

- 必选：是
- 字段类型：Array
- 默认值：无



- **idColumn**
    - 描述：用于构造文档id的若干个列，每列形式如下
    
```
[{
  "name": "id",  // column列名
  "type": "int" 列的类型，默认为string
}]
```

- 必选：否
- 注意：
    - 如果不指定idColumn属性，则会随机产生文档id
    - 如果指定的字段值存在重复或者指定了常数，按照es的逻辑，同样值的doc只会保留一份
    - 字段类型：Array
- 默认值：无
  <br />


- **bulkAction**
    - 描述：批量写入的记录条数
    - 必选：是
    - 字段类型：int
    - 默认值：100
      <br />


- **timeout**
    - 描述：连接超时时间，如果bulkAction指定的数值过大，写入数据可能会超时，这时可以配置超时时间
    - 必选：否
    - 字段类型：int
    - 默认值：无
      <br />


## 四、配置示例
```json
{
  "job": {
    "content": [
      {
        "mysqlReader1" : {
          "type" : "reader",
          "pluginName" : "mysqlReader",
          "parameter" : {
            "jdbcUrl" : "jdbc:mysql://0.0.0.1:3306/database?useSSL=false",
            "username" : "username",
            "password" : "password",
            "table" : "student",
            "column": [
              {
                "name": "id",
                "type": "id"
              },
              {
                "name": "name",
                "type": "varchar"
              },
              {
                "name": "sex",
                "type": "int"
              }
            ]
          }
        },
        "myesWriter": {
          "type" : "writer",
          "pluginName" : "esWriter",
          "parameter": {
            "address": "localhost:9200",
            "username": "elastic",
            "password": "abc123",
            "index": "tudou",
            "indexType": "_doc",
            "bulkAction": 100,
            "timeout": 100,
            "idColumn": [
              {
                "index": 0,
                "type": "integer"
              }
            ],
            "column": [
              {
                "name": "id",
                "type": "integer"
              },
              {
                "name": "name",
                "type": "string"
              },
              {
                "name": "sex",
                "type": "int"
              }
            ]
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
        "isRestore": false
      }
    }
  }
}
```
