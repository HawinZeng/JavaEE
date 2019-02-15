# Oracle_01

## 一、Oracle体系结构[理解]

#### 1.1、 数据库

```properties
	Oracle 数据库是数据的物理存储。这就包括(数据文件 ORA 或者 DBF、控制文件、联机日 志、参数文件)。其实 Oracle 数据库的概念和其它数据库不一样,这里的数据库是一个操作系统 只有一个库。可以看作是 Oracle 就只有一个大数据库。
	区分mysql：
	1. mysql是包含了多个数据库，相当于是一个数据库集合；
	2. Oracle却只有一个数据库，无需人为创建；
```

#### 1.2、实例 （这是一个真实的应用的）

```properties
	一个 Oracle 实例(Oracle Instance)有一系列的后台进程(Backguound Processes)和内存结构
(Memory Structures)组成。一个数据库可以有 n 个实例。
```

> #### oracle里实例和数据库之间的关系：
>
> ```javascript
> 	1. 一个数据库服务器上可以装几个数据库它们都是用sid来标志，例如orcl1,orcl2,orcl3等等，一个数据库如orcl1中可以有多个实例!
>         
>     2. Oracle数据库，实际上应该是说，我们使用的是Oracle服务server。 
> 
> 	3. Oracle服务server包括有两个部分：
>     	一个部分是Oracle数据库database ；
>         一个部分是数据库实例instance。 
>         
>     4. 这两个词有时可互换使用，不过二者的概念完全不同。实例和数据库之间的关系是：数据库可以由多个实例装载和打开，而实例可以在任何时间点装载和打开一个数据库。实际上，准确地讲，实例在其整个生存期中最多能装载和打开一个数据库！
>     
>     5. Oracle数据库database，包括有数据文件、控制文件、重做日志文件，都是一些物理上的文件。
>     
>     6. 数据库实例instance，包括有数据库后台进程（PMON、SMON、DBWR、LGWR、CKPT等）和内存区域SGA（包括shared pool、db buffer cache、redo log buffer等）。实例是一系列复杂的内存结构和操作系统进程；
>     
>     7. 数据库与实例之间是1对1/n的关系，在非并行的数据库系统中每个Oracle数据库与一个实例相对应；在并行的数据库系统中，一个数据库会对应多个实例，同一时间用户只与一个实例相联系，当某一个实例出现故障时，其他实例自动服务，保证数据库正常运行。在任何情况下，每个实例都只可以对应一个数据库。
> ```

#### 1.3、用户

```properties
用户是在实例下建立的。不同实例可以建相同名字的用户。
```

#### 1.4、表空间 (tablespace)

```properties
	表空间是 Oracle 对物理数据库上相关数据文件(ORA 或者 DBF 文件)的逻辑映射。 ---- 相当于物理数据库文件与实例关联的纽带或逻辑关联框！这也是为啥，不能直接删除关联的数据文件，直接删除会提示被一个应用正在调用中。若要删除，则必须先删除表空间。

	ORACLE数据库被划分成称作为表空间的逻辑区域——形成ORACLE数据库的逻辑结构。一个ORACLE数据库能够有一个或多个表空间,而一个表空间则对应着一个或多个物理的数据库文件。表空间是ORACLE数据库恢复的最小单位,容纳着许多数据库实体,如表、视图、索引、聚簇、回退段和临时段等。
	
	每个数据库至少有一个表空间(称之为 system 表空间)。
	
	每个表空间由同一磁盘上的一个或多个文件组成,这些文件叫数据文件(datafile)。一个数据文件只能属于一个表空间。
```

#### 1.5、 数据文件(dbf、ora)

```properties
	数据文件是数据库的物理存储单位。数据库的数据是存储在表空间中的,真正是在某一个 或者多个数据文件中。而一个表空间可以由一个或多个数据文件组成,一个数据文件只能属于 一个表空间。一旦数据文件被加入到某个表空间后,就不能删除这个文件,如果要删除某个数 据文件,只能删除其所属于的表空间才行。
	
	注：表的数据,是有用户放入某一个表空间的,而这个表空间会随机把这些表数据放到 一个或者多个数据文件中。
```

```properties
	由于 oracle 的数据库不是普通的概念,oracle 是有用户和表空间对数据进行管理和存放的。 但是表不是有表空间去查询的,而是由用户去查的。因为不同用户可以在同一个表空间建立同 一个名字的表!这里区分就是用户了!
```



