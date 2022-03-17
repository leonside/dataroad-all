# 流程设计说明

## 插件说明
Dataroad各个组件采用SPI插件方式进行开发，其中已支持reader、writer、processor、lookup、agg、deciderOn、union等类型插件。


| <img width=60/> 插件类型<img width=60/> |  <img width=60/> 插件类型说明   <img width=60/>  | <img width=60/> 插件名称        <img width=60/> |
| :------------------------------------------: | :----------------------------------------: | :-------------------------------------------: |
|                    reader |  读        |  [mysql](plugin/mysql-reader.md) <br/> [mysql stream](plugin/mysql-stream.md)<br/> [oracle](plugin/oracle-reader.md)<br/> [postgresql](plugin/postgresql-reader.md)<br/>  [elasticsearch](plugin/elasticsearch-reader.md)<br/>             |
|                    writer |  写        | [mysql](plugin/mysql-writer.md) <br/> [mysql stream](plugin/mysql-stream.md)<br/> [oracle](plugin/oracle-writer.md)<br/> [postgresql](plugin/postgresql-writer.md)<br/>  [elasticsearch](plugin/elasticsearch-writer.md)<br/>  |
|                  processor|  转换       |  [sql转换过滤](plugin/sqltrans.md)<br/> [script转换](plugin/scripttrans.md) <br/> [script过滤](plugin/scriptfilter.md)  |
|                lookup     | 维表补全     |  [mysql](plugin/lookup-mysql.md) <br/>  [静态数据](plugin/lookup-direct.md)   |
|                 agg       | 聚合计算     |  [计数窗口聚合](plugin/agg-countWindowAgg.md)  <br/>[滑动窗口聚合](plugin/agg-slidingWindowAgg.md)  <br/>[滚动窗口聚合](plugin/agg-tumblingWindowAgg.md)  <br/>  |
|                 deciderOn | 分流        |  [并行分支](plugin/flow-forkjoin.md) <br/> [条件分支（包容分支）](plugin/flow-forkjoin.md) <br/> [排他分支](plugin/flow-forkjoin.md) |
|                 union     | 合并        |  [合并](plugin/flow-forkjoin.md)   |


## 配置文件
一个完整的Job任务脚本配置包含 content， setting两个部分。content用于配置任务的输入源、输出源、补全、转换、聚合、分流合并等，其中包含reader、writer、processor、lookup、agg、deciderOn、union，其中当存在分支流程时，通过dependencies来指定上个节点名。
而setting则配置任务整体的环境设定，其中包含jobName、restore、speed等。 具体如下所示：
```java
{
	"job" : {
	  "content" :[{
    	"mysqlreader1" : {              ---自定义插件名，此处定义读插件
          "type" : "reader",            ---插件类型
          "pluginName" : "mysqlReader", ---插件名
          "parameter" : {               ---插件参数
          }
      },
      "myprocessor1" : {                     ---自定义插件名，此处定义脚本过滤插件
        "type" : "processor",
        "pluginName" : "filterProcessor",
        "parameter" : {
        }
      },
      "deciderFlow_1" : {                         ---自定义插件名，此处定义流程分支
        "type": "deciderOn",                      ---插件类型
        "pluginName": "expressionPredicate",      ---插件名，此处采用条件分支插件
        "dependencies": ["myprocessor1"],         ---指定上级插件名（如果不存在分支的流程无需配置）
        "parameter": {                            ---插件参数
          "expression": "row.getField('sex')==1"  ---定义分支表达式
        }
      },
    	"mysqlwriter1" : {                  ---自定义插件名，此处定义写插件
          "type" : "reader",
          "pluginName" : "mysqlWriter",
          "dependencies": ["deciderFlow_1"],
          "parameter" : {
          }
		},
          "deciderFlow_2" : {                     ---自定义插件名，此处定义流程分支
            "type": "deciderOn",
            "pluginName": "expressionPredicate",
            "dependencies": ["myprocessor1"],
            "parameter": {
              "expression": "row.getField('sex')==2"
            }
          },
          "mysqlwriter2" : {                  ---自定义插件名，此处定义写插件
            "type" : "reader",
            "pluginName" : "mysqlWriter",
            "dependencies": ["deciderFlow_2"],---上级插件名（如果不存在分支的流程无需配置）
            "parameter" : {
            }
          }
    }],
   "setting" : {
      "jobName": "myJob",               ---任务名
      "restore" : {                     ---配置同步任务类型（离线同步、实时采集）和断点续传功能
      },
      "speed" : {                       ---配置任务并发数及速率限制
      }
    }
	}
}
```
其中转换流程设计图展现如下:<br/>

![](/doc/images/flow-designer-flow.png)

