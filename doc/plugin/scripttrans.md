## Script Filter脚本转换

### 一、插件名称
类型：**processor**<br/>
名称：**scriptFilter**
### 二、参数说明<br />

- **language**
    - 描述：脚本语言类型，支持groovy,fel,bsh,javascript
    - 必选：否
    - 字段类型：String
    - 默认值：默认采用fel语言


- **expression**
    - 描述：脚本语言表达，并返回boolean值。
    - 注意：
        - 表达式需返回Boolean值！
        - 可通过"row"变量获取到Row行记录集，例如判断性别为1的记录，表达式如：row.getField('sex')==1。其中sex对应配置的数据库column列名。
        - 支持 && || !（与或非）等操作，具体的表达式语法可参见各个脚本语言的编写规则。
    - 必选：是
    - 字段类型：String
    - 默认值：无


### 三、配置示例
#### 1、groovy
```json
{                        
  "type": "processor",                      
  "pluginName": "scriptFilter",            
  "parameter": {                          
        "expression": "row.getField('sex')==1 && row.getField("name")==\"zhangsan\" " ,
        "language": "groovy" 
  }
}
```


#### 2、fel

```json
{
  "type": "processor",
  "pluginName": "scriptFilter",
  "parameter": {
    "expression": "row.getField('sex')==1 && !(row.getField("name")==\"zhangsan\") ",
    "language": "fel"
  }
}
```

#### 3、javascript

```json
{                        
  "type": "processor",                      
  "pluginName": "scriptFilter",            
  "parameter": {                          
        "expression": "row.getField('sex')==1 && row.getField("name")==\"zhangsan\" " ,
        "language": "javascript" 
  }
}
```

#### 4、bsh
```json
{                        
  "type": "processor",                      
  "pluginName": "scriptFilter",            
  "parameter": {                          
        "expression": "row.getField('sex')==1 && row.getField("name").equals(\"zhangsan\") " ,
        "language": "bsh" 
  }
}
```