#### 1.6、Oracle 初始设置 / 问题解决 

```sql
-- 1. 最初始的账号／密码： 
	system、sys,sysman,dbsnmp 初始化账户
	密码: 就是你安装数据库时，设置的密码，如 orcl。

-- 2. 在cmd终端进入Oracle命令：
-- 2.1 直接sqlplus登录oracle实例
sqlplus system/orcl@[192.168.186.141:1521/]orcl [as sysdba];
sqlplus system/orcl@orcl as sysdba; -- 连接本地oracle
-- 2.2 先进oracle，再连接实例
sqlplus /nolog; 
SQL> conn system/orcl{@[192.168.186.141:1521]/orcl} [as sysdba]

-- 3. 进入oracle后，查看实例是否启动
SQL> select status from v$instance;
--- 如下结果：代表已启动 ----         ---- 如下结果：代表没有连接，需要先用户登录连接----
STATUS       					   SP2-0640:not connected
------------
OPEN

-- 4. 登录时，错误监--> (听程序当前无法识别连接描述符中请求的服务)
ORA-12514:listener does not currently know of service requested in connect descriptor;
--分析：
-- 4.1 这个错误，当OracleServiceORCL服务关闭时，也是必然提示的。这时，我们启动服务即可；
	
-- 4.2 关键是OracleServiceORCL服务正常，依然报错！
	-- 1) 检查$ORACLE_BASE/diag/tnslsnr/DB-Server/listener/alert下的日志log.xml (Linux,windows下搜索alert即可).
		iP地址不一致，修正即可;
		实例名称设置有误，也修正即可；
		其他情况阅读具体内容修正；
	-- 2) 检查网络是否正常： ping 公网或自身ip， tnsping orcl(数据库实例)。
		ping 192.168.186.141;
		tnsping orcl;
	-- 3) 检查监听服务是否正常： lsnrctl status
	...
	> instance 'PLSEExtProc', status UNKNOWN, has 1 handler(s) for this service...
    > The command command completed successfully.
    从上面输出看，实例'PLSEExtProc'被监听到了。那为啥我们是用orcl实例呢？
    	-------- # tnsnames.ora： 监听器的配置文件--------
            ORCL =
              (DESCRIPTION =
                (ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.186.141)(PORT = 1521))
                (CONNECT_DATA =
                  (SERVER = DEDICATED)
                  (SERVICE_NAME = orcl)
                )
              )

            EXTPROC_CONNECTION_DATA =
              (DESCRIPTION =
                (ADDRESS_LIST =
                  (ADDRESS = (PROTOCOL = IPC)(KEY = EXTPROC1))
                )
                (CONNECT_DATA =
                  (SID = PLSExtProc)
                  (PRESENTATION = RO)
                )
              )
-------------------------listener.ora : 监听器-----------------------------------       
            SID_LIST_LISTENER =
              (SID_LIST =
                (SID_DESC =
                  (SID_NAME = PLSExtProc)
                  (ORACLE_HOME = C:\oracle\product\10.2.0\db_1)
                  (PROGRAM = extproc)
                )
              )

            LISTENER =
              (DESCRIPTION_LIST =
                (DESCRIPTION =
                  (ADDRESS = (PROTOCOL = IPC)(KEY = EXTPROC1))
                  (ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.186.141)(PORT = 1521))
                )
              )
----------------------------------------------------------------------------------------- 
  从上看，监听器的配置文件将实例'PLSExtProc' 的TCP协议连接名称修改为SERVICE_NAME = orcl。同时在最外层也有个别名ORCL，方便在PL/SQL登录时选择；
  
  正常情况： lsnrctl status (查看是有orcl的实例)
  Services Summary...
	Service "PLSExtProc" has 1 instance(s).
  		Instance "PLSExtProc", status UNKNOWN, has 1 handler(s) for this service..
	Service "orcl" has 1 instance(s).
  		Instance "orcl", status READY, has 1 handler(s) for this service...
	Service "orclXDB" has 1 instance(s).
  		Instance "orcl", status READY, has 1 handler(s) for this service...
	Service "orcl_XPT" has 1 instance(s).
  		Instance "orcl", status READY, has 1 handler(s) for this service...
	The command completed successfully
    
```

> ##### 总结：
>
> ##### 1、下次有机会要好好解决下！ 能够tnsping orcl ，但是 lsnrctl status 抓不到orcl！
>
> ##### 2、listener.ora 、tnsnames.ora都可以放在C:\instantclient_12_1\config目录下。config文件夹自创建。当此文件夹有这两个文件后，oracle重装的C:\oracle\product\10.2.0\db_1\NETWORK\ADMIN目录将不在有此两个文件。

