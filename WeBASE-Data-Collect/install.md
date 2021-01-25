# 部署说明

## 1. 前提条件

| 序号 | 软件                |
| ---- | ------------------- |
| 1    | FISCO-BCOS feature-external-support版本 |
| 2    | WeBASE-Front feature-external-support版本 |
| 3    | MySQL5.6或以上版本  |
| 4    | Java8或以上版本     |
| 5 | Elasticsearch7.8.0及其对应elasticsearch-analysis-ik（分词插件），需要将交易数据存入Elasticsearch时需部署 |
| 6 | ZooKeeper-3.6.0或以上版本（多活场景需部署） |


## 2. 注意事项
* FISCO-BCOS和WeBASE-Front推荐使用docker部署，可以参考[使用说明](<https://github.com/WeBankFinTech/WeBASE-Docker/blob/bsn/docker/front-install.md>)，对应修改使用以下版本：

  ```
  docker pull fiscoorg/front:feature-external-support
  ```

* Java推荐使用[OracleJDK](https://www.oracle.com/technetwork/java/javase/downloads/index.html)，[JDK配置指引](./appendix.html#jdk)

*  安装说明可以参考 [安装示例](./appendix.md#install)

*  在服务搭建的过程中，如碰到问题，请查看 [常见问题解答](./appendix.md#q&a)

* 安全温馨提示： 强烈建议设置复杂的数据库登录密码，且严格控制数据操作的权限和网络策略


## 3. 拉取代码
执行命令：
```shell
git clone https://github.com/WeBankFinTech/WeBASE-Data.git -b bsn
```
进入目录：

```shell
cd WeBASE-Data/WeBASE-Data-Collect
```

## 4. 编译代码

方式一：如果服务器已安装Gradle，且版本为Gradle-4.10或以上

```shell
gradle build -x test
```

方式二：如果服务器未安装Gradle，或者版本不是Gradle-4.10或以上，使用gradlew编译

```shell
chmod +x ./gradlew && ./gradlew build -x test
```

构建完成后，会在根目录WeBASE-Data-Collect下生成已编译的代码目录dist。

## 5. 数据库初始化

### 5.1 新建数据库

```
#登录MySQL:
mysql -u ${your_db_account} -p${your_db_password}  例如：mysql -u root -p123456
#新建数据库：
CREATE DATABASE IF NOT EXISTS {your_db_name} DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
```

### 5.2 修改脚本配置

进入数据库脚本目录

```shell
cd  dist/script
```

修改数据库连接信息：

```shell
修改数据库名称：sed -i "s/webasedata/${your_db_name}/g" webase.sh
修改数据库用户名：sed -i "s/defaultAccount/${your_db_account}/g" webase.sh
修改数据库密码：sed -i "s/defaultPassword/${your_db_password}/g" webase.sh
```

例如：将数据库用户名修改为root，则执行：

```shell
sed -i "s/defaultAccount/root/g" webase.sh
```

### 5.3 运行数据库脚本

执行命令：bash  webase.sh  ${dbIP}  ${dbPort}
如：

```shell
bash webase.sh 127.0.0.1 3306
```

## 6. 服务配置及启停

### 6.1 服务配置修改
（1）回到dist目录，dist目录提供了一份配置模板conf_template：

```
根据配置模板生成一份实际配置conf。初次部署可直接拷贝。
例如：cp conf_template conf -r
```

（2）修改服务配置，完整配置项说明请查看 [配置说明](./appendix.md#application-yml)

- 服务端口，默认不修改。
- 数据库连接（数据库名需事先创建）。
- 默认保存区块、交易和gas信息。不保存则将对应数据设置成false。
- 需要需要将交易数据存入elasticsearch的话，需要将ifEsEnable设置成true，并配置IP端口和用户名密码。不使用则不需要修改。**使用elasticsearch的话，需先部署elasticsearch，再部署WeBASE-Data**。

```shell
...
server:
  port: 5009
  servlet:
    context-path: /WeBASE-Data-Collect

# database connection configuration
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://127.0.0.1:3306/webasedata?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull
    username: "defaultAccount"
    password: "defaultPassword"
  elasticsearch:
    rest:
      uris: 127.0.0.1:9200
      username: "elasticAccount"
      password: "elasticPassword"
# constants
constant:
  ## if save block and transaction info
  ifSaveBlockAndTrans: true
  ## if save gas info
  ifSaveGas: true
  ## if use elasticsearch
  ifEsEnable: false
...
```

### 6.2 服务启停
在dist目录下执行：
```shell
启动：bash start.sh
停止：bash stop.sh
检查：bash status.sh
```
**备注**：服务进程起来后，需通过日志确认是否正常启动，出现以下内容表示正常；如果服务出现异常，确认修改配置后，重启。如果提示服务进程在运行，则先执行stop.sh，再执行start.sh。

```
...
	Application() - main run success...
```

## 7. 访问

配置页面：

```
http://{deployIP}:{deployPort}/WeBASE-Data-Collect
示例：http://localhost:5009/WeBASE-Data-Collect
```

如果只调用接口，可以通过swagger查看调用接口：

```
http://{deployIP}:{deployPort}/WeBASE-Data-Collect/swagger-ui.html
示例：http://localhost:5009/WeBASE-Data-Collect/swagger-ui.html
```

**备注：** 

- 部署服务器IP和服务端口需对应修改，网络策略需开通

## 8. 查看日志

在dist目录查看：
```shell
全量日志：tail -f log/WeBASE-Data-Collect.log
错误日志：tail -f log/WeBASE-Data-Collect-error.log
```
