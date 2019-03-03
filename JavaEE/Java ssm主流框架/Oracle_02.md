# Oracle_02

## 一、视图[应用]

#### 1.1、视图的概念：

视图就是提供一个查询的窗口，所有数据来自于原表。

#### 1.2、视图作用：

第一：视图可以屏蔽掉一些敏感字段，如sal工资字段；

第二：保证总部和分部数据及时统一；（视图中没有真实数据，当总部原表数据一旦更新，视图就会立马更新）

#### 1.3、视图操作

```sql
---查询语句创建表, 跨用户查询表，相当于copy
create table emp as select * from scott.emp;

---创建视图【必须有dba权限】
create view v_emp as select ename, job from emp;
---查询视图
select * from v_emp;
---修改视图[不推荐]
update v_emp set job='CLERK' where ename='ALLEN'; -- 修改视图，其实际就是修改原表数据。
commit;
---创建只读视图
create view v_emp1 as select ename, job from emp with read only;
```



## 二、索引

#### 2.1、索引的概念：

概念：索引就是在表的列上构建一个二叉树；

作用：可以大幅度提高查询效率的目的，但是索引会影响增删改的效率。

> ##### 为什么添加了索引之后,会加快查询速度呢?
>
> ```properties
> 图书馆: 
> 	如果杂乱地放书的话检索起来就非常困难,所以将书分类,然后再建一个箱子,箱 子里面放卡片,卡片里面可以按类查询,按书名查或者类别查,这样的话速度会快很多很多, 这个就有点像索引。索引的好处就是提高你找到书的速度,但是正是因为你建了索引,就应该有人 专门来维护索引,维护索引是要有时间精力的开销的,也就是说索引是不能乱建的。
> 	所以建索引有个原则:如果有一个字段如果不经常查询,就不要去建索引。
> 	现在把书变成我们的表,把卡片变成 我们的索引,就知道为什么索引会快,为什么会有开销。
> ```

#### 2.2、索引的操作

```sql
--1. 单列索引
---创建单列索引
create index idx_ename on emp(ename);
---单列索引触发规则，条件必须是索引列中的原始值。
---单行函数，模糊查询，都会影响索引的触发。
select * from emp where ename='SCOTT'

-- 2. 复合索引
---创建复合索引
create index idx_enamejob on emp(ename, job);
---复合索引中第一列为优先检索列
---如果要触发复合索引，必须包含有优先检索列中的原始值。
select * from emp where ename='SCOTT' and job='xx';---触发复合索引
select * from emp where ename='SCOTT' or job='xx';---不触发索引
select * from emp where ename='SCOTT';---触发单列索引。
```

> #### 索引的使用原则: 
>
> -  在大表上建立索引才有意义；
> -  在where子句后面或者是连接条件上的字段建立索引；
> -  表中数据修改频率高时不建议建立索引 ；



## 三、pl/sql 编程语言

#### 3.1、pl/sql 编程语言的概念

1. pl/sql编程语言是对sql语言的扩展，使得sql语言具有过程化编程的特性。
2. pl/sql编程语言比一般的过程化编程语言，更加灵活高效。
3. pl/sql编程语言主要用来编写存储过程和存储函数等。

#### 3.2、pl/sql语法

```sql
---声明方法
---赋值操作可以使用:=也可以使用into查询语句赋值
declare
       i number(2) := 10;
       s varchar2(10) := '小明';
       ena emp.ename%type; -- 引用型变量, 直接查看赋值类型比较low，同时可能造成资源浪费
       emprow emp%rowtype; -- 记录型变量
begin
       dbms_output.put_line(i);
       dbms_output.put_line(s);
       select ename into ena from emp where empno=7788;
       dbms_output.put_line(ena);
       select * into emprow from emp where empno=7788;
       dbms_output.put_line(emprow.ename || '的工作为：'|| emprow.job);
end;  
-----输出结果----
10
小明
SCOTT
SCOTT的工作为：ANALYST
```

```sql
---pl/sql中的if判断

declare
       i number(3) := &ii;
begin
       if i<18 then
         dbms_output.put_line('未成年');
       elsif i<40 then
         dbms_output.put_line('中成年');
       else
          dbms_output.put_line('老成年');
       end if;
end;    
```

```sql
---pl/sql中的loop循环
---用三种方式输出1到10是个数字

---1. while循环
declare
  i number(2) := 1;
begin
  while i<11 loop
     dbms_output.put_line(i);
     i := i+1;
  end loop;  
end;
---2. exit循环 (重点)
declare
  i number(2) := 1;
begin
  loop
    exit when i>10;
    dbms_output.put_line(i);
    i := i+1;
  end loop;
end;
---3. for循环
declare

begin
  for i in 1..10 loop
     dbms_output.put_line(i);  
  end loop;
end;
```

