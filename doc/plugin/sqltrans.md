## SQL Transformer  SQL转换

### 一、插件名称
类型：**processor**<br/>
名称：**sqlTransformer**
### 二、参数说明<br />

- **tableName**
    - 描述：临时表名，如:t1
    - 必选：是
    - 字段类型：String
    - 默认值：否


- **sql**
    - 描述：转换的SQL语句，此处语法参照Flink SQL语法，例如：select * from t1 where score>=600 and area_code like '3501%'
    - 必选：是
    - 字段类型：String
    - 默认值：无


### 三、配置示例
#### 1、groovy
```json
{                        
  "type": "processor",                      
  "pluginName": "sqlTransformer",            
  "parameter": {                          
        "sql": "select * from t1 where score>=600 and area_code like '3501%'" ,
        "tabelName": "t1" 
  }
}
```