### 1.7、错误统计／解决

1） PL/SQL 登录Oracle时，会弹出 `using a filter for all users can lead to poor performace`: 

```
1. 分析：与Oracle的配置无关，在使用plsql左侧的树形目录时候会看到非常多的和当前工作无关的表、视图、序列等，导致打开速度非常慢；

2. 具体解决办法：Tools-->Object browser filter-->选中“my objects”，default-->“确定”，退出再登录；此时只有自己创建的tables，functions，tiggers等；
```

2）语法不起作用：

```sql
CREATE TABLE product(
  id varchar2(32) default SYS_GUID() PRIMARY KEY,
  productNum VARCHAR2(50) NOT NULL,
  productName VARCHAR2(50),
  cityName VARCHAR2(50),
  DepartureTime timestamp,
  productPrice Number,
  productDesc VARCHAR2(500),
  productStatus INT,
  CONSTRAINT product UNIQUE (id, productNum)
);
-- 上面的SYS_GUID() ，CONSTRAINT product UNIQUE (id, productNum)在一些情况下根本不起作用。为啥？由于我先在ssm用户下创建了product，ssm账户表空间没有关联具体的物理数据文件，导致实际开发无法连接ssm账户对应的product表。于是，重新创建了表空间itcast，然后再创建用户itheima关联表itcast。 此时再用 create table product as select * from ssm.product; 相当于复制表，这种情况下是无法复制 部分属性的，如SYS_GUID() ，CONSTRAINT..UNIQUE等；
```





## 二、创建表空间[理解]

```sql
-- 表空间? ORACLE 数据库的逻辑单元。 数据库---表空间 一个表空间可以与多个数据文件(物理结构)关联 一个数据库下可以建立多个表空间,一个表空间可以建立多个用户、一个用户下可以建立多个表。

create tablespace itcast -- 创建表空间，名称为itcast
datafile 'c:\itcast.dbf' -- 指定表空间对应的数据文件
size 100m -- 定义表空间的初始化大小
autoextend on -- 自动增长，当空间存储都占满时，会自动增长
next 10m -- 每次自动增长的大小
```

> mysql：讲的是哪个数据库db下的表；
>
> oracle：讲的是哪个用户下的表；



## 三、用户[理解]

#### 3.1、创建用户

```sql
create user itcastuser -- 创建用户，设置名称
identified by itcast -- 设置用户的密码
default tablespace itcast -- 关联的表空间
```

#### 3.2、用户赋权限

Oracle 中已存在三个重要的角色:connect 角色,resource 角色,dba 角色。

- ##### CONNECT角色: -- 是授予最终用户的典型权利,最基本的；R

- ##### RESOURCE 角色: -- 是授予开发人员的；CRUD

- ##### DBA 角色:拥有全部特权,是系统最高权限,只有 DBA 才可以创建数据库结构,并且系统权限也需要 DBA 授出,且 DBA 用户可以操作全体用户的任意基表,包括删除；

```sql
grant dba to itcastuser -- 授予创建的用户DBA权限
```



## 四、Oracle数据类型[应用]

ORACLE基本数据类型（亦叫内置数据类型 built-in datatypes)可以按类型分为：

- 字符串类型

- 数字类型
- 日期类型
- LOB类型
- LONG RAW& RAW类型
- ROWID & UROWID类型

常用数据类型如下：

| No   | 数据类别   | 具体类型 | 描述                                                         |
| ---- | ---------- | -------- | ------------------------------------------------------------ |
| 1    | 字符串类型 | CHAR     | 定长字符串，会用空格填充来达到其最大长度.                    |
| 2    | 字符串类型 | VARCHAR2 | 变长字符串，与CHAR类型不同，它不会使用空格填充至最大长度。VARCHAR2最多可以存储4,000字节的信息。 |
| 3    | 数字类型   | NUMBER   | NUMBER(n)表示一个整数,长度是 n。<br/>NUMBER(m,n):表示一个小数,总长度是 m,小数是 n,整数是 m-n |
| 4    | 日期类型   | DATE     | 表示日期类型                                                 |
| 5    | LOB类型    | CLOB     | 大对象,表示大文本数据类型,可存 4G                            |
| 6    | LOB类型    | BLOB     | 大对象,表示二进制,可存 4G                                    |