```sql
---游标：可以存放多个对象，多行记录。

---输出emp表中所有员工的姓名
declare
  cursor c1 is select * from emp;
  emprow emp%rowtype;
begin
  open c1;
     loop
         fetch c1 into emprow;
         exit when c1%notfound;
         dbms_output.put_line(emprow.ename);
     end loop;
  close c1;
end;

-----给指定部门员工涨工资
declare
  cursor c2(eno emp.deptno%type) 
  is select empno from emp where deptno = eno;
  en emp.empno%type;
begin
  open c2(10);
     loop
        fetch c2 into en;
        exit when c2%notfound;
        update emp set sal=sal+100 where empno=en;
        commit;
     end loop;  
  close c2;
end;
----查询10号部门员工信息
select * from emp where deptno = 10;
```



## 四、存储过程 [理解]

#### 4.1、概念

存储过程(Stored Procedure)是在大型数据库系统中，一组为了完成特定功能的 SQL 语句集，经
编译后存储在数据库中，用户通过指定存储过程的名字并给出参数(如果该存储过程带有参数)来
执行它。存储过程是数据库中的一个重要对象，任何一个设计良好的数据库应用程序都应该用到存
储过程。

##### 简单理解：

- 存储过程就是提前已经编译好的一段pl/sql语言，放置在数据库端可以直接被调用。这一段pl/sql一般都是固定步骤的业务。

> ##### 与java语法操作数据对比：java需要获取数据库连接对象connection，而存储过程就在数据库，不需要这个对象，效率肯定要高！但是不要复杂的操作，很难维护！

#### 4.2、语法

```sql
----------第1种写法--------------------
create [or replace] PROCEDURE 过程名[(参数名 in/out 数据类型)] 
AS
begin
	PLSQL 子程序体; 
End;
----------第2种写法--------------------
create [or replace] PROCEDURE 过程名[(参数名 in/out 数据类型)] 
is
begin
	PLSQL 子程序体; 
End [过程名];
```

#### 4.3、案例

```sql
-- 给指定员工涨100元
-- or replace代表可以修改，若去掉话，一次性没有成功，就会不好修改了。所以一般加上
create or replace procedure p1(eno emp.empno%type) 
is
begin
  update emp set sal = sal+100 where empno = eno;
  commit;
end;
----- 创建成功后，会在Procedures文件夹生成一个p1文件。代表这个准备好的存储过程！
```



## 五、存储函数[理解]

#### 5.1、语法

```sql
create or replace function 函数名(Name in type, Name in type, ...) return 数据类型 
is 
	结果变量 数据类型;
begin 
	return(结果变量);
end 函数名;
```

> #### 存储过程和存储函数的区别:
>
> ```properties
> 1. 语法区别：
> 	- 关键字不一样;
> 	- 存储函数比存储过程多了两个return; 
> 	
> 2. 本质区别：
> 	- 存储函数有返回值，而存储过程没有返回值。
> 	- 如果存储过程想实现有返回值的业务，我们就必须使用out类型的参数。
> 	- 即便是存储过程使用了out类型的参数，其本质也不是真的有了返回值，而是在存储过程内部给out类型参数赋值，在执行完毕后，我们直接拿到输出类型参数的值。
> 	
> 3. 我们可以使用存储函数有返回值的特性，来自定义函数。而存储过程不能用来自定义函数。
> ```
>
> ```sql
> ----案例需求：查询出员工姓名，员工所在部门名称。
> ----案例准备工作：把scott用户下的dept表复制到当前用户下。
> create table dept as select * from scott.dept;
> ----使用传统方式来实现案例需求
> select e.ename, d.dname
> from emp e, dept d
> where e.deptno=d.deptno;
> ----使用存储函数来实现提供一个部门编号，输出一个部门名称。
> create or replace function fdna(dno dept.deptno%type) return dept.dname%type
> is
>   dna dept.dname%type;
> begin
>   select dname into dna from dept where deptno = dno;
>   return dna;
> end;
> 
> ---使用fdna存储函数来实现案例需求：查询出员工姓名，员工所在部门名称。
> select e.ename, fdna(e.deptno) from emp e;
> ```



## 六、触发器

#### 6.1、触发器概念：

​	就是制定一个规则，在我们做增删改操作的时候，只要满足该规则，自动触发，无需调用。

#### 6.2、分类：

