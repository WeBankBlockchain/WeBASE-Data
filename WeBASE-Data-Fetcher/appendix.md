# 附录

## 1. 安装示例

### 1.1 Java部署

此处给出OpenJDK安装简单步骤，供快速查阅。更详细的步骤，请参考[官网](https://openjdk.java.net/install/index.html)。

#### ① 安装包下载

从[官网](https://jdk.java.net/java-se-ri/11)下载对应版本的java安装包，并解压到服务器相关目录

```shell
mkdir /software
tar -zxvf openjdkXXX.tar.gz /software/
```

#### ② 配置环境变量

- 修改/etc/profile

```
sudo vi /etc/profile
```

- 在/etc/profile末尾添加以下信息

```shell
JAVA_HOME=/software/jdk-11
PATH=$PATH:$JAVA_HOME/bin
CLASSPATH==.:$JAVA_HOME/lib
export JAVA_HOME CLASSPATH PATH
```

- 重载/etc/profile

```
source /etc/profile
```

#### ③ 查看版本

```
java -version
```

### 1.2. 数据库部署

此处以Centos安装*MariaDB*为例。*MariaDB*数据库是 MySQL 的一个分支，主要由开源社区在维护，采用 GPL 授权许可。*MariaDB*完全兼容 MySQL，包括API和命令行。其他安装方式请参考[MySQL官网](https://dev.mysql.com/downloads/mysql/)。

#### ① 安装MariaDB

- 安装命令

```shell
sudo yum install -y mariadb*
```

- 启停

```shell
启动：sudo systemctl start mariadb.service
停止：sudo systemctl stop  mariadb.service
```

- 设置开机启动

```
sudo systemctl enable mariadb.service
```

- 初始化

```shell
执行以下命令：
sudo mysql_secure_installation
以下根据提示输入：
Enter current password for root (enter for none):<–初次运行直接回车
Set root password? [Y/n] <– 是否设置root用户密码，输入y并回车或直接回车
New password: <– 设置root用户的密码
Re-enter new password: <– 再输入一次你设置的密码
Remove anonymous users? [Y/n] <– 是否删除匿名用户，回车
Disallow root login remotely? [Y/n] <–是否禁止root远程登录，回车
Remove test database and access to it? [Y/n] <– 是否删除test数据库，回车
Reload privilege tables now? [Y/n] <– 是否重新加载权限表，回车
```

#### ② 授权访问和添加用户

- 使用root用户登录，密码为初始化设置的密码

```
mysql -uroot -p -h localhost -P 3306
```

- 授权root用户远程访问

```sql
mysql > GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '123456' WITH GRANT OPTION;
mysql > flush PRIVILEGES;
```

- 创建test用户并授权本地访问

```sql
mysql > GRANT ALL PRIVILEGES ON *.* TO 'test'@localhost IDENTIFIED BY '123456' WITH GRANT OPTION;
mysql > flush PRIVILEGES;
```

**安全温馨提示：**

- 例子中给出的数据库密码（123456）仅为样例，强烈建议设置成复杂密码
- 例子中root用户的远程授权设置会使数据库在所有网络上都可以访问，请按具体的网络拓扑和权限控制情况，设置网络和权限帐号

#### ③ 测试连接和创建数据库

- 登录数据库

```shell
mysql -utest -p123456 -h localhost -P 3306
```

- 创建数据库

```sql
mysql > create database webasedata;
```

### 1.3. Elasticsearch部署

此处以Centos安装为例。详情请查看[Elasticsearch官网](<https://www.elastic.co/cn/downloads/elasticsearch>)。

#### ① 安装包下载

下载[elasticsearch](<https://www.elastic.co/cn/downloads/elasticsearch>)和[elasticsearch-analysis-ik](<https://github.com/medcl/elasticsearch-analysis-ik/releases>)，注意版本要对应。

```shell
# 上传elasticsearch安装包并解压
tar -zxvf elasticsearch-7.8.0-linux-x86_64.tar.gz /software/
# 在elasticsearch的plugins目录创建子目录ik，并将ik分词插件上传解压到该目录
mkdir /software/elasticsearch-7.8.0/plugins/ik
# 进入目录
cd /software/elasticsearch-7.8.0/plugins/ik
# 上传ik分词插件安装包并解压
unzip elasticsearch-analysis-ik-7.8.0.zip
```

#### ② 启动

在 ES 根目录下面，执行启动脚本文件：

```
cd /software/elasticsearch-7.8.0
bin/elasticsearch -d
```

如果需要**设置用户名密码访问**，则进行以下操作：

1. 在配置文件中开启x-pack验证, 修改config目录下面的elasticsearch.yml文件，在里面添加如下内容，并**重启**

   ```
   xpack.security.enabled: true
   xpack.license.self_generated.type: basic
   xpack.security.transport.ssl.enabled: true
   ```

2. 设置用户名和密码，需要为4个用户分别设置密码（elastic，kibana，logstash_system，beats_system）

   ```
   bin/elasticsearch-setup-passwords interactive
   ```

3. 如果需要修改密码，命令如下：

   ```
   curl -H "Content-Type:application/json" -XPOST -u elastic 'http://127.0.0.1:9200/_xpack/security/user/elastic/_password' -d '{ "password" : "123456" }'
   ```

#### ③ 验证

打开浏览器，输入 <http://localhost:9200/> 地址，然后可以得到下面的信息：

```shell
{
  "name" : "node-1",
  "cluster_name" : "my-application",
  "cluster_uuid" : "K194HmUgRW2uwE9Zv0IDDQ",
  "version" : {
    "number" : "7.8.0",
    "build_flavor" : "default",
    "build_type" : "tar",
    "build_hash" : "757314695644ea9a1dc2fecd26d1a43856725e65",
    "build_date" : "2020-06-14T19:35:50.234439Z",
    "build_snapshot" : false,
    "lucene_version" : "8.5.1",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}
```

#### ④ 停止

查询进程并kill：

```
ps -ef|grep elasticsearch
kill -9 pid
```

## 2. 常见问题

### 2.1 脚本没权限

- 执行shell脚本报错误"permission denied"或格式错误

```
赋权限：chmod + *.sh
转格式：dos2unix *.sh
```

### 2.2 构建失败

- 执行构建命令`gradle build -x test`抛出异常：

```
A problem occurred evaluating root project 'WeBASE-Data-Fetcher'.
Could not find method compileOnly() for arguments [[org.projectlombok:lombok:1.18.2]] on root project 'WeBASE-Data-Fetcher'.
```

  答：

方法1、已安装的Gradle版本过低，升级Gradle版本到4.10以上即可
方法2、直接使用命令：`./gradlew build -x test`

### 2.3 数据库问题

- 服务访问数据库抛出异常：

```
The last packet sent successfully to the server was 0 milliseconds ago. The driver has not received any packets from the server.
```

答：检查数据库的网络策略是否开通

```
下面以centos7为例：
查看防火墙是否开放3306端口： firewall-cmd --query-port=3306/tcp
防火墙永久开放3306端口：firewall-cmd --zone=public --add-port=3306/tcp --permanent
重新启动防火墙：firewall-cmd --reload
```

- 执行数据库初始化脚本抛出异常：

```
ERROR 2003 (HY000): Can't connect to MySQL server on '127.0.0.1' (110)
```

答：MySQL没有开通该帐号的远程访问权限，登录MySQL，执行如下命令，其中TestUser改为你的帐号

```
GRANT ALL PRIVILEGES ON *.* TO 'TestUser'@'%' IDENTIFIED BY '此处为TestUser的密码’' WITH GRANT OPTION;
```

## 3. application.yml配置项说明

| 参数                                | 默认值                                 | 描述                                       |
| ----------------------------------- | -------------------------------------- | ------------------------------------------ |
| server.port                         | 5010                                   | 当前服务端口                               |
| server.servlet.context-path         | /WeBASE-Data-Fetcher                   | 当前服务访问目录                           |
| mybatis.typeAliasesPackage          | com.webank.webase.data.fetcher         | mapper类扫描路径                           |
| mybatis.mapperLocations             | classpath:mapper/*.xml                 | mybatis的xml路径                           |
| spring.datasource.driver-class-name | com.mysql.cj.jdbc.Driver               | mysql驱动                                  |
| spring.datasource.url               | jdbc:mysql://127.0.0.1:3306/webasedata | mysql连接地址                              |
| spring.datasource.username          | defaultAccount                         | mysql账号                                  |
| spring.datasource.password          | defaultPassword                        | mysql密码                                  |
| spring.elasticsearch.rest.uris      | 127.0.0.1:9200                         | elasticsearch服务的ip地址                  |
| spring.elasticsearch.rest.username  |                                        | elasticsearch用户名，可以为空              |
| spring.elasticsearch.rest.password  |                                        | elasticsearch密码，可以为空                |
| constant.keywordAuditCron           | 0 0 0/1 * * ?                          | 关键字审计任务执行时间，默认每小时执行一次 |
| executor.corePoolSize               | 50                                     | 线程池大小                                 |
| executor.maxPoolSize                | 100                                    | 线程池最大线程数                           |
| executor.queueSize                  | 50                                     | 线程池队列大小                             |
| executor.threadNamePrefix           | custom-async-                          | 线程名前缀                                 |
| logging.config                      | classpath:log/log4j2.xml               | 日志配置文件目录                           |
| logging.level                       | com.webank.webase.data.fetcher: info   | 日志扫描目录和级别                         |
