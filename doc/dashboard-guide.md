## Dashboard指南

### 流程设计
#### 创建流程
进入流程设计菜单，点击“新建流程”，进行流程创建，如下：
![](http://r938o17k5.hn-bkt.clouddn.com/dashboard-create.png)
其中，新建流程可进行流程全局配置，详见[流程设计章节](flow-designer.md)
#### 设计流程
新建好的流程，点击“设计流程”，进行流程设计，如下：
![](http://r938o17k5.hn-bkt.clouddn.com/dashboard-guide-designer.png)
其中，可通过切换源码模式，查看流程设计对应存储结构。
#### 查看流程JSON
点击查看流程JSON，可查看最终转换成的JSON流程定义，流程JSON格式具体可参见[流程设计章节](flow-designer.md)
![](http://r938o17k5.hn-bkt.clouddn.com/dashboard-guide-json.png)
#### 下载流程JSON
点击下载流程JSON，可将设计好的流程JSON打包下载至本地
![](http://r938o17k5.hn-bkt.clouddn.com/dashboard-guide-download.png)
注：通常情况下，可直接在Dashboard上设计并运行流程，当然也可以把Dashboard仅当做流程设计器，通过可视化界面设计并最终将流程JSON下载至本地运行。
#### 删除流程
点击“删除”按钮进行流程删除。
### 流程运行
#### 上传Jar包
进入流程运行菜单，点击“上传Jar包”将Dashboard主Jar上传至Flink。
![](http://r938o17k5.hn-bkt.clouddn.com/dashboard-guide-upload.png)
注：点击上传，将会对Flink WebUI服务器上的Dashboard Jar进行校验，当已存在此Jar将确认是否重复上传。
#### 提交Job
点击“提交Job”，将会上传此任务至Flink上运行
![](http://r938o17k5.hn-bkt.clouddn.com/dashboard-guide-commit.png)

其中，各个参数说明如下：
|   参数名    |       是否必填        |                      说明                      |
| :-----------: | :-----------------: | :--------------------------------------------: |
|    entryClass     |         是          |               Dataroad运行主类，不可修改               |
| Parallelism |  否 |             并行度，此处并行度配置优先级低于流程全局配置speed中的channel配置             |
| savepointPath  |     否     | 让作业在指定的savepoint中恢复 |
| allowNonRestoredState |        否         |         通常，恢复意味着savepoint的每一个状态都要恢复到应用中去，但如果你恰好去掉了某个operator，你可以通过设置来忽略这个状态          |
| confProp |         否         |         更多Flink参数配置，采用{"参数名"："参数值"}方式配置，例如：{\"parallelism.default\":2}          |


### 流程调度
暂未实现