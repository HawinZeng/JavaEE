# 第二十五节  Nginx 及 Linux 开发

## 一、Linux系统上安装JDK

- #### 先检查本地是否有JDK版本，卸载OpenJDK

  ```java
  rpm -qa : 查看所有安装包
  rpm -qa | grep java 
  
  //  若有，先删除，因为默认JDK版本不友好
  rpm -e --nodeps java-1.7.0-openjdk..   
  ```

- #### 创建JDK的安装路径

  ```java
  // 一般安装在/usr/local/目录下
  mkdir java
  ```

- #### 上传安装文件到Linux

  ##### 1）在linux下一般用scp这个命令来通过ssh传输文件。

  ```java
  // 1. 从服务器上下载文件
  scp username@servername:/path/filename /var/www/local_dir（本地目录）
  
  // 2. 上传本地文件到服务器
  scp /path/filename username@servername:/path  
  
  // 3. 从服务器下载整个目录
  scp -r username@servername:/var/www/remote_dir/（远程目录） /var/www/local_dir（本地目录）
  
  // 4. 上传目录到服务器
  scp  -r local_dir username@servername:remote_dir
  
  //  例如
  scp -r /Volumes/C/toLinux root@120.79.53.76:/usr/local/ 
  ```

  ##### 2）使用第三方软件上传

- #### 安装前先检查本地是否安装JDK

  ```java
  rpm -qa | grep java   // 查找
  rpm -e --nodeps java_.... // 卸载
  ```

- #### 解压tar.gz

  ```
  // 依据Linux不同版本而定，有些不需要安装依赖，CentOS需要安装
  首先需要安装依赖：
  	 yum install glibc.i686
  然后解压：
  	tar -xvf jdk-7u71-linux-i586.tar.gz -C /usr/local/src/java 
  ```

- #### 配置环境变量：

  ```shell
  vim /etc/profile
  
  	#set java environment
  	JAVA_HOME=/usr/local/src/java/jdk1.7.0_75
  	CLASSPATH=.:$JAVA_HOME/lib.tools.jar
  	PATH=$JAVA_HOME/bin:$PATH
  	export JAVA_HOME CLASSPATH PATH
  
  重新加载配置文件：
  source /etc/profile
  ```


## 二、Linux系统上安装MySQL

####  第一步：上传mysql安装包 (CentOS 6)

#### 第二步：卸载自带mysql

```
rpm -qa | grep mysql  // 找到与mysql相关包
rpm -e --nodeps mysql-libs-5.1.73-5.e16_6.x86_64  // 删除
```

第三步：创建mysql安装路径

第四步：解压msyql

```shell
tar -xvf MySQL-5.6.25-1.el6.x86_64.rpm-bundle.tar -C /usr/local/mysql
```

第五步：安装依赖

```
yum -y install libaio.so.1 libgcc_s.so.1 libstdc++.so.6
// 若是64位系统，发现安装如下, 即32位依赖
  libaio.i686 0:0.3.109-13.el7               
  libgcc.i686 0:4.8.5-36.el7               
  libstdc++.i686 0:4.8.5-36.el7   
// 一定更新成64位依赖 
libaio-0.3.109-13.el7.x86_64
```

第六步：安装mysql的服务端

```java
rpm -ivh  MySQL-server-5.6.25-1.el6.x86_64.rpm

// 错误
file /usr/share/mysql/charsets/README from install of MySQL-server-5.6.25-1.el6.x86_64 conflicts with file from package mariadb-libs-1:5.5.52-1.el7.x86_64

// 原因：依赖包e17.x86_64 安装包el6.x86_64不兼容导致！！！！
```

```
// 安装后，提示password信息
A RANDOM PASSWORD HAS BEEN SET FOR THE MySQL root USER !
You will find that password in '/root/.mysql_secret'.
```

第七步：安装mysql的客户端

```
rpm -ivh MySQL-client-5.6.25-1.el6.x86_64.rpm 
```

 第八步：启动mysql

```
service mysql status // 查看启动情况
service mysql start // 启动
```

第九步：登录mysql

```
vim /root/.mysql_secret. // 查看默认密码

mysql -u root -p // 登录，先使用默认密码登录
set password = password('root'); // 重置密码
```

第十步：设置开机自动启动mysql

```
加入到系统服务：
chkconfig --add mysql

自动启动：
chkconfig mysql on
```

第十一步：开启远程服务

```
登录mysql:
grant all privileges on *.* to 'root' @'%' identified by '123456';
flush privileges;
```

