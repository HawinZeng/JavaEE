# 第五节 JDBC （重点掌握）

## 一、概述：

**概念：**Java DataBase Connectivity  Java 数据库连接， Java语言操作数据库；

**本质：**其实是官方（sun公司）定义的一套操作所有关系型数据库的规则，即接口。各个数据库厂商去实现这套接口，提供数据库驱动jar包。我们可以使用这套接口（JDBC）编程，真正执行的代码是驱动jar包中的实现类。

![](attach/F0_JDBC本质.png)



## 二、快速入门

 步骤：

1. 导入驱动jar包 mysql-connector-java-5.1.37-bin.jar

​	1.1复制mysql-connector-java-5.1.37-bin.jar到项目的libs目录下
	1.2.右键-->Add As Library

2. 注册驱动

3. 获取数据库连接对象 Connection

4. 定义sql

5. 获取执行sql语句的对象 Statement

6. 执行sql，接受返回结果

		7. 处理结果

		8. 释放资源

```java
    public static void main(String[] args) throws Exception {
        // 1. 注册驱动
        Class.forName("com.mysql.jdbc.Driver");

        // 2. 获取数据库连接对象 Connection
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/db4", "root", "root");

        // 3. 定义sql
        String sql = "update stu set math=99 where name='郭靖'";

        // 4. 获取执行sql的对象statement
        Statement stat = con.createStatement();

        // 5. 执行sql
        int count = stat.executeUpdate(sql);

        // 6. 查看执行结果，处理结果
        System.out.println(count);

        // 7. 关闭资源
        stat.close();
        con.close();
    }
```