| 名称 |  | 说明 | 是否必填 |
| --- | --- | --- | --- |
| content | reader | reader插件详细配置 | 是 |
|  | processor | 转换过滤插件详细配置 | 是 |
|  | lookup | 维表补全插件详细配置 | 是 |
|  | agg | 聚合插件详细配置 | 是 |
|  | deciderOn | 分流插件详细配置 | 是 |
|  | union | 合并插件详细配置 | 是 |
|  | writer | writer插件详细配置 | 是 |
| setting | restore | 任务类型及断点续传配置 | 否 |
|  | speed | 速率限制 | 否 |


### content配置
#### reader
reader用于配置数据的输入源，即数据从何而来。具体配置如下所示：

```java
{
  "mysqlreader1" : {              ---自定义插件名，此处定义读插件
    "type" : "reader",            ---插件类型
    "pluginName" : "mysqlReader", ---插件名
    "parameter" : {               ---插件参数
      ...
    }
  }
}
```

| 名称 | 说明 | 是否必填 |
| --- | --- | --- |
| 节点名 | 自定义插件名，不可和其他节点名称重复 | 是 |
| type | reader插件类型，具体名称参考各数据源配置文档 | 是 |
| pluginName | reader插件名称，具体名称参考各数据源配置文档 | 是 |
| parameter | 数据源配置参数，具体配置参考各数据源配置文档 | 是 |

#### writer
writer用于配置数据的输出源，即数据写往何处。具体配置如下所示：

```java
{
  "mysqlwriter2" : {                  ---自定义插件名，此处定义写插件
    "type" : "reader",                ---插件类型
    "pluginName" : "mysqlWriter",     ---插件名
    "parameter" : {                   ---插件参数
    }
  }
}
```
| 名称 | 说明 | 是否必填 |
| --- | --- | --- |
| 节点名 | 自定义插件名，不可和其他节点名称重复 | 是 |
| type | writer插件类型，具体名称参考各数据源配置文档 | 是 |
| pluginName | writer插件名称，具体名称参考各数据源配置文档 | 是 |
| parameter | 数据源配置参数，具体配置参考各数据源配置文档 | 是 |

#### processor
processor用于配置数据的过滤转换，包含SQL转换、脚本转换、脚本过滤。具体配置如下所示：

```java
{
  "myprocessor1" : {                     ---自定义插件名
    "type" : "processor",                ---插件类型为processor
    "pluginName" : "filterProcessor",    ---此处定义脚本过滤插件
    "parameter" : {
    }
  }
}
```
| 名称 | 说明 | 是否必填 |
| --- | --- | --- |
| 节点名 | 自定义插件名，不可和其他节点名称重复 | 是 |
| type | processor插件类型，具体名称参考各配置文档 | 是 |
| pluginName | processor插件名称，具体名称参考各配置文档 | 是 |
| parameter | 过滤转换插件配置参数，具体配置参考各配置文档 | 是 |

#### agg
agg用于配置聚合函数，包含max、min、avg、count、sum、stats等几种聚合函数，其中窗口类型包含时间窗口（滚动窗口、滑动窗口）、计数窗口，时间窗口可以Event Time、Ingestion Time、Processing Time作为事件时间。具体配置如下所示：

```java
{
  "myagg" : {                          ---自定义插件名，此处定义写插件
    "type" : "agg",                   ---插件类型
    "pluginName" : "slidingWindowAgg", ---插件名，此处定义滑动窗口插件
    "parameter" : {                   ---插件参数
    }
  }
}
```
| 名称 | 说明 | 是否必填 |
| --- | --- | --- |
| 节点名 | 自定义插件名，不可和其他节点名称重复 | 是 |
| type | agg插件名称，具体名称参考各配置文档 | 是 |
| pluginName | agg插件名称，具体名称参考各配置文档 | 是 |
| parameter | 聚合函数配置参数，具体配置参考各配置文档 | 是 |

#### lookup
agg用于配置聚合函数，包含max、min、avg、count、sum、stats等几种聚合函数，其中窗口类型包含时间窗口（滚动窗口、滑动窗口）、计数窗口，时间窗口可以Event Time、Ingestion Time、Processing Time作为事件时间。具体配置如下所示：

```java
{
  "myagg" : {                          ---自定义插件名，此处定义写插件
    "type" : "agg",                   ---插件类型
    "pluginName" : "slidingWindowAgg", ---插件名，此处定义滑动窗口插件
    "parameter" : {                   ---插件参数
    }
  }
}
```
| 名称 | 说明 | 是否必填 |
| --- | --- | --- |
| 节点名 | 自定义插件名，不可和其他节点名称重复 | 是 |
| type | agg插件名称，具体名称参考各配置文档 | 是 |
| pluginName | agg插件名称，具体名称参考各配置文档 | 是 |
| parameter | 聚合函数配置参数，具体配置参考各配置文档 | 是 |