第十二步：设置Linux的防火墙:

```
/sbin/iptables -I INPUT -p tcp --dport 3306 -j ACCEPT // 设置3306端口对外开放

/etc/rc.d/init.d/iptables save  // 保存防火墙设置
```



## 三、Linux系统上安装tomcat



## 四、发布项目到Linux：

#### 1. 首先，将数据库备份到Linux：

```shell
// 1. 进入mysql， 执行下面命令进行数据备份
sh-3.2# /usr/local/mysql/bin/mysqldump -u root -p travel > /Volumes/C/sql/travel.sql 

// 2. 然后将travel.sql 上传到Linux
 ~root# scp /Volumes/C/sql/travel.sql root@120.79.53.76:/usr/local/hawin
 
// 3. 进入Linux mysql 创建一个对应database travel, 再将sql数据导入
mysql> create database if not exists travel default character set utf8;
mysql> source /usr/local/hawin/travel.sql;
```

#### 2. 然后，将项目打成war包，上传到Linux中的tomcat

```
Intellij Build选项 －－ > Build Artifacts --> Build war/build项 
```





## 五、Nginx

### 5.1、概念：

```
Nginx (engine x) 是一个高性能的HTTP和反向代理服务，也是一个IMAP/POP3/SMTP服务。Nginx是由伊戈尔·赛索耶夫为俄罗斯访问量第二的Rambler.ru站点（俄文：Рамблер）开发的，第一个公开版本0.1.0发布于2004年10月4日。
其将源代码以类BSD许可证的形式发布，因它的稳定性、丰富的功能集、示例配置文件和低系统资源的消耗而闻名。2011年6月1日，nginx 1.0.4发布。
Nginx是一款轻量级的Web 服务器/反向代理服务器及电子邮件（IMAP/POP3）代理服务器，并在一个BSD-like 协议下发行。其特点是占有内存少，并发能力强，事实上nginx的并发能力确实在同类型的网页服务器中表现较好，中国大陆使用nginx网站用户有：百度、京东、新浪、网易、腾讯、淘宝等。
```

### 5.2、为什么使用Nginx:

```
背景:
互联网飞速发展的今天,大用户量高并发已经成为互联网的主体.怎样能让一个网站能够承载几万个或几十万个用户的持续访问呢？这是一些中小网站急需解决的问题。用单机tomcat搭建的网站，在比较理想的状态下能够承受的并发访问量在150到200左右。按照并发访问量占总用户数量的5%到10%这样计算，单点tomcat网站的用户人数在1500到4000左右。对于一个为全国范围提供服务的网站显然是不够用的，为了解决这个问题引入了负载均衡方法。负载均衡就是一个web服务器解决不了的问题可以通过多个web服务器来平均分担压力来解决，并发过来的请求被平均分配到多个后台web服务器来处理，这样压力就被分解开来。

负载均衡服务器分为两种:
一种是通过硬件实现的负载均衡服务器，简称硬负载例如：f5。
另一种是通过软件来实现的负载均衡，简称软负载:例如apache和nginx。

硬负载和软负载相比前者作用的网络层次比较多可以作用到socket接口的数据链路层对发出的请求进行分组转发但是价格成本比较贵，而软负载作用的层次在http协议层之上可以对http请求进行分组转发并且因为是开源的所以几乎是0成本，并且阿里巴巴，京东等电商网站使用的都是Nginx服务器。
```

### 5.3、使用Nginx完成负载均衡:

```
完成Nginx负载均衡,那么需要先来介绍Tomcat的安装和配置,我们首先要来配置Tomcat完成集群的配置.因为我们没有多台服务器运行Tomcat.那么我们可以模拟在一台服务器上运行多个Tomcat程序.
```

### 5.4、Nginx + 多台Tomcat集群配置：

```
n
```





Session共享：

- 第1种解决办法：一个用户进来以后只在tomcat1上进行操作，另一个用户进行只在tomcat2上进行操作.

  ```java
  upstream server_lb{
      server localhost:8080 weight=5;
      server localhost:8081 weight=10; // weight 权重比，权重大容易被访问到
      ip_hash; // 指定访问ip不变
  }
  ```

- 第2种 使用tomcat的广播机制完成session的共享。（不推荐）

  不推荐原因：tomcat集群的数量越多，性能越差！

  ```
  1. 修改两个tomcat中的server.xml:
  打开：
  ```

- 第3种 使用redis服务器的方式完成session的共享（推荐的方式）



## 六、Linux搭建Nginx＋Tomcat集群







