# 同一服务器部署多个web站点详解

## 一、服务器上的web站点：3种搭建方式

- ##### 基于IP地址：（一对一的方式）

  ```
  需要为每个虚拟主机使用不同的域名，且各自对应的IP地址也不相同。这种方式需要为服务器配备多个网络接口，因此应用并不是非常广泛；
  ```

- ##### 基于端口号：

  ```
  使用不同的TCP端口号来区分不同的站点内容，但是用户在浏览不同的虚拟站点时需要同时指定端口号才能够访问；
  ```

- ##### 基于主机名（host）：

  ```
  每个虚拟主机使用不同的域名，但是其对应的IP地址是相同的；(应用最广)
  ```



## 二、linux下用Apache一个IP多个网站

### 2.1、部署服务器

#### 安装Apache服务器（httpd）

#### 1. 检查并安装httpd服务

> ```java
> [root@localhost ~]# rpm -q httpd //检查是否已安装httpd
> httpd-2.2.15-29.el6_4.x86_64
> [root@localhost ~]# rpm -ivh /mnt/cdrom/Packages/httpd-2.2.15-29.el6_4.x86_64.rpm
> ```

#### 2. 关闭安全设置和防火墙

> ```java
> setenforce 0
> service iptables stop
> ```



### 2.2、虚拟目录(用户授权限制)

#### 1. 编辑httpd.conf配置文件

> ```java
> vim /etc/httpd/conf/httpd.conf
> 
> ServerName www.yum01.com:80 //设置主机名
> Listen 192.168.100.5:80 //设置监听IP地址
> Include conf.d/*.conf //开启加载conf.d目录下以.conf为后缀的配置文件
> ```

#### 2. 在/etc/httpd/conf.d/目录下新建vdir.conf（虚拟目录文件）

> ```java
> cd /etc/httpd/conf.d/
> vim vdir.conf                       //新建vdir.conf配置文件
> 
> //以下为虚拟目录定义配置文件
> Alias /bbs "/opt/bbs/" //设置别名
> <Directory "/opt/bbs/"> //定义虚拟目录
> Options Indexes MultiViews FollowSymLinks
> AllowOverride None
> authname "Auth Directory" //认证领域的名称
> authtype basic //基本认证方式
> authuserfile /etc/httpd/user //基本认证用户账户、密码的认证文件路径
> require Valid-user //授权用户可以进行访问
> </Directory>
> ```

#### 3. 创建/opt/bbs目录，并新建bbs站点首页

> ```java
> mkdir /opt/bbs
> echo "<h1>This is bbs web.</h1>" > /opt/bbs/index.html
> ```

#### 4. 创建用户认证数据文件

> ```java
> htpasswd -c /etc/httpd/conf.d/user zhangsan
> ```

#### 5. 重启httpd服务

> ```java
> service httpd restart
> 
> windows 下测试ping 192.168.100.5
> ```

#### 6. 访问虚拟目录站点：

> ```java
> 浏览器输入：192.168.100.5/bbs/
> 
> 提示认证：输入帐号密码
> 即可访问虚拟目录下的资源
> ```



### 2.3、多个网站配置

- #### 基于IP地址：主要在于硬件上，待续

- ### 基于端口：

  ##### 1. 在/etc/httpd/conf.d/目录下新建vport.conf（虚拟目录文件）

  > ```java
  > cd /etc/httpd/conf.d/
  > vim vport.conf                       //新建vport.conf配置文件
  >     
  > NameVirtualHost 192.168.100.5:80 //虚拟主机名称
  > <VirtualHost 192.168.100.5:80>
  > ServerAdmin admin@yun01.com //管理员邮箱
  > DocumentRoot /opt/yun01/ //网站站点目录
  > ServerName www.yun01.com //域名
  > ErrorLog logs/yun01.com-error_log //错误日志
  > CustomLog logs/yun01.com-access_log common //访问日志
  > </VirtualHost>
  > NameVirtualHost 192.168.100.5:81 //虚拟主机名称
  > <VirtualHost 192.168.100.5:81>
  > ServerAdmin admin@yun02.com //管理员邮箱
  > DocumentRoot /opt/yun02/ //网站站点目录
  > ServerName www.yun02.com //域名
  > ErrorLog logs/yun02.com-error_log //错误日志
  > CustomLog logs/yun02.com-access_log common //访问日志
  > </VirtualHost>
  > ```

  ##### 2. 创建/opt/yun01、/opt/yun02站点目录，并新建各站点首页文件（index.html）

  > ```java
  > mkdir /opt/yun01 /opt/yun02 //创建站点目录
  > echo "<h1>this is yun01.com web.</h1>" > /opt/yun01/index.html //站点yun01添加index.html文件
  > echo "<h1>this is yun02.com web.</h1>" > /opt/yun02/index.html //站点yun02添加index.html文件
  > ```

  ##### 3. 修改httpd.conf配置文件

  > ```java
  > vim /etc/httpd/conf/httpd.conf
  > 
  > Listen 192.168.100.5:81 //添加81端口的监听地址
  > ```

  ##### 4. 重启httpd服务

  > ```java
  > service httpd restart
  > ```

