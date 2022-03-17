## Script Transformer脚本转换

### 一、插件名称
类型：**processor**<br/>
名称：**scriptTransformer**
### 二、参数说明<br />

- **language**
    - 描述：脚本语言类型，支持groovy,fel,bsh,javascript
    - 必选：否
    - 字段类型：String
    - 默认值：默认采用fel语言


- **expression**
    - 描述：脚本语言表达，无需返回值。
    - 注意：
        - 可通过"row"变量获取到Row行记录集，例如判断如果性别为空则设置默认值为0： if(row.getField("sex") == null){row.setField("sex",0) ;}。其中sex对应配置的数据库column列名。
        - 脚本转换，通过表达式修改row值，无需返回值！
        - 具体的表达式语法可参见各个脚本语言的编写规则。
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
        "expression": "int sex = row.getField('sex'); if(sex==0){row.setField("sex_value", "男");}else{row.setField("sex_value", "女");}  " ,
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
    "expression": "row.setField("area_code",row.getField("area_code").substring(0,4)) ",
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
        "expression": " if(row.getField("sex") == null){row.setField("sex",0) ;} " ,
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
        "expression": "row.setField("score",row.getField("score")+1); " ,
        "language": "bsh" 
  }
}
```