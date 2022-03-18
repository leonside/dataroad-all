# 流程分流合并
Dataroad提供了expressionPredicate、trueExpressionPredicate、otherwisePredicate几种插件，来实现如下几种分支场景：
- 并行分支：即所有的分支都会走，并行分支没有条件的概念。
- 排他分支：当条件分支为true则选择此分支，如果没有匹配到true分支，则选择otherwise分支。
- 条件分支（包容分支）：基于条件,当满足条件这执行此分支。

## 条件分支

### 一、插件名称
类型：**deciderOn**<br/>
名称：**expressionPredicate**<br/>
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

### 三、配置示例<br />
#### 1、条件分支配置示例
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
            "column": ["*"]
          }
        },
        "deciderOn_1" : {
          "type": "deciderOn",
          "pluginName": "expressionPredicate",
          "dependencies": ["mysqlReader1"],
          "parameter": {
            "expression": "row.getField('sex')==1"
          }
        },
        "mysqlWriter1" : {
           "type": "writer",
          "pluginName": "printWriter",
          "dependencies": ["deciderOn_1"]
        },
        "deciderOn_2" : {
          "type": "deciderOn",
          "pluginName": "expressionPredicate",
          "dependencies": ["mysqlReader1"],
          "parameter": {
            "expression": "row.getField('sex')==0"
          }
        },
        "mysqlWriter2" : {
          "type" : "writer",
          "pluginName" : "mysqlWriter",
          "dependencies": ["deciderOn_2"],
          "parameter" : {
            "jdbcUrl" : "jdbc:mysql://0.0.0.1:3306/database?useSSL=false",
            "username" : "username",
            "password" : "password",
            "table" : "student1",
            "column" : [ "id", "name", "sex" ],
            "writeMode" : "INSERT"
          }
        }
      }
    ]
  }
}
```

## 并行分支
### 一、插件名称
类型：**deciderOn**<br/>
名称：**trueExpressionPredicate**<br/>
### 二、参数说明<br />
并行分支无需配置参数，即配置此插件则返回true条件。

### 三、配置示例<br />
#### 1、条件分支配置示例
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
            "column": ["*"]
          }
        },
        "deciderOn_1" : {
          "type": "deciderOn",
          "pluginName": "trueExpressionPredicate",
          "dependencies": ["mysqlReader1"]
        },
        "mysqlWriter1" : {
           "type": "writer",
          "pluginName": "printWriter",
          "dependencies": ["deciderOn_1"]
        },
        "deciderOn_2" : {
          "type": "deciderOn",
          "pluginName": "trueExpressionPredicate",
          "dependencies": ["mysqlReader1"]
        },
        "mysqlWriter2" : {
          "type" : "writer",
          "pluginName" : "mysqlWriter",
          "dependencies": ["deciderOn_2"],
          "parameter" : {
            "jdbcUrl" : "jdbc:mysql://0.0.0.1:3306/database?useSSL=false",
            "username" : "username",
            "password" : "password",
            "table" : "student1",
            "column" : [ "id", "name", "sex" ],
            "writeMode" : "INSERT"
          }
        }
      }
    ]
  }
}
```



## 排他分支
### 一、插件名称
类型：**deciderOn**<br/>
名称：**otherwisePredicate**<br/>
### 二、参数说明<br />
如果没有匹配到其他条件分支，则进入此分支

### 三、配置示例<br />
#### 1、条件分支配置示例
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
            "column": ["*"]
          }
        },
        "deciderOn_1" : {
          "type": "deciderOn",
          "pluginName": "expressionPredicate",
          "dependencies": ["mysqlReader1"],
          "parameter": {
            "expression": "row.getField('sex')==1"
          }
        },
        "mysqlWriter1" : {
           "type": "writer",
          "pluginName": "printWriter",
          "dependencies": ["deciderOn_1"]
        },
        "deciderOn_2" : {
          "type": "deciderOn",
          "pluginName": "otherwisePredicate",
          "dependencies": ["mysqlReader1"]
        },
        "mysqlWriter2" : {
          "type" : "writer",
          "pluginName" : "mysqlWriter",
          "dependencies": ["deciderOn_2"],
          "parameter" : {
            "jdbcUrl" : "jdbc:mysql://0.0.0.1:3306/database?useSSL=false",
            "username" : "username",
            "password" : "password",
            "table" : "student1",
            "column" : [ "id", "name", "sex" ],
            "writeMode" : "INSERT"
          }
        }
      }
    ]
  }
}
```

