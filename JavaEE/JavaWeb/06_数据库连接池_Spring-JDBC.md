# 第六节 数据库连接池、Spring JDBC



## 一、数据库链接池

为什么要使用连接池？

像前面代码，我们每次都要去获取数据库链接，然后最后还要关闭Connection对象。而获取数据库链接是系统底层申请资源的操作，是比较耗时的操作。

那么这么不科学的操作，怎么解决？那就是我们的数据库链接池来搞定咯！！！

### 1、概述

- 概念：其实就是一个容器(集合)，存放数据库连接的容器。

  当系统初始化好后，容器被创建，容器中会申请一些连接对象，当用户来访问数据库时，从容器中获取连接对象，用户访问完之后，会将连接对象归还给容器。

- 好处：

  1. 节约资源
  2. 用户访问高效

### 2、实现

- 标准接口：DataSource   javax.sql包下的。具体有以下两个方法：
  - 获取连接：getConnection()
  - 归还连接：Connection.close()。如果连接对象Connection是从连接池中获取的，那么调用Connection.close()方法，则不会再关闭连接了。而是归还连接
- 一般我们不去实现它，有数据库厂商来实现。常见的两种：
  - C3P0：数据库连接池技术
  - Druid(德鲁伊)：数据库连接池实现技术，由阿里巴巴提供的。最好的数据库连接池技术！！



## 二、C3P0：数据库连接池技术

- 操作步骤：

  1. 导入jar包 (两个) c3p0-0.9.5.2.jar mchange-commons-java-0.2.12.jar

  2. 定义配置文件：

     名称： c3p0.properties 或者 c3p0-config.xml

     路径：直接将文件放在src目录下即可。

  3. 创建核心对象 数据库连接池对象 ComboPooledDataSource

  4. 获取连接： getConnection

- 代码

   //1.创建数据库连接池对象
    DataSource ds  = new ComboPooledDataSource();
    //2. 获取连接对象
    Connection conn = ds.getConnection();



## 三、Druid：数据库连接池实现技术，由阿里巴巴提供的

目前最好使用的数据库连接池，后续都用这个做项目！！！

- 操作步骤：

  1. 导入jar包 druid-1.0.9.jar

  2. 定义配置文件：

     - 是properties形式的

     - 可以叫任意名称，可以放在任意目录下

  3. 加载配置文件.Properties

  4. 获取数据库连接池对象：通过工厂来来获取  DruidDataSourceFactory

  5. 获取连接：getConnection

- 代码：

```java
public static void main(String[] args) throws Exception {
    // 1. 加载配置文件
   Properties pro = new Properties();
   InputStream is = Demo1.class.getClassLoader().
            getResourceAsStream("druid.properties");
   pro.load(is);
    // 2. 工厂方法，获取DataSource对象
   DataSource ds  = DruidDataSourceFactory.createDataSource(pro);
   Connection con = ds.getConnection();
}
```

- JDBCUtils修正

```java
public final class JDBCUtils {

    private static DataSource ds;
    static {
        try{
            Properties pro = new Properties();
            InputStream is = Demo1.class.getClassLoader().getResourceAsStream("druid.properties");
            pro.load(is);
            ds  = DruidDataSourceFactory.createDataSource(pro);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取连接池对象
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    /**
     *  释放资源
     * @param stat
     * @param con
     */
    public static void close(Statement stat,Connection con){
        close(null,stat,con);
    }

    /**
     *  释放资源
     * @param stat
     * @param con
     */
    public static void close(ResultSet rs,Statement stat, Connection con){

        if(null != rs){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(null != stat){
            try {
                stat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(null != con){
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static DataSource getDataSource(){
        return ds;
    }
}
```

## 特别注意：统一编码格式防止意外

```java
// 一定要加   ?useUnicode=true&characterEncoding=UTF-8  这一串！！！！
url=jdbc:mysql:///day14?useUnicode=true&characterEncoding=UTF-8
```





## 四、Spring JDBC -- 简单封装JDBC

- 概述：**Spring框架对JDBC的简单封装。提供了一个`JDBCTemplate`对象简化JDBC的开发；**

- 使用步骤：

1. 导入jar包, 6个jar包；

2. 创建**`JdbcTemplate`**对象。依赖于数据源DataSource

   ```java
   JdbcTemplate template = new JdbcTemplate(ds);
   ```

3. 调用**`JdbcTemplate`**的方法来完成CRUD的操作

   ```java
   update(); // 执行DML语句。增、删、改语句
   
   queryForMap(); // 查询结果将结果集封装为map集合，将列名作为key，将值作为value 将这条记录封装为一个map集合
   
   queryForList(); // 查询结果将结果集封装为list集合
   	// 注意：将每一条记录封装为一个Map集合，再将Map集合装载到List集合中
   
   query(); // 查询结果，将结果封装为JavaBean对象
   	// query的参数：RowMapper
   		// 一般我们使用BeanPropertyRowMapper实现类。可以完成数据到JavaBean的自动封装
   		// new BeanPropertyRowMapper<类型>(类型.class)
   
   queryForObject; // 查询结果，将结果封装为对象
   	//一般用于聚合函数的查询
   
   ```

4. 用单元测试方式进行调试，省去在main方法调试还要注释干扰项；

```

```