- ### 基于主机名【常用】

  ##### 1. 在/etc/httpd/conf.d/目录下新建vhost.conf（虚拟目录文件）

  > ```java
  > cd /etc/httpd/conf.d/
  > vim vhost.conf                       //新建vhost.conf配置文件
  >     
  > NameVirtualHost 192.168.100.5:80 //虚拟主机名称
  > <VirtualHost 192.168.100.5:80>
  > ServerAdmin admin@yun03.com //管理员邮箱
  > DocumentRoot /opt/yun03/ //网站站点目录
  > ServerName www.yun03.com //域名
  > ErrorLog logs/yun03.com-error_log //错误日志
  > CustomLog logs/yun03.com-access_log common //访问日志
  > </VirtualHost>
  > NameVirtualHost 192.168.100.5:80 //虚拟主机名称
  > <VirtualHost 192.168.100.5:80>
  > ServerAdmin admin@yun04.com //管理员邮箱
  > DocumentRoot /opt/yun04/ //网站站点目录
  > ServerName www.yun04.com //域名
  > ErrorLog logs/yun04.com-error_log //错误日志
  > CustomLog logs/yun04.com-access_log common //访问日志
  > </VirtualHost>
  > ```

  ##### 2. 创建/opt/yun03、/opt/yun04站点目录，并新建各站点首页文件（index.html）

  > ```java
  > mkdir /opt/yun03 /opt/yun04 //创建站点目录
  > echo "<h1>this is yun03.com web.</h1>" > /opt/yun03/index.html //站点yun03添加index.html文件
  > echo "<h1>this is yun04.com web.</h1>" > /opt/yun04/index.html //站点yun04添加index.html文件
  > ```

  #### 3. 安装DNS服务器

  3.1 安装bind软件包

  > ```java
  > rpm -ivh /mnt/cdrom/Packages/bind-9.8.2-0.17.rc1.el6_4.6.x86_64.rpm
  > ```

  3.2 编辑主配置文件

  > ```java
  >  vim /etc/named.conf
  >  
  >  options {
  > listen-on port 53 { 192.168.100.5; }; //修改指定监听IP
  > listen-on-v6 port 53 { ::1; };
  > directory "/var/named";
  > dump-file "/var/named/data/cache_dump.db";
  > statistics-file "/var/named/data/named_stats.txt";
  > memstatistics-file "/var/named/data/named_mem_stats.txt";
  > allow-query { any; }; //允许any（所有人）访问
  > recursion yes;
  > dnssec-enable yes;
  > dnssec-validation yes;
  > dnssec-lookaside auto;
  > / Path to ISC DLV key /
  > bindkeys-file "/etc/named.iscdlv.key";
  > managed-keys-directory "/var/named/dynamic";
  > };
  > ```

  3.3 编辑区域配置文件

  > ```java
  > vim /etc/named.rfc1912.zones
  > 
  > zone "yun03.com" IN {
  > type master;
  > file "yun03.com.zone";
  > };
  > zone "yun04.com" IN {
  > type master;
  > file "yun04.com.zone";
  > };
  > ```

  3.4 切换到/var/named目录下

  > ```java
  > cd /var/named
  > ```

  3.5 复制模板文件

  > ```java
  > cp -p named.localhost yun03.com.zone
  > ```

  3.6 编辑区域数据配置文件

  > ```java
  > vim yun03.com.zone
  > 
  > //配置一条信息
  > www   IN   A  192.168.100.5
  >     
  > // (由于yun04.com和yun03.com解析文件都一样，这里直接复制yun03.com.zone文件)  
  > cp -p yun03.com.zone yun04.com.zone    
  > ```

  3.7 加入named服务并重启该服务

  > ```java
  > chkconfig named on
  > service named restart //重启named服务
  > service httpd restart //重启httpd服务
  > ```