## 五、表的管理[应用]

#### sql语句：

DDL: data definition language  数据定义语言 create、alter、drop

DML: data manipulation language 数据操作语言  增删改 insert,delete, update

DQL: data query language 数据查询语言  select ,where 等

DCL: data control language 数据控制语言 

### 5.1、Oracle DDL (database不要操作了，重点table)

#### 1）创建表   —— 语法同mysql，使用sql语句

```sql
create table person(
       pid number(20),
       pname varchar2(10),
       gender char(1),
       birthday date  
);
insert into person(pid, name, gender, birthday) values(1, '张三', 1, to_date('1999-12-22', 'yyyy-MM-dd'));
```

> oracle建表时，不需要跟什么engine引擎，charset字符设置。
>
> 那么oracle是如何解决中文乱码问题？［PLSQL 与 Oracle乱码解决］
>
> ```sql
> -- 第一步：查看服务器端的编码
> select userenv('language') from dual; -- 查到的结果：AMERICAN_AMERICA.ZHS16GBK
> 
> -- 第二步：执行下面语句查看 PARAMETER中 NLS_LANGUAGE的值
> select * from V$NLS_PARAMETERS  
> -- 1. 若NLS_LANGUAGE的值value 与 第一步的值不一样，则需要配置环境变量；
> -- 2. 一致则不会乱码
> 
> -- 第三步：设置环境变量
> 计算机->属性->高级系统设置->环境变量->新建
> 设置变量名:NLS_LANG,变量值:第1步查到的值， 我的是	AMERICAN_AMERICA.ZHS16GBK
> 
> -- 第四步：重新启动PLSQL,插入数据正常
> ```

#### 2）表的删除

```sql
drop table person
```

> 还有两个删除：对数据的删除 delete from person ， truncate table person

#### 3）表的修改

```sql
-- 添加一列或多列
alter table person add address varchar2(10);
alter table person add (address varchar2(10), money number(5,2));

-- 修改列的类型
alter table person modify gender char(2)
alter table person modify (address varchar2(20),money number(10,1))

-- 修改列名
alter table person rename column gender to sex;

-- 删除一列
alter table person drop column money;


```

#### 4）表的查询（非数据查询，了解）

```sql
-- 如同msyql show create table person;
select table_name,dbms_metadata.get_ddl('TABLE','PERSON')from dual,user_tables where table_name='PERSON'; -- TABLE_NAME 指表名 需要大写。

-- 如同msyql show tables
select TABLE_NAME from user_tables;

--show databases
select name as database from v$database;
```



### 5.2、Oracle DML －－ 数据的增删改 C(create)/U(update)/D(delete)

> ##### R(Retrieve) : 查询

#### 1）添加 insert

```sql
insert into person values(3,'李四',1,null,'北京育新',5000);
insert into person(pid, pname, gender, birthday) values(1, '张三', 1, to_date('1999-12-22', 'yyyy-MM-dd'),1000);
commit; -- Oracle DML 都需要commit,否则都可以回滚。 在PL/SQL在commit之前的回滚按钮会高亮！！
```

#### 2）修改 update

```sql
update person set pname='小妞', money=10000, gender='0' where pid =1;
```

#### 3）删除 delete

```sql
delete from person; -- 全部删除

-- 先删除表，再次创建表，效果等同于删除表中全部记录
-- 在数据量大的情况下，尤其在表中带有索引的情况下，该操作效率高。
-- 索引可以提供查询效率，但是会影响增删改效率。
truncate table person; 

delete from person where pid =1; -- 删除指定行数据
```



## 六、序列 －－ oracle 特有

在很多数据库中都存在一个自动增长的列，如果现在要想在 oracle 中完成自动增长的功能，则只能依靠序列完成,所有的自动增长操作,需要用户手工完成处理 ！

```sql
----序列不真的属于任何一张表，但是可以逻辑和表做绑定。
----序列：默认从1开始，依次递增，主要用来给主键赋值使用。
----dual：虚表，只是为了补全语法，没有任何意义。
create sequence s_person;
select s_person.nextval from dual; -- 查看下个序列
----添加一条记录
insert into person (pid, pname) values (s_person.nextval, '小明');

select s_person.currval from dual;  -- 查看当前序列
```



## 七、Scott用户下的表结构[了解]

***初学者的练习数据库，能演示各种复杂查询***

