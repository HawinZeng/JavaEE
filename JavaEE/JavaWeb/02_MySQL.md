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
- 使用管理员打开cmd ----(**启动DOS命令不需要；结束，并非sql语句**)
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

```
MAC OS 终端进入MySQL步骤：
先在偏好设置里启动mysql服务
获取超级权限
在终端输入代码
sudo su
输入完后获取超级权限 终端显示 sh-3.2#
输入本机密码（Apple ID密码）
接着通过绝对路径登陆 代码
/usr/local/mysql/bin/mysql -u root -p
再输入mysql密码（我的密码设置为root）
登陆成功
退出代码
quit
退出成功 bye
最后退出sh-3.2#超级权限 代码
exit
```



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

### 4.1、操作数据库：CRUD+Use

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



### 4.2、操作表：CRUD

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
create table 表名 like 被复制的表名;	// 复制表：但是不会复制表中的数据；
```

- ##### R(Retrieve)：查询

```sql
show tables; //查询某个数据库中所有的表名称
desc 表名; //查询表结构
show create table stu; // 查询表创建时的信息
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

- 客户端图形化工具：SQLYog (Mac)

### 4.3、DML：增删改【表】中数据

- ##### 添加数据：

```sql
insert into 表名(列名1,列名2,...列名n) values(值1,值2,...值n);
```

> 注意：
>
> 1. 列名和值要一一对应。
> 2. 如果表名后，不定义列名，则默认给所有列添加值
>
> 		insert into 表名 values(值1,值2,...值n);
>
> 3. 除了数字类型，其他类型需要使用引号(单双都可以)引起来

- ##### 删除数据：

```sql
delete from 表名 [where 条件]
```

> 注意：
>
> 1. 如果不加条件，则删除表中所有记录。
> 2. 如果要删除所有记录
>    - delete from 表名; -- 不推荐使用。有多少条记录就会执行多少次删除操作
>
>    - TRUNCATE TABLE 表名; -- 推荐使用，效率更高 先删除表，然后再创建一张一样的表。 
>
>      -- **此法若有外键关联，执行无效；**

- ##### 修改数据：

```sql
update 表名 set 列名1 = 值1, 列名2 = 值2,... [where 条件];
```

> 注意： 如果不加任何条件，则会将表中所有记录全部修改。

### 4.3、DQL：查询表中的记录

```sql
最基本的查询： select * from 表名;

1. 语法：
	select
		字段列表
	from
		表名列表
	where
		条件列表
	group by
		分组字段
	having
		分组之后的条件
	order by
		排序
	limit
		分页限定
```

- #### 基础查询

  ```sql
  -- 1. 多个字段的查询 --
  select 字段名1，字段名2... from 表名；
  -- 注意：如果查询所有字段，则可以使用*来替代字段列表；
  ```

  ```sql
  -- 2. 去除重复：--
  distinct
  select distinct address from student;
  ```

  ```sql
  -- 3. 计算列 --
  select name,math,english,math+english from student;
  -- null参与的运算，计算结果都为null。所以要使用ifnull函数替换null
  select naem,math,english,math+ifnull(english,0) from student;
  -- 重命名 as 可以省略
  SELECT NAME,math,english,IFNULL(math,0)+IFNULL(english,0) AS total FROM stu;
  ```

- #### 条件查询--where子句后跟条件

  - #### **运算符**

  ```sql
  	* > 、< 、<= 、>= 、= 、[<>、!=]不等于
  	* BETWEEN...AND  // 闭区间
  	SELECT * FROM stu WHERE age >=20 AND age <= 40;
  	SELECT * FROM stu WHERE age BETWEEN 20 AND 40;
  	
  	* IN( 集合) 
  	SELECT * FROM stu WHERE age=12 OR age=20 OR age=45;
  	SELECT * FROM stu WHERE age IN (12,20,28);
  	
  	* and  或 &&
  	* or  或 || 
  	* not  或 !
  	
  	* IS NULL  
  	SELECT * FROM stu WHERE english IS NOT NULL;
  	
  	
  	* LIKE：模糊查询
  		* 占位符：
  			* _:单个任意字符
  			* %：多个任意字符
  	
  ```

  - #### 排序查询

  ```sql
  1. order by 子句
  2. order by 排序字段1 排序方式1 ，  排序字段2 排序方式2...
  
  	* 排序方式：
  		* ASC：升序，默认的。
  		* DESC：降序。
  
  	* 注意：
  		* 如果有多个排序条件，则当前边的条件值一样时，才会判断第二条件。
  		
  SELECT math,english FROM stu ORDER BY math ASC, english DESC;
  ```

  - #### 聚合函数：将一列数据作为一个整体，进行［纵向］的计算。

  ```sql
  1. count：计算个数
  		1. 一般选择非空的列：主键
  		2. count(*)
  		
  select count(english) from stu0;
  select count(*) from stu0;
  
  2. max：计算最大值
  3. min：计算最小值
  4. sum：计算和
  5. avg：计算平均值
  select max(math) from stu0;
  
  注意：聚合函数的计算，排除null值。
  		解决方案：
  			1. 选择不包含非空的列进行计算
  			2. IFNULL函数
  ```

  - #### 分组查询:

  ```sql
  group by 分组字段；
  
  注意：
  	1. 分组之后查询的字段：［分组字段、聚合函数］
  	select sex,avg(math) from stu group by sex;
  	
  	2. where 和 having 的区别？
  		2.1. where 在分组之前进行限定，如果不满足条件，则不参与分组。having在分组之后进行限定，如果不满足结果，则不会被查询出来
  		2.2. where 后不可以跟聚合函数，having可以进行聚合函数的判断。
  select sex,COUNT(id) ct,avg(math) from stu where math >=70 GROUP BY sex HAVING ct > 5;
  ```

  - #### 分页查询

  ```sql
  limit 开始的索引,每页查询的条数;
  
   公式：开始的索引 = （当前的页码 - 1） * 每页显示的条数
  		-- 每页显示3条记录 
  
  		SELECT * FROM student LIMIT 0,3; -- 第1页
  		
  		SELECT * FROM student LIMIT 3,3; -- 第2页
  		
  		SELECT * FROM student LIMIT 6,3; -- 第3页
  
   limit 是一个MySQL"方言",只能在MySQL使用；
  
  ```



```sql
create database if not exists ssm default character set utf8;
use ssm_web;
create table account(
	id int primary key auto_increment,
	name varchar(30) not null,
	money double default null
) Engine=InnoDB default charset=utf8;

insert into stu values( 2,'张无忌',25,'成都',89,92),( 3,'赵敏',24,'北京',95,98),
( 4,'张三丰',88,'成都',97,92),( 5,'郭靖',45,'郑州',90,82),(6,'黄蓉',42,'广州',99,100),
( 7,'杨过',26,'郑州',92,93),( 8,'小龙女',30,'西安',98,99),(9,'黄药师',66,'广州',95,96),
(10,'杨康',44,'西安',78,65),(11,'欧阳锋',60,'西安',34,58),(12,'老顽童',58,'郑州',23,36),
( 13,'龙哥',35,'上海',69,72),( 14,'徐子龙',15,'上海',88,80);
```
