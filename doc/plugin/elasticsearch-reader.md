## ElasticSearch Reader

### 一、插件名称
类型：**reader**<br/>
名称：**esReader**
### 二、支持的数据源版本
**Elasticsearch 6.X**
### 三、参数说明<br />

- **address**
    - 描述：Elasticsearch地址，单个节点地址采用host:port形式，多个节点的地址用逗号连接
    - 必选：是
    - 字段类型：String
    - 默认值：无



- **username**
    - 描述：Elasticsearch认证用户名
    - 必选：否
    - 字段类型：String
    - 默认值：无



- **password**
    - 描述：Elasticsearch认证密码
    - 必选：否
    - 字段类型：String
    - 默认值：无



- **query**
    - 描述：Elasticsearch查询表达式，[查询表达式](https://www.elastic.co/guide/cn/elasticsearch/guide/current/query-dsl-intro.html)
    - 必选：否
    - 字段类型：json结构体
    - 默认值：无，默认为全查询



- **batchSize**
    - 描述：每次读取数据条数
    - 必选：否
    - 字段类型：int
    - 默认值：10



- **timeout**
    - 描述：连接超时时间
    - 必选：否
    - 字段类型：int
    - 默认值：无



- **index**
    - 描述：要查询的索引名称，支持String和String[]两种类型
    - 必选：是
    - 字段类型：可以为String或者String[]
    - 默认值：无



- **indexType**
    - 描述：要查询的索引类型，默认_doc
    - 必选：是
    - 字段类型：String
    - 默认值：_doc



- **column**
    - 描述：读取elasticsearch的查询结果的若干个列，每列形式如下
        - name：字段名称，可使用多级格式查找,多级查询时采用'.'作为间隔
        - type：字段类型，当name没有指定时，则返回常量列，值为value指定
        - value：常量列的值  
          示例：
 ```json
  "column": [
    {
      "name": "id",
      "type": "integer"
    },{
      "name": "user_id",
      "type": "integer"
    },{
      "name": "name",
      "type": "string"
    }
```
- 必选：是
- 默认值：无



### 四、配置示例
```json
{
  "job": {
    "content": [
      {
        "mysqlReader1": {
          "type" : "reader",
          "pluginName" : "esReader",
          "parameter": {
            "address": "localhost:9200",
            "query": {
              "match_all": {}
            },
            "index": "tudou",
            "indexType": "_doc",
            "batchSize": 1000,
            "username": "elastic",
            "password": "abc123",
            "timeout": 10,
            "column": [
              {
                "name": "id",
                "type": "integer"
              },{
                "name": "user_id",
                "type": "integer"
              },{
                "name": "name",
                "type": "string"
              }
            ]
          }
        },
        "printwriter2" : {                  ---自定义插件名，此处定义写插件
          "type" : "writer",
          "pluginName" : "printWriter"
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
