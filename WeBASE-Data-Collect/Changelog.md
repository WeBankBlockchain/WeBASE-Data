### v1.1.0 (2021-01-19)

​	WeBASE-Data-Collect（微众区块链中间件平台-数据监管服务数据导出和分析子系统）。

**Add**

- 支持导出交易gas数据，并提供gas查询和gas用户对账。增加gas数据保存开关ifSaveGas，为false时gas数据不存储数据库；gas对账开关ifGasReconciliation，为false时gas数据不对账
- 增加区块和交易数据保存开关ifSaveBlockAndTrans，为false时区块和交易交易数据不存储数据库
- 增加Elasticsearch开关配置ifEsEnable，为false时交易数据不存储Elasticsearch
- 数据表支持按天或按月分区，由定时任务创建分区

**Update**

- jar包更新

**兼容性**

- 支持FISCO-BCOS feature-external-support版本
- WeBASE-Front feature-external-support版本

详细了解,请阅读[**技术文档**](https://webasedoc.readthedocs.io/zh_CN/latest/)



### v1.0.0 (2020-05-20)

​	WeBASE-Data-Collect（微众区块链中间件平台-数据监管服务数据导出和分析子系统）。

**Add**

- 前置管理，维护WeBASE-Front服务信息
- 群组管理，查询前置群组信息
- 数据管理，查询区块和交易数据信息、定时拉取数据

**兼容性**

- 支持FISCO-BCOS 2.3.0或以上版本
- WeBASE-Front 1.3.0或以上版本

详细了解,请阅读[**技术文档**](https://webasedoc.readthedocs.io/zh_CN/latest/)