```sql
-- scott用户，密码：tiger (默认密码)
-- 解锁scott用户
alter user scott account unlock;
-- 解锁scott的密码, 下面语句也可来重置密码
alter user scott identified by scott;
```



## 八、函数［应用］

### 8.1、单行函数 : 作用于一行，返回一个值。

```sql
---1. 字符函数
select upper('yes') from dual;--YES
select lower('YES') from dual;--yes

---2. 数值函数
select round(56.16, -2) from dual;---四舍五入，后面的参数表示保留的位数
select trunc(56.16, -1) from dual;---直接截取，不在看后面位数的数字是否大于5.
select mod(10, 3) from dual;---求余数

---3. 日期函数
----查询出emp表中所有员工入职距离现在几天。
select sysdate-e.hiredate from emp e;
----算出明天此刻
select sysdate+1 from dual;
----查询出emp表中所有员工入职距离现在几月。
select months_between(sysdate,e.hiredate) from emp e;
----查询出emp表中所有员工入职距离现在几年。
select months_between(sysdate,e.hiredate)/12 from emp e;
----查询出emp表中所有员工入职距离现在几周。
select round((sysdate-e.hiredate)/7) from emp e;

---4. 转换函数
----日期转字符串
select to_char(sysdate, 'fm yyyy-mm-dd hh24:mi:ss') from dual;
----字符串转日期
select to_date('2018-6-7 16:39:50', 'fm yyyy-mm-dd hh24:mi:ss') from dual;

---5. 通用函数
---- 5.1 空值处理 nvl
---算出emp表中所有员工的年薪
----奖金里面有null值，如果null值和任意数字做算术运算，结果都是null。
select e.sal*12+nvl(e.comm, 0) from emp e;

--- 5.2 条件表达式
----条件表达式的通用写法，mysql和oracle通用
----给emp表中员工起中文名
select e.ename, 
       case e.ename
         when 'SMITH' then '曹贼'
           when 'ALLEN' then '大耳贼'
             when 'WARD' then '诸葛小儿'
               --else '无名' -- 其他都没有别名
                 end
from emp e;

---判断emp表中员工工资，如果高于3000显示高收入，如果高于1500低于3000显示中等收入，其余显示低收入
select e.sal, 
       case 
         when e.sal>3000 then '高收入'
           when e.sal>1500 then '中等收入'
               else '低收入'
                 end sal_status -- sal_status 是条件表达式的一个别名
from emp e;

---- 5.3 Decode函数,oracle专用条件表达式  类似 if....else if...esle
----oracle中除了起别名，都用单引号。
select e.ename, 
        decode(e.ename,
          'SMITH',  '曹贼',
            'ALLEN',  '大耳贼',
              'WARD',  '诸葛小儿',
                '无名') "中文名"  -- 此处“中文名”字段要么没有引号，要么就是双引号。单引号是错误            
from emp e;

```

### 8.2、多行函数(聚合函数)：作用于多行，返回一个值。

```sql
select count(1) from emp;---查询总数量
select sum(sal) from emp;---工资总和
select max(sal) from emp;---最大工资
select min(sal) from emp;---最低工资
select avg(sal) from emp;---平均工资
```



## 九、分组查询

```sql
---9.1 查询出每个部门的平均工资
---分组查询中，出现在group by后面的原始列，才能出现在select后面
---没有出现在group by后面的列，想在select后面，必须加上聚合函数。
---聚合函数有一个特性，可以把多行记录变成一个值。
select e.deptno, avg(e.sal)--, e.ename是不能出现的。一个部门平均工资只有一个，但是有多个成员
from emp e
group by e.deptno;

-- 9.2 查询出平均工资高于2000的部门信息
select e.deptno, avg(e.sal) a_sal, 
from emp e
group by e.deptno
having avg(e.sal) > 2000
-- 9.2.1 扩展：查询出平均工资高于2000的部门编码、部门平均工资及部门名称
-- 由于平均工资高于2000 的表不存在，所以此时需要子查询。
-- 1) 先查平均工资高2000部门信息 2) 然后根据子表与dept表查询得到要求
select t.*,d.dname
from (
     select e.deptno, avg(e.sal) a_sal
     from emp e
     group by e.deptno
     having avg(e.sal) > 2000
     ) t,dept d
where t.deptno = d.deptno

--9.3 所有条件都不能使用别名来判断。可以分析先后顺序,where条件判断在前,若用了别名，就不知道从哪里来的
--比如下面的条件语句也不能使用别名当条件
select ename, sal s from emp where sal>1500;

--9.4 查询出每个部门工资高于800的员工的平均工资, 并且平均工资高于2000的部门信息
-- 普通实现
----where是过滤分组前的数据，having是过滤分组后的数据。
---表现形式：where必须在group by之前，having是在group by之后。
select e.deptno,avg(e.sal) 
from emp e
where sal > 800
group by e.deptno
having avg(e.sal) > 2000
-- 子查询方式实现： 有缺陷，因为子查询后还是原来的emp表，不涉及自链接，没有多表关联查询，所以可以简化普通查询
select avg(t.sal)
from(
  select e.deptno,e.sal 
  from emp e
  where sal > 800) t
group by t.deptno 
having avg(t.sal) > 2000;
```



