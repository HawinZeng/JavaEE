# 启动多个Tomcat 需要修改的端口

不知道朋友们 有没有遇到过 在一台机器 上启动多个Tomcat 的情况（别跟我说启动一个，里面多个项目就可以，这个我知道 ）。

启动时会有商品冲突，需要修改Tomcat 的端口： 我用的是zip 版的 直接 解压

一共有3个：

修改

%TOMCAT_HOME%\conf下的 server.xml

注意：修改后的端口一定要使用命令查看下该端口是否被占用

lsof -i:8080

netstat -anp|grep 8080



#### 第一个： 修改http访问端口（默认为8080端口）

```xml
 <Connector port="8080" protocol="HTTP/1.1"   
           connectionTimeout="20000"   
           redirectPort="8443" />  
```

（大约69行左右）将8080修改为第一个tomcat不在使用的端口号。此处所设的端口号即是以后访问web时所用的端口号。

 

#### 第二个：修改Shutdown端口（默认为8005端口）

```xml
<Server port="8005" shutdown="SHUTDOWN">  
```

（大概在22行左右）将8005修改为没有在使用的端口号

 

#### 第三个：修改8009端口

```XML
<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />  
```

（大概在90行左右）将8009修改为没有在使用的端口号，例如8099