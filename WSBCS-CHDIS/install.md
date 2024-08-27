# WSBCS-CHDIS缓存服务部署说明

## 1. 依赖

| 序号 | 软件                               |
| ---- | ---------------------------------- |
| 1    | FISCO-BCOS 3.0                     |
| 2    | JAVA版本：openjdk8u382-b05         |
| 3    | 数据库版本：MYSQL  MariaDB 10.1.44 |
| 4    | REDIS版本：Redis 7.0               |

## 2. 解压缩包

上传解压安装包WSBCS-CHDIS_0.4.0.tar.gz


## 3. 数据库初始化
* 新建数据库

* 在数据库执行以下脚本，脚本在压缩包‘script’目录

  ```
  -- 建表脚本
  ./script/explorer_create.sql
  -- 初始化脚本
  ./script/explorer_insert.sql
  ```

## 4. 服务配置及启停

### 4.1 服务配置修改

- 修改服务配置`conf/application.yml`

```shell
...
spring:
  # 数据库配置
  datasource:
    url: jdbc:mysql://[@4378-TDSQL_VIP]:[@4378-TDSQL_PORT]/wsbcschdis?useUnicode=true&autoReconnect=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&connectTimeout=10000&socketTimeout=60000&allowMultiQueries=true&useFractionalSeconds=true&useUnbufferedInput=false&serverTimezone=Asia/Shanghai
    username: [*user_wsbcschdis]
    password: [*pass_wsbcschdis]
...
constant:
  # aomp子系统私钥
  privateStr: "[*key_wsbcs_chdis_system_private_key]"
...
# Redis配置
jedis:
  ip: [@GLOBAL_WSBCSCHDIS_REDIS_IP]
  port: [@GLOBAL_WSBCSCHDIS_REDIS_PORT]
  authPwd: [*common_pass_wsbcs_chdis_redis]
...
swagger:
  enabled: false
```

- 修改节点配置`conf/cert-v3/chain0_group0/node-sdk-config.toml`

```shell
...
# 节点IP端口
[network]
peers=[[@GLOBAL_WBBCOCCNODE_SEALER_IP_RPCPORT_LIST]]     # The peer list to connect
# peers=["127.0.0.1:20200"]    # The peer list to connect
...
```

### 4.2 服务启停

在解压目录下执行：
```shell
启动：bash start.sh // start脚本里JAVA_HOME="/data/wepkg/openjdk8u382-b05"，如不一致，对应修改
停止：bash stop.sh
检查：bash status.sh
```
**备注**：服务进程起来后，需通过日志确认是否正常启动，出现以下内容表示正常；如果服务出现异常，确认修改配置后，重启。如果提示服务进程在运行，则先执行stop.sh，再执行start.sh。

```
...
	Application() - main run success...
```

## 5. 访问

```
可以通过swagger查看调用接口（需将配置文件将'swagger.enabled'设置为true）：
示例：http://localhost:43781/wsbcs-chdis/swagger-ui/index.html
```

**备注：** 

- 部署服务器IP和服务端口需对应修改，网络策略需开通

## 6. 查看日志

```shell
日志目录配置文件：./conf/log4j2.xml
全量日志：tail -f logs/wsbcs-chdis.log
错误日志：tail -f logs/error.log
```
