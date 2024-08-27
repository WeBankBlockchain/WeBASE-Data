# WSAS-FRONT前置服务部署说明

## 1. 依赖

| 序号 | 软件                       |
| ---- | -------------------------- |
| 1    | JAVA版本：openjdk8u382-b05 |
| 2    | WSBCS-CHDIS                |

## 2. 解压缩包

上传解压安装包WSAS-FRONT_0.1.0.tar.gz


## 3. 服务配置及启停
### 3.1 服务配置修改

- 修改服务配置`conf/application.yml`

```shell
...
spring:
  application:
    name: WSAS-FRONT
  cloud:
    gateway:
      httpclient:
        pool:
          type: elastic
          maxIdleTime: 2000
        connect-timeout: 5000
        response-timeout: 10s
      routes:
        - id: front_route
          # 配置WSBCS-CHDIS缓存服务IP端口
          uri: http://[@GLOBAL_WSBCSCHDIS_SERVER_VIP]
          # http://127.0.0.1:43781
...
```

### 3.2 服务启停

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

## 4. 查看日志

```shell
日志目录配置文件：./conf/log4j2.xml
全量日志：tail -f wsas-front.log
错误日志：tail -f error.log
```
