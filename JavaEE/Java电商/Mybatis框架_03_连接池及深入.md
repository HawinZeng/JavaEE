# 03_Mybatis连接池、事务及深入

## 一、Mybatis 的连接池技术

### 1.1、Mybatis连接池的分类：3类

- ##### POOLED : 

  采用传统的javax.sql.DataSource规范中的连接池，mybatis中有针对规范的实现;

- ##### UNPOOLED : 

  采用传统的获取连接的方式，虽然也实现Javax.sql.DataSource接口，但是并没有使用池的思想。

- ##### JNDI : （拓展）

  采用服务器提供的JNDI技术实现，来获取DataSource对象，不同的服务器所能拿到DataSource是不一样。

  > 注意：如果不是web或者maven的war工程，是不能使用的JNDI方式获取连接池的！
  >  我们课程中使用的是tomcat服务器，采用连接池就是dbcp连接池。

### 1.2、Mybatis连接池配置的位置：

##### 在主配置文件SqlMapConfig.xml: 

```xml
<environments default="mysql">
    <environment id="mysql">
        <transactionManager type="JDBC"/>
        <dataSource type="POOLED">
            <property name="driver" value="${jdbc.driver}"/>
            <property name="url" value="${jdbc.url}"/>
            <property name="username" value="${jdbc.username}"/>
            <property name="password" value="${jdbc.password}"/>
        </dataSource>
    </environment>
</environments>
```

### 1.3、POOLED方式原理分析：

```java
// org.apache.ibatis.datasource.pooled.PooledDataSource
private PooledConnection popConnection(String username, String password) throws SQLException {
    ...
        while (conn == null) {
            synchronized (state) { // 同步锁，防止并发抢占
                if (!state.idleConnections.isEmpty()) { // 如果连接池有空闲连接
                    conn = state.idleConnections.remove(0); // 直接从拿空闲连接即可
                    ...
                } else { // 如果没有空闲连接，先判断连接池是否达到最大的量
                    if (state.activeConnections.size() < poolMaximumActiveConnections) {
                        // 没有达到最大容量，就new个连接出来
                        conn = new PooledConnection(dataSource.getConnection(), this);
                        ...
                    } else {
                        // 上面都不行，那就只能等待拿现有的conn，最老的连接。最先进入池中的，符合队列先进先出原理
                        PooledConnection oldestActiveConnection = state.activeConnections.get(0);
                        long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();
                        if (longestCheckoutTime > poolMaximumCheckoutTime) {
                            ...
                          ｝
                                ...
        }
    return conn;
}
```

### 1.4、JNDI方式了解

(参考后续)

## 二、Mybatis 的事务控制

- #### 内容回顾：

  ##### 1. 什么是事务？ 

  ##### 2. 事务的四大特性ACID？

  #####  3. 不考虑隔离性会产生的3个问题

  ##### 4. 解决办法：四种隔离级别

- #### Mybatis 事务提交：session.commit();

```java
//4.创建 SqlSession 对象。设置自动提交！！！！
session = factory.openSession(true);
```



## 三、 Mybatis 的动态 SQL 语句





## 四、Mybatis 多表查询之一对多

### 4.1、表之间的关系有几种：3种（具体细分可是4种）

- #### 一对多／多对一：

  ```java
  // 例子
  用户和订单就是一对多
  订单和用户就是多对一
  		一个用户可以下多个订单
  		多个订单属于同一个用户
  ```

- #### 一对一：

  ```java
  // 例子
  人和身份证号就是一对一
  			一个人只能有一个身份证号
  			一个身份证号只能属于一个人
  ```

- #### 多对多：

  ```java
  // 例子
  老师和学生之间就是多对多
  			一个学生可以被多个老师教过
  			一个老师可以交多个学生
  ```

  ```java
  // 特例
  如果拿出每一个订单，他都只能属于一个用户。
  所以Mybatis就把多对一看成了一对一。
  ```


### 4.2、一对一实现：账户与用户关系（实际：多对一。Mybatis特例）

- ####  方式一、定义一个自己封装的实体，实现一对一（不推荐）

```java
public class AccountUser extends Account {
    private String username;
    private String address;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
｝
```

```xml
<select id="findAccountUser" resultType="accountUser">
    select * from account a, user u where a.UID = u.id;
</select>
```

- #### 方式二、user主键在account，直接在account实体中封装一个user对象 （推荐）

```java
public class Account implements Serializable {
    private Integer ID;
    private Integer UID;
    private Double MONEY;
	// 封装一个实体
    private User user;
   	...
}
```

#### 此时，需要一个resultMap来定义对应封装信息！如下：

```xml
<resultMap id="accountUserMap" type="account">
    <id property="ID" column="aid" />
    <result property="UID" column="UID"/>
    <result property="MONEY" column="MONEY"/>
    <association property="user" column="ID" javaType="useR">
        <id property="id" column="id"/>
        <result property="username" column="username"/>
        <result property="address" column="address"/>
        <result property="sex" column="sex"/>
        <result property="birthday" column="birthday"/>
    </association>
</resultMap>

<select id="findAll" resultMap="accountUserMap">
    select u.*,a.id as aid,a.UID,a.MONEY from account a, user u where a.UID = u.id;
    <!--select * from account;-->
</select>
```



### 4.3、一对多实现：用户与账户关系

```java
public class User implements Serializable {
    private Integer id;
    private String username;
    private Date birthday;
    private String sex;
    private String address;

    //  一对多。主表实体应包含从表实体集合的引用
    private List<Account> accounts;

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
```