## 十、多表查询[应用]

```sql
---笛卡尔积
select * from emp e, dept d;

---等值连接 (隐式内连接)
select *
from emp e, dept d
where e.deptno=d.deptno;

---内连接 (显式内连接)
select *
from emp e inner join dept d
on e.deptno = d.deptno;

---查询出所有部门，以及部门下的员工信息。【右外连接】
select *
from emp e right join dept d
on e.deptno=d.deptno;

---查询所有员工信息，以及员工所属部门。【左外连接】
select *
from emp e left join dept d
on e.deptno=d.deptno;

---oracle中专用外连接
select *
from emp e, dept d
where e.deptno(+) = d.deptno; -- 右外链
where e.deptno = d.deptno(+); -- 左外链

---查询出员工姓名，员工领导姓名
--- 自链接
select e1.ename,e2.ename
from emp e1,emp e2
where e1.mgr = e2.empno -- 如此查，会出现一些员工没有领导就没有数据信息显示了。所以应该自外链

select e1.ename,e2.ename
from emp e1
left join emp e2
on e1.mgr = e2.empno

------查询出员工姓名，员工部门名称，员工领导姓名，员工领导部门名称
select e1.ename, d1.dname, e2.ename, d2.dname
from emp e1, emp e2, dept d1, dept d2
where e1.mgr = e2.empno
and e1.deptno=d1.deptno
and e2.deptno=d2.deptno; -- 有一个人虽然没有领导，但是也已在领导列表显示，这时就没有一味追求外链了。
```



## 十一、子查询[应用] －－ 也是多表的一种形式

```sql
---1. 子查询返回一个值
---查询出工资和SCOTT一样的员工信息
select * from emp where sal in ( -- 可以用 = ，但是有风险，如果是主键，则可以使用=，因为主键唯一
       select sal from emp where ename='SCOTT'
);

---2. 子查询返回一个集合
---查询出工资和10号部门任意员工一样的员工信息
select e.* 
from emp e 
where e.sal in (select sal from emp where deptno = 10)

---3. 子查询返回一张表
---查询出每个部门最低工资，和最低工资员工姓名，和该员工所在部门名称
---- 3.1 首先查出每个部门的最低工资
select e.deptno, min(e.sal)
from emp e
group by e.deptno
---- 3.2 然后子表，查询最低工资的员工及对应的部门名称
select t.*, e.ename,d.dname
from (
  select deptno, min(sal) msal
  from emp
  group by deptno) t, emp e ,dept d
--- 三张表联合查询，那就必须三个条件，否则当某个表的数据列值有重复数据，就会造成笛卡尔积。  
--- 即表表之间需要两两比较。
where t.deptno = e.deptno  
and t.msal = e.sal 
and e.deptno = d.deptno
```



## 十二、oracle中的分页查询 ，Rownum关键字 －－ 特有

#### ROWNUM行号：

1. 表示行号,实际上此是一个列,但是这个列是一个伪列,此列可以在每张表中出现。

2. 当我们做select操作的时候，每查询出一行记录，就会在该行上加上一个行号，行号从1开始，依次递增，不能跳着走。

```sql
----排序操作会影响rownum的顺序
select rownum, e.* from emp e order by e.sal desc

----如果涉及到排序，但是还要使用rownum的话，我们可以再次嵌套查询。
select rownum, t.* from(
	select e.* from emp e order by e.sal desc) t;
	
----emp表工资倒叙排列后，每页五条记录，查询第二页 【分页查询固定格式】
select tt.*
from (
  select rownum rn ,t.*
  from (select * from emp order by sal desc) t
  where rownum < 11) tt ----rownum行号不能写上大于一个正数。
where tt.rn > 5; 
```
