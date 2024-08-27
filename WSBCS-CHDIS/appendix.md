# 附录

## 1. 安装示例

### 1.1 Java部署

<span id="jdk"></span>

##### CentOS环境安装Java

<span id="centosjava"></span>

**注意：CentOS下OpenJDK无法正常工作，需要安装OracleJDK[下载链接](https://www.oracle.com/technetwork/java/javase/downloads/index.html)。**

```
# 创建新的文件夹，安装Java 8或以上的版本，将下载的jdk放在software目录
# 从Oracle官网(https://www.oracle.com/technetwork/java/javase/downloads/index.html)选择Java 8或以上的版本下载，例如下载jdk-8u201-linux-x64.tar.gz
$ mkdir /software

# 解压jdk
$ tar -zxvf jdk-8u201-linux-x64.tar.gz

# 配置Java环境，编辑/etc/profile文件
$ vim /etc/profile

# 打开以后将下面三句输入到文件里面并保存退出
export JAVA_HOME=/software/jdk-8u201  #这是一个文件目录，非文件
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar

# 生效profile
$ source /etc/profile

# 查询Java版本，出现的版本是自己下载的版本，则安装成功。
java -version
```

##### Ubuntu环境安装Java

<span id="ubuntujava"></span>

```
  # 安装默认Java版本(Java 8或以上)
  sudo apt install -y default-jdk
  # 查询Java版本
  java -version
```

<span id="gradle"></span>

### 1.2 Gradle部署

此处给出简单步骤，供快速查阅。更详细的步骤，请参考[官网](http://www.gradle.org/downloads)。

（1）从[官网](http://www.gradle.org/downloads)下载对应版本的Gradle安装包，并解压到相应目录

```shell
mkdir /software/
unzip -d /software/ gradleXXX.zip
```

（2）配置环境变量

```shell
export GRADLE_HOME=/software/gradle-4.9
export PATH=$GRADLE_HOME/bin:$PATH
```

（3）查看版本

```
gradle -version
```



## 2. 常见问题

<span id="q&a"></span>

### 2.1 脚本没权限

- 执行shell脚本报错误"permission denied"或格式错误

```
赋权限：chmod + *.sh
转格式：dos2unix *.sh
```

### 2.2 构建失败

- 执行构建命令`gradle build -x test`抛出异常：

```
A problem occurred evaluating root project 'WeBASE-Chain-Manager'.
Could not find method compileOnly() for arguments [[org.projectlombok:lombok:1.18.2]] on root project 'WeBASE-Chain-Manager'.
```

  答：

方法1、已安装的Gradle版本过低，升级Gradle版本到4.10以上即可
方法2、直接使用命令：`./gradlew build -x test`