```xml
<resultMap id="userAccountMap" type="uSer">
    <id property="id" column="id"/>
    <result property="username" column="username"/>
    <result property="address" column="address"/>
    <result property="sex" column="sex"/>
    <result property="birthday" column="birthday"/>
    <collection property="accounts" ofType="account">
        <id property="ID" column="aid"/>
        <result property="UID" column="UID"/>
        <result property="MONEY" column="MONEY"/>
    </collection>
</resultMap>

<select id="findAll" resultMap="userAccountMap">
    select u.*,a.ID as aid,a.UID,a.MONEY from user u left outer join account a on u.id = a.UID;
</select>
```

> ##### 注意：一对多。xml配置的是：`<collection>`标签，封装属性是：ofType。



### 4.4、多对多实现：用户与角色

- #### 查询所有用户及对应的角色信息

```java
public class User implements Serializable {
    private Integer id;
    private String username;
    private Date birthday;
    private String sex;
    private String address;
    
	// 多对多，相互添加对方实体集合的引用
    private List<Role> roles;

    public List<Role> getRoles() {
        return roles;
    }
    ....
}
```

```xml
<resultMap id="userMap" type="uSer">
    <id property="id" column="id"/>
    <result property="username" column="username"/>
    <result property="address" column="address"/>
    <result property="sex" column="sex"/>
    <result property="birthday" column="birthday"/>
    <collection property="accounts" ofType="account">
        <id property="ID" column="aid"/>
        <result property="UID" column="UID"/>
        <result property="MONEY" column="MONEY"/>
    </collection>
    <collection property="roles" ofType="role">
        <id property="ID" column="rid"/>
        <result property="ROLE_NAME" column="ROLE_NAME"/>
        <result property="ROLE_DESC" column="ROLE_DESC"/>
    </collection>
</resultMap>

<select id="findAllWithRoles" resultMap="userMap">
    select u.*,r.ID as rid,r.ROLE_NAME,r.ROLE_DESC from user u
    left outer join user_role ur on u.id = ur.UID
    left outer join role r on ur.RID = r.ID;
</select>
```

- #### 查询所有角色及对应用户的信息

```java
public class Role implements Serializable {
    private Integer ID;
    private String ROLE_NAME;
    private String ROLE_DESC;

    private List<User> users;
    ...
}
```

```xml
<mapper namespace="com.eoony.dao.IRoleDao">
    <resultMap id="roleMap" type="role">
        <id property="ID" column="rid"/>
        <result property="ROLE_NAME" column="ROLE_NAME"/>
        <result property="ROLE_DESC" column="ROLE_DESC"/>
        <collection property="users" ofType="usEr">
            <id property="id" column="id"/>
            <result property="username" column="username"/>
            <result property="address" column="address"/>
            <result property="sex" column="sex"/>
            <result property="birthday" column="birthday"/>
        </collection>
    </resultMap>


    <select id="findAll" resultMap="roleMap">
        select u.*,r.ID as rid,r.ROLE_NAME,r.ROLE_DESC from role r
         left outer join user_role ur on r.ID = ur.RID
         left outer join user u on ur.UID = u.ID
    </select>
</mapper>
```



## 五、JNDI补充

JNDI 其实就是Mybatis自己不实现连接池创建，而是由服务器已经实现的技术来创建连接池达到完成使用！

如Tomcat已有dbcp连接池了，我们直接拿来用即可！

- #### webapp / META-INF / context.xml  -- 创建该目录及文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <!-- 
<Resource 
name="jdbc/mybatis"								数据源的名称
type="javax.sql.DataSource"						数据源类型
auth="Container"								数据源提供者
maxActive="20"									最大活动数
maxWait="10000"									最大等待时间
maxIdle="5"										最大空闲数
username="root"									用户名
password="1234"									密码
driverClassName="com.mysql.jdbc.Driver"			驱动类
url="jdbc:mysql://localhost:3306/eesy_mybatis"	连接url字符串
/>
 -->
    <Resource 
              name="jdbc/mybatis"
              type="javax.sql.DataSource"
              auth="Container"
              maxActive="20"
              maxWait="10000"
              maxIdle="5"
              username="root"
              password="root"
              driverClassName="com.mysql.jdbc.Driver"
              url="jdbc:mysql://localhost:3306/day03_mybatis?useUnicode=true&amp;characterEncoding=UTF-8"
              />
</Context>
```

> ##### 1. Tomcat启动后，就会将此文件，映射一个如同windows注册表一样的Map。通过文件directory＋name(名称“jdbc/mybatis” 这个key , 找出下面的Object封装数据内容来使用！
>
> ##### 2. 下面的主配置文件：就是通过`value="java:comp/env/jdbc/mybatis"`来找到对应数据源信息！

- #### 主配置文件修改

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- 导入约束 -->
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <typeAliases>
        <package name="com.itheima.domain"></package>
    </typeAliases>
    <!-- 配置mybatis的环境 -->
    <environments default="mysql">
        <!-- 配置mysql的环境 -->
        <environment id="mysql">
            <!-- 配置事务控制的方式 -->
            <transactionManager type="JDBC"/>
            <!-- 配置连接数据库的必备信息  type属性表示是否使用数据源（连接池）-->
            <dataSource type="JNDI">
                <property name="data_source" value="java:comp/env/jdbc/mybatis"/>
            </dataSource>
        </environment>
    </environments>

    <!-- 指定mapper配置文件的位置 -->
    <mappers>
        <mapper resource="com/itheima/dao/IUserDao.xml"/>
    </mappers>
</configuration>
```

> ##### 1. maven工程也可以直接食用本地Tomcat Local Server启动！如同普通项目一样，配置下本地Tomcat即可启动！ －－－启动后，底部就会出现application-server栏！
>
> ##### 2. 上述测试，要在jsp中进行测试，不能通过junit测试，因为dataSource是从Tomcat配置中拿来的！











