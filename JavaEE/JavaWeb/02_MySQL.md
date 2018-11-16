# 第二节 MySQL

## 一、数据库的概述

1. **数据存储：**

   - 内存：速度快，但是不能永久保存，只是临时数据状态；
   - 文件：可以永久保存数据，但是操作数据很困难；
   - 数据库：数据永久保存、查询快、方便管理数据；但是占用资源、需购买；

2. **概念：DataBase 简称 DB**

   - 定义：用于存储盒管理数据的仓库

3. **特点：**

   - 持久化存储数据的。其实数据库就是一个文件系统
   - 方便存储和管理数据
   - 使用了统一的方式操作数据库 -- SQL

4. **常见的数据库软件:**

   **Oracle  No.1  —— Oracle**

   **MySQL No.2  —— Oracle**

   SQL Server No.3 —— Microsoft

   ...

   **MongoDB : NoSQL** －－ 文件型数据库

   DB2 （IBM）主要应用于银行系统的大型数据库

   **Redis :**  －－ key－value型数据库

   SQLite：嵌入式的小型数据库,应用在手机端,如:Android。

（Mac password：root12345）

## 二、MySQL安装／卸载 ／配置

### 安装（略）

### 卸载：

1. 去mysql的安装目录找到my.ini文件
* 复制 datadir="C:/ProgramData/MySQL/MySQL Server 5.5/Data/"
2. 卸载MySQL
3. 删除C:/ProgramData目录下的MySQL文件夹。

```shell
Mac OS: 完全卸载MySQL:

sudo rm /usr/local/mysql
sudo rm -rf /usr/local/mysql*
sudo rm -rf /Library/StartupItems/MySQLCOM
sudo rm -rf /Library/PreferencePanes/My*
vim /etc/hostconfig  (and removed the line MYSQLCOM=-YES-)
rm -rf ~/Library/PreferencePanes/My*
sudo rm -rf /Library/Receipts/mysql*
sudo rm -rf /Library/Receipts/MySQL*
sudo rm -rf /var/db/receipts/com.mysql.*
```

> vim: shit+zz 保存退出  shift＋zq 不保存退出  shift＋q 放弃

### 配置

#### 1、MySQL服务启动

- 手动。
- cmd--> services.msc 打开服务的窗口
- 使用管理员打开cmd
  - net start mysql : 启动mysql的服务
  - net stop mysql:关闭mysql服务

#### 2、MySQL登录

- mysql -uroot -p密码

- mysql -hip -uroot -p连接目标的密码

- mysql --host=ip --user=root --password=连接目标的密码

#### 3、MySQL退出

- exit
- quit

#### 4、MySQL目录结构

- MySQL安装目录：basedir="D:/develop/MySQL/"

  - 配置文件 my.ini

- MySQL数据目录：datadir="C:/ProgramData/MySQL/MySQL Server 5.5/Data/"

  几个概念
  * 数据库：相当于文件夹
  * 表：相当于文件
  * 数据：相当于数据



## 三、SQL语句

### 1、什么是SQL？

Structured Query Language：结构化查询语言。其实就是定义了操作所有关系型数据库的规则。

每一种数据库操作的方式存在不一样的地方，称为“方言”。

### 2、SQL通用语法

- SQL 语句可以单行或多行书写，以分号结尾。

- 可使用空格和缩进来增强语句的可读性。

- MySQL 数据库的 SQL 语句不区分大小写，关键字建议使用大写。

- 3 种注释
  - 单行注释: -- 注释内容 或 
  - \# 注释内容(mysql 特有) 
  - 多行注释: /* 注释 */

### 3、SQL分类

- DDL(Data Definition Language)数据定义语言
  用来定义数据库对象：数据库，表，列等。关键字：create, drop,alter 等
- DML(Data Manipulation Language)数据操作语言
  用来对数据库中表的数据进行增删改。关键字：insert, delete, update 等
- DQL(Data Query Language)数据查询语言
  用来查询数据库中表的记录(数据)。关键字：select, where 等
- DCL(Data Control Language)数据控制语言(了解)
  用来定义数据库的访问权限和安全级别，及创建用户。关键字：GRANT， REVOKE 等

### 4、DDL SQL：操作数据库、表

#### 4.1、操作数据库：CRUD+Use

- C(Create): 创建

  - 创建数据库：

  ```sql
  create database dbname；
  ```

  - 创建时判断：

  ```sql
  create database if not exists dbname;
  ```

  - 创建时判断指定字符集：

  ```sql
  create database if not exists dbname character set gbk;
  ```

- R(Retrieve): 查询

  - 查询所有数据库的名称: 

  ```
  show databases;
  ```

  - 查询某个数据库的字符集:查询某个数据库的创建语句

  ```sql
  show create database 数据库名称;
  ```

- U(Update): 修改数据库的字符集

  ```sql
  alter database 数据库名称 character set 字符集名称; // utf8;gbk;
  ```

- D(Delete): 删除

  - 删除数据库

  ```sql
  drop database 数据库名称;
  ```

  - 判断数据库存在，存在再删除

  ```sql
  drop database if exists 数据库名称;
  ```

- 使用数据库

  - 查询当前正在使用的数据库名称

  ```sql
  select database();
  ```

  - 使用数据库

  ```sql
  use 数据库名称;
  ```

> 当在DOS／终端，没有以；结束就回车了，会出现箭头符号，直接；即可结束箭头！！！



#### 4.2、操作表：CRUD

- ##### C(Create)：创建

```sql
create table 表名(
				列名1 数据类型1,
				列名2 数据类型2,
				....
				列名n 数据类型n
			);
			* 注意：最后一列，不需要加逗号（,）
			* 数据库类型：
				1. int：整数类型
					* age int,
				2. double:小数类型
					* score double(5,2) -- //最大5位有效数字，保留2位小数位；
				3. date:日期，只包含年月日，yyyy-MM-dd
				4. datetime:日期，包含年月日时分秒	 yyyy-MM-dd HH:mm:ss
				5. timestamp:时间错类型	包含年月日时分秒	 yyyy-MM-dd HH:mm:ss	
					* 如果将来不给这个字段赋值，或赋值为null，则默认使用当前的系统时间，来自动赋值

				6. varchar：字符串
					* name varchar(20):姓名最大20个字符
					* zhangsan 8个字符  张三 2个字符
```

```sql
create table student(
    id int,
    name varchar(32),
    age int,
    score double(4,1),
    birthday date,
    insert_time timestamp
);
```

```sql
create table 表名 like 被复制的表名;	// 复制表：
```

- ##### R(Retrieve)：查询

```sql
show tables; //查询某个数据库中所有的表名称
desc 表名; //查询表结构
```

- ##### U(Update)：修改

```sql
// 1. 修改表名
alter table 表名 rename to 新的表名; 

// 2. 修改表的字符集
alter table 表名 character set 字符集名称; 

// 3. 添加一列
alter table 表名 add 列名 数据类型; 

// 4. 修改列名称
alter table 表名 change 列名 新列别 新数据类型; 

// 5. 修改列类型
alter table 表名 modify 列名 新数据类型;

// 6. 删除列
alter table 表名 drop 列名;
```

- #####  D(Delete)：删除

```sql
drop table 表名;
drop table  if exists 表名 ;
```



