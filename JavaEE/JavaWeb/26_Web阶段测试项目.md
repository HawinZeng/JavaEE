# 第二十六节  Web阶段项目

## 一，技术选型:

### web层：

​	Servlet＋filter: 前端控制器

​	jsp ＋ el ＋jstl：视图

​	ajax ： 异步请求

​	BeanUtils: 数据封装

​	Jaskson: json序列化工具

### Service层：

​	Redis：缓存

​	Jedis：客户端

### Dao层：

​	mysql：数据库

​	druid：数据库连接池

​	JdbcTemplate : sql 操作工具类 Spring

### Server:

​	JDK8+ tomcat7 +maven



## 二、数据库

创建数据库：依据sql



## 三、登录/注册

```java
Servlet.service() for servlet [jsp] in context with path [/bookstore] threw exception [Unable to compile class for JSP: 

查找原因：由于Maven的依赖jsp 与 Tomcat本省jsp 两套api冲突造成无法compile class！！！
解决： Maven的依赖jsp添加作用域：<scope>provided</scope>
```