#### deciderOn分流合并
deciderOn用于存在分流合并的场景，目前提供并行分支、条件分支、排他分支实现分流合并，其中支持的条件脚本语言包括Groovy、Bsh、JavaScript、Fel。具体配置如下所示：
```java
{
  "myagg" : {                                   ---自定义插件名，此处定义写插件
    "type" : "agg",                             ---插件类型
    "pluginName" : "expressionPredicate",       ---插件名，此处定义条件分支
    "parameter" : {                             ---插件参数，此处如果不配置则表示
      "expression": "row.getField('sex')==1",   ---定义脚本语句
      "language": "fel"                         ---非必填，定义脚本语言类型，默认采用fel语言
    }
  }
}
```
| 名称 | 说明 | 是否必填 |
| --- | --- | --- |
| 节点名 | 自定义插件名，不可和其他节点名称重复 | 是 |
| type | 分流插件类型，具体名称参考各配置文档 | 是 |
| pluginName | 分流插件名称，具体名称参考各配置文档 | 是 |
| parameter | 分流配置参数，具体配置参考各配置文档 | 是 |


### setting配置

#### restore
restore用于配置任务断点续传功能。具体配置如下所示：

```java
{
"restore" : {
  "isRestore" : false,
  "restoreColumnName" : "",
  "restoreColumnIndex" : 0
}
}
```
| 名称 | 说明 | 是否必填 | 默认值 | 参数类型 |
| --- | --- | --- | --- | --- |
| isRestore | 是否开启断点续传 | 否 | false | Boolean |
| restoreColumnName | 断点续传字段名称 | 开启断点续传后必填 | 无 | String |
| restoreColumnIndex | 断点续传字段索引ID | 开启断点续传后必填 | -1 | int |



#### speed
speed用于配置任务并发数。具体配置如下所示：

```java
{
"speed" : {
  "channel": 1
}
}
```
| 名称 | 说明 | 是否必填 | 默认值 | 参数类型 |
| --- | --- | --- | --- | --- |
| channel | 任务并发数 | 否 | 1 | int |


## 统一DSL语言
Dataroad同时通过了统一DSL语言实现流程定义（通常情况下只需基于JSON配置来定义流程），实现了条件分支、并行分支、排他分支等场景。

- 基于条件分支场景（包容分支），同时实现JobPredicate接口返回断言布尔值，支持lambada表达式，实现条件分支场景
```java
       Job build = JobBuilder.newInstance()
        .reader(reader)
        .decider()
        .on((context, o) ->{
            //条件判断，并返回boolean值
        }).processor(processor1)
        .on((context, o) ->{
             //条件判断，并返回boolean值
        }).processor(processor2)
        .end()
        .union()
        .writer(writer2)
        .build();

        build.execute();
```

- 并行分支场景，提供内置提供的JobPredicate.TRUE_PREDICATE返回true，实现并行分支场景
```java
        Job build = JobBuilder.newInstance().listener(new JobExecutionListener() {
        })
        .reader(reader)
        .decider()
        .on(JobPredicate.TRUE_PREDICATE).processor(processor1).writer(writer2)
        .on(JobPredicate.TRUE_PREDICATE).processor(processor2).writer(writer3)
        .end()
        .build();

        build.execute();
```

- 排他分支场景，通过otherwise()处理不满足条件的数据，实现排他分支场景
```java
       Job build = JobBuilder.newInstance()
        .reader(reader)
        .decider()
        .on((context, o) -> true).processor(processor1)
        .on((context, o) -> true).processor(processor2)
        .otherwise().processor(processor3)
        .end()
        .union()
        .writer(writer2)
        .build();

        build.execute();
```

- 另外，也可以通过返回ExecuteStatus状态来判断流程分支，同时union可指定流合并的分支。
  
```java
 Job build = JobBuilder.newInstance().listener(new JobExecutionListener() {
                })
                .reader(reader)
                .decider(new JobExecutionDecider<FlinkExecuteContext,Row>() {
                    @Override
                    public ExecuteStatus decide(FlinkExecuteContext context, Row in) {
                        Integer integer = Integer.valueOf((String) in.getField(0));
                        if(integer <5){
                            return new ExecuteStatus("1");
                        }else if(integer >= 5 && integer <= 15){
                            return new ExecuteStatus("2");
                        }else{
                            return new ExecuteStatus("3");
                        }
                    }
                })
                .on("1").processor(processor)
                .on("2").processor(processor2)
                .otherwise().writer(writer2)
                .end()
                .union(1,2)
                .writer(writer3)
                .build();
```