- 语句级触发器：不包含有for each row的触发器。

- 行级触发器：包含有for each row的就是行级触发器。

  - 加for each row是为了使用:old或者:new对象或者一行记录。

  | 触发语句 | :old                 | :new                 |
  | -------- | -------------------- | -------------------- |
  | Insert   | 所有字段都是空(null) | 将要插入的数据       |
  | Update   | 更新以前该行的值     | 更新后的值           |
  | Delete   | 删除以前该行的值     | 所有字段都是空(null) |


#### 6.3、语法：

```sql
CREATE [or REPLACE] TRIGGER 触发器名 
	{BEFORE | AFTER}
	{DELETE | INSERT | UPDATE [OF 列名]} 
	ON 表名
	[FOR EACH ROW [WHEN(条件) ] ] 
begin
	PLSQL 块
End 触发器名;
```

#### 6.4、案例1：

```sql
---1. 语句级触发器: 在指定的操作语句操作之前或之后执行一次,不管这条语句影响 了多少行 。
----插入一条记录，输出一个新员工入职
create or replace trigger t1
after
insert
on person
declare

begin
  dbms_output.put_line('一个新员工入职'); -- 触发这个输出
end;
---触发t1
insert into person values (1, '小红');
commit;
```

```sql
---2. 行级别触发器: 
--- 触发语句作用的每一条记录都被触发。在行级触 发器中使用old和new伪记录变量, 识别值的状态。
---不能给员工降薪
---raise_application_error(-20001~-20999之间, '错误提示信息');
create or replace trigger t2
before
update
on emp
for each row
declare

begin
  if :old.sal>:new.sal then
     raise_application_error(-20001, '不能给员工降薪');
  end if;
end;
----触发t2
select * from emp where empno = 7788;
update emp set sal=sal-1 where empno = 7788;
commit;
```

#### 6.5、案例2: 实际开发常用

```sql
----案例：触发器实现主键自增。【行级触发器】
create or replace trigger auid
before
insert
on person
for each row
declare

begin
  select s_person.nextval into :new.pid from dual;
end;
--查询person表数据
select * from person;
---使用auid实现主键自增
insert into person (pname) values ('a');
commit;
insert into person values (1, 'b');
commit;
```



## 七、Java程序调用存储过程／存储函数

```java
/**
 * java调用存储过程
 *  {?= call <procedure-name>[(<arg1>,<arg2>, ...)]}   调用存储函数使用
 *  {call <procedure-name>[(<arg1>,<arg2>, ...)]}   调用存储过程使用
 */
@Test
public void javaCallProcedure() throws Exception{

    Class.forName("oracle.jdbc.driver.OracleDriver");
    Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.186.141:1521:orcl", "itheima", "itheima");

    CallableStatement pstm = connection.prepareCall("{call p_yearsal(?,?)} ");

    pstm.setObject(1,"7788");
    pstm.registerOutParameter(2, OracleTypes.NUMBER);

    pstm.execute();
    System.out.println(pstm.getObject(2));

    pstm.close();
    connection.close();
}

/**
 * 调用存储函数
 * {?= call <procedure-name>[(<arg1>,<arg2>, ...)]}   调用存储函数使用
 */
@Test
public void javaCallFunction() throws Exception{

    Class.forName("oracle.jdbc.driver.OracleDriver");

    Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.186.141:1521:orcl", "itheima", "itheima");

    CallableStatement pstm = connection.prepareCall("{?=call f_yearsal(?)} ");

    pstm.setObject(2,"7788");
    pstm.registerOutParameter(1, OracleTypes.NUMBER);

    pstm.execute();

    System.out.println(pstm.getObject(1));

    pstm.close();
    connection.close();
}
```

> 环境设置：
>
> ```properties
> 1. 首先要连通vmare的系统：ping 192.168.186.141(虚拟机的IP地址) 
> 	ping不通：检查防火墙
> 2. 检查oracle的网络配置项：
> 	oracle安装包下: C:\oracle\product\10.2.0\db_1\NETWORK\ADMIN里面两个文件
>     	- listener.ora  将网址修改为192.168.186.141
>     	- tnsnames.ora  将网址修改为192.168.186.141 (端口号不要动，保持1521)
> 3. 修改后，此时PL/SQL是无法连接Oracle，若要使用PL/SQL图形管理工具连接，必须把tnsnames.ora拷贝到某个地方，最好在PL/SQL安装包下新建一个config文件夹，放入。然后再配置一个“TNS_ADMIN”的系统环境变量，值就是../config/tnsnames.ora 
> ```

















