# MySQL的约束

### 一、DQL:查询语句

- #### 排序查询

```sql
 order by 子句
 order by 排序字段1 排序方式1 ，  排序字段2 排序方式2...

	* 排序方式：
		* ASC：升序，默认的。
		* DESC：降序。

	* 注意：
		* 如果有多个排序条件，则当前边的条件值一样时，才会判断第二条件。
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



## 二、约束

#### 2.1、概念：

 对表中的数据进行限定，保证数据的正确性、有效性和完整性。	

#### 2.2、分类：

 	1. 主键约束：primary key
 	2. 非空约束：not null
 	3. 唯一约束：unique
 	4. 外键约束：foreign key

#### 2.3、非空约束：not null，值不能为null

```sql
-- 1. 创建表时添加约束
CREATE TABLE person(
	id INT,
	NAME VARCHAR(20) NOT NULL -- name为非空
);
insert into person (id,name) values (1,null); // 插入失败
```

```sql
-- 2. 创建表完后，添加非空约束
ALTER TABLE person MODIFY NAME VARCHAR(20) NOT NULL;
-- 3. 删除非空约束
ALTER TABLE person MODIFY NAME VARCHAR(20) ;
```

#### 2.4、唯一约束：unique，值不能重复

```sql
-- 1. 创建表时，添加唯一约束
CREATE TABLE person(
	id INT,
	NAME VARCHAR(20) NOT NULL, -- name为非空
	phone_num varchar(32) unique
);
-- 注意mysql中，唯一约束限定的列的值可以有多个null
```

```sql
-- 2. 删除唯一约束
ALTER TABLE person MODIFY phone_num VARCHAR(32) ; // 这个是无效的
ALTER TABLE person DROP INDEX phone_num; // 这个有效！！

--3. 在创建表后，添加唯一约束
ALTER TABLE person MODIFY phone_num VARCHAR(32) unique; // 同非空判断
```

#### 2.5、主键约束：primary key

##### 特点：

1. 含义：非空且唯一
2. 一张表只能有一个字段为主键
3. 主键就是表中记录的唯一标识

```sql
-- 1. 在创建表时，添加主键约束
create table stu(
	id int primary key,-- 给id添加主键约束
	name varchar(20)
);

-- 2. 删除主键
alter table stu modify id int ; // 错误，无效
ALTER TABLE stu DROP PRIMARY KEY;

-- 3. 创建完表后，添加主键
ALTER TABLE stu MODIFY id INT PRIMARY KEY;
```

##### 自动增长：auto_increment, 一般与主键一同使用，但也可以不与主键搭配

```sql
-- 1. 在创建表时，添加主键约束，并且完成主键自增长
create table stu(
	id int primary key auto_increment,-- 给id添加主键约束
	name varchar(20)
);

-- 2. 删除自动增长
ALTER TABLE stu MODIFY id INT;

-- 3. 添加自动增长
ALTER TABLE stu MODIFY id INT AUTO_INCREMENT;
```



#### 2.6、外键约束：foreign key,让表于表产生关系，从而保证数据的正确性。

当发现数据有冗余时，

```sql
-- 1. 在创建表时，可以添加外键
```



