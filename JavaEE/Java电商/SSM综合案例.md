# SSM综合案例_01

一、AdminLTE 

二、

#### 问题1: springmvc类型转换？

## 三、SSM 环境搭建与产品操作

### 3.1、环境准备

#### 3.1.1、数据库与表结构

- ##### 创建用户与授权：

```properties
1. 创建用户: SQL语句创建、PL/SQL GUI创建；
 	SQL语句创建: 参考前面的内容Oracle。
 	PL/SQL GUI创建: 
 		1） 在dba／system用户下，找到Users表；
 		2） 右击new， 即可直接输入name、password；
        3） 赋权限: 
        	Object Privileges(对象权限): 指针对于某一张表的操作权限;
        	System Privileges(系统权限): 指对表的CRUD操作权限;
        	Role Privileges: 系统权限的集合;
        一般是设置角色权限,设置具体的resource与connect	！！
```

> 问题：连接oracle是连接表空间，还是用户呢？

- ##### 创建表

```

```

