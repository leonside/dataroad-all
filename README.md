

## Dataroad简介
### 概述

​	Smart Discovery旨在对SpringCloud服务注册发现功能进行增强，提供一个可基于规则的服务注册发现、路由的智能微服务治理框架，基于此种灵活的规则配置可实现如服务注册校验、分组隔离、权重路由、主机路由、自定义标签路由、动态参数路由、灰度发布、黑白名单等业务场景。同时框架适配了主流的注册中心、配置中心，依托于配置中心的能力实现配置规则的动态下发。

​	开发者只需引入此框架，并做少量规则配置即可享受此框架带来的功能，无代码入侵；另外开发者也可以基于框架提供的扩展机制实现自定义的业务逻辑。





### 特性

- 支持数据抽取、加载、过滤、转换、聚合计算、数据补全等功能

- 支持流程编排，支持基于条件数据分流、合并

- 采用轻量易用的JSON语言进行流程定义描述，同时也提供DSL语句来编排设计流程

- 支持基于窗口聚合计算，包含max、min、avg、count、sum、stats等几种聚合函数，其中窗口类型包含时间窗口（滚动窗口、滑动窗口）、计数窗口，时间窗口可以Event Time、Ingestion Time、Processing Time作为事件时间。

- 支持基于维表进行数据补全，维表可支持关系库（如Mysql）、静态数据等方式

- 支持Groovy、Bsh、Fel、JavaScript几种脚本引擎，实现数据的过滤、转换功能

- 支持并发读写数据，可以大幅度提升读写性能

- 关系数据库支持全量、增量轮询方式抽取数据，同时Mysql库支持基于Binlog方式同步数据

- 支持失败恢复功能，可以从失败的数据位置恢复任务，提升性能

- 提供Dashboard控制台，实现流程创建、可视化的流程编排设计、流程运行、流程调度等全生命周期管理。其中Dashboard支持采用Docker方式快速部署

- 支持多种运行模式，包含命令行运行方式、Dashboard控制台运行流程

- 扩展性：组件以SPI插件方式进行开发，包含了Reader、Writer、过滤器、转换器等组件

- 支持插件Jar包隔离，按需动态加载相应的插件Jar包

  目前已支持如下数据库：

| Database Type<img width=200/> |                 Reader      <img width=200/>               |                  Writer          <img width=200/>          |
| :-----------: | :----------------------------------------: | :----------------------------------------: |
|     MySQL     | [doc](docs/offline/reader/mysqlreader.md)  | [doc](docs/offline/writer/mysqlwriter.md)  |
|    Oracle     | [doc](docs/offline/reader/oraclereader.md) | [doc](docs/offline/writer/oraclewriter.md) |
|  PostgreSQL   | [doc](docs/offline/reader/oraclereader.md) | [doc](docs/offline/reader/oraclereader.md) |
| ElasticSearch | [doc](docs/offline/reader/oraclereader.md) | [doc](docs/offline/reader/oraclereader.md) |
| Mysql Stream  | [doc](docs/offline/reader/oraclereader.md) | [doc](docs/offline/reader/oraclereader.md) |

​     目前已支持其他插件：

|  插件名 <img width=200/>  | 文档 <img width=200/> |
| :------: | :--: |
| 流程编排 | doc  |
| SQL转换  | doc  |
| 脚本转换 | doc  |
| 脚本过滤 | doc  |
|   聚合   | doc  |
| 维表补全 | doc  |



![](D:\myblog\dataroad-all\doc\images\整体架构图-整体架构图.png)



## 快速入门

### 代码下载

 使用git工具把项目clone到本地 (**如果只想通过Dashboard快速体验下Dataroad功能，可跳过此章节**)

```
git clone https://github.com/leonside/dataroad-all.git
cd dataroad-all
```



### 源码编译

进入dataroad-all目录下，执行如下命令(**如果只想通过Dashboard快速体验下Dataroad功能，可跳过此章节**)：

```
mvn clean package -DskipTests
```

其中dataroad插件存在在工程的同级目录dataroad-dist下。

### 环境准备

#### Flink安装

详见Flink相关文档，示例中采用Flink standlone安装模式。

#### 初始化示例工程脚本

初始化本示例的SQL语句（另外Dashboard中附带了大量的示例流程）:

```
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `sex` smallint(6) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `idcard` varchar(18) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `code` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `area_code` varchar(20) DEFAULT NULL,
  `score` double(255,0) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;
INSERT INTO `student` VALUES ('1', '张三', '1', '18', '福建省', '11111111111111111', '13877777777', '1001', '2021-11-17 15:43:46', '350000', '500');
INSERT INTO `student` VALUES ('2', '李四', '0', '20', '厦门市', '11111111111111111', '13877777777', '1002', '2021-11-17 15:44:20', '350200', '480');
INSERT INTO `student` VALUES ('3', '王五', '0', '22', '厦门市', '11111111111111111', '13877777771', '1003', '2021-11-17 15:44:51', '350200', '500');
INSERT INTO `student` VALUES ('4', '王五2', '1', '22', '厦门市', '11111111111111111', '13877777771', '1004', '2021-11-17 15:44:51', '3502', '501');
INSERT INTO `student` VALUES ('5', '王五3', '0', '17', '漳州市', '11111111111111111', '13877777771', '1004', '2021-11-17 15:44:51', '3504', '602');
INSERT INTO `student` VALUES ('6', '王五', '1', '23', '厦门集美', '11111111111111111', '13877777771', '1005', '2022-01-26 08:54:46', '3504', '501');
INSERT INTO `student` VALUES ('7', '王六', '0', '23', '漳州', '11111111111111111', '13877777771', '1005', '2021-12-03 17:23:03', '3504', '501');
INSERT INTO `student` VALUES ('8', '王五', '0', '23', '厦门集美', '11111111111111111', '13877777771', '1005', '2022-01-26 08:54:46', '3501', '351');
INSERT INTO `student` VALUES ('9', '王五', '0', '23', '厦门集美', '11111111111111111', '13877777771', '1005', '2022-01-26 08:54:46', '3501', '501');
INSERT INTO `student` VALUES ('10', '王五', '0', '23', '厦门集美', '11111111111111111', '13877777771', '1005', '2022-01-26 08:54:46', '3501', '551');

DROP TABLE IF EXISTS `student1`;
CREATE TABLE `student1` (
  `id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(50) DEFAULT NULL,
  `sex` smallint(6) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `idcard` varchar(18) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `code` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `area_code` varchar(20) DEFAULT NULL,
  `score` double(255,0) DEFAULT NULL,
  `sex_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `student2`;
CREATE TABLE `student2` (
  `id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(50) DEFAULT NULL,
  `sex` smallint(6) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `idcard` varchar(18) DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `code` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `area_code` varchar(20) DEFAULT NULL,
  `score` double(255,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

```



### 通过Dashboard方式运行任务

#### 部署Dashboard工程

##### 采用Docker方式运行Dashboard

执行如下命令运行dashboard

```java
docker run -d -p 8089:8089 -e WEB_UI=http://10.254.10.32:8081 -e DATAROAD_DIST=/opt/dataroad-dist/ -e HOST_ADDRESS=192.168.10.9 -e SAMPLE_ENABLED=true dataroad-dashboard:0.5 
```

其中环境变量说明如下：

|   环境变量    |       默认值        |                      说明                      |
| :-----------: | :-----------------: | :--------------------------------------------: |
|    WEB_UI     |         无          |               Flink的Web UI地址                |
| DATAROAD_DIST | /opt/dataroad-dist/ |             dataroad插件根路径地址             |
| HOST_ADDRESS  |     机器IP地址      | 如无法正确获取对外访问地址，可通过此参数配置IP |
| SAMPLE_ENABLE |         doc         |         是否初始化案例数据，默认false          |

##### 通过源码编译方式运行Dashboard Jar

执行如下命令运行Dashboard，可通过设置系统属性方式指定变量：

```java
java -Dweb-ui=http://10.254.10.32:8081 -Ddataroad.sample-enabled=true -Ddataroad.dataroad-dist=/opt/dataroad-dist/ -Dhost-address=192.168.10.9 -jar dataroad-dashboard-0.5.jar
```



#### 流程设计

通过Dashboard创建并设计流程，本示例实现将学生信息源表按区划抽取至不同的目标表中，中间经过数据过滤：

![](D:\myblog\dataroad-all\doc\images\dashboard-designer.png)

其中可通过“查看JSON流程”查看设计的流程JSON，如图：

![](D:\myblog\dataroad-all\doc\images\dashboard-json.png)

#### 任务提交

进入Dashboard的流程运行菜单，选中已设计好的流程进行任务提交：

![](D:\myblog\dataroad-all\doc\images\dashboard-commit.png)

其中：提交流程可设置Flink相关参数，其中更多参数可通过confProp进行设置，例如：{\"parallelism.default\":2}

#### 查看任务

进入Flink Web UI，查看任务的运行情况。

### 通过命令行方式运行任务

#### 源码编译并获取部署包

​	详见如上代码下载、源码编译章节，获取dataroad-dist部署包及插件

#### 	上传服务器

​	将打包获取到的dataroad-dist插件包上传至部署Flink的服务器

#### 	流程设计

​	设计流程JSON，此处可通过Dashboard可视化流程设计器来设计流程（见上），并获取JSON流程配置（Dashboard已内置了一些流程JSON案例，可直接获取）。也可以

自行设计流程，其中简要的流程JSON结构如下：

![](D:\myblog\dataroad-all\doc\images\designer-json.png)

#### 	任务提交

通过flink运行任务，运行脚本如下：

```java
flink run dataroad-dashboard-0.5.jar -conf file:/tmp/mysql_customsql_decider_union_mysql.json -pluginRootDir /tmp/dataroad-dist -jobName myjob - confProp {\"parallelism.default\":1}
```

其中参数如下：

|   环境变量    | 是否必填 |                             说明                             |
| :-----------: | :------: | :----------------------------------------------------------: |
|     conf      |    是    | 流程JSON文件路径，支持file、http几种资源类型，如：-conf http://ip:port/api/jobflowjson/mysql_scriptfilter_mysql 直接引用Dashboard的设计流程 |
| pluginRootDir |    是    |         dataroad插件根路径地址，如/tmp/dataroad-dist         |
|  confProp否   |    否    |    Flink参数，采用Json格式，如{\"parallelism.default\":1}    |
|    jobName    |    否    |                           任务名称                           |



#### 	查看任务

进入Flink Web UI，查看任务的运行情况。

## Dashboard操作指南

请查看[Dashboard操作指南]

## 流程设计说明

请查看[流程设计说明]

## 插件通用配置

mysql-reader
mysql-writer

mysql-stream-reader
mysql-stream-writer
oracle-reader

oracle-writer
postgresql-reader  ---验证下
postgresql-writer
elasticsearch-reader
elasticsearch-writer

scriptFilter
scriptTransformer
sqlTransformer

countWindowAgg
tumblingWindowAgg
slidingWindowAgg

mysql-lookup
direct-lookup



## 写在最后
Smart Discovery是在总结工作项目中使用SpringCloud遇到的诉求，利用工作之外的业余时间编写的，难免会有些不完善的地方欢迎指正。另外，Smart Discovery框架如果对你有帮助的话也请点个赞，这是对我最大的鼓励！

