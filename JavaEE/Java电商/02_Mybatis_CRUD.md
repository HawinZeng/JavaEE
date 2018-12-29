# 02_Mybatis CRUD操作

## 一、Mybatis CRUD

### 1.1、查询所有／一个

```xml
<select id="findAll" resultType="com.eoony.domain.User">
    select * from user;
</select>

<!-- 查询一个 -->
<select id="findById" parameterType="Integer" resultType="com.eoony.domain.User">
    select * from user where id=#{uid};
</select>
```

```java
public void findAllTest() {
    List<User> users = userDao.findAll();
    for(User user:users){
        System.out.println(user);
    }
}

public void findOneTest(){
    User user = userDao.findById(51);
    System.out.println(user);
}
```

> ##### 1. 有参数：一定要parameterType属性，指明参数类型！！
>
> ##### 2. 有返回值：一定要有resultType属性，指明返回值类型！！
>
> ##### 以上两个都要使用全限定类名！！！！

### 1.2、 保存

```xml
<insert id="saveUser" parameterType="com.eoony.domain.User">
    <selectKey keyProperty="id" keyColumn="id" order="AFTER" resultType="Integer">
        select last_insert_id();
    </selectKey>
    insert into user(username,address,sex,birthday) values(#{username},#{address},#{sex},#{birthday});
</insert>
```

```java
public void saveTest()  {
    User user = new User();
    user.setUsername("mybatis112221");
    user.setAddress("深圳市龙华区");
    user.setSex("男");
    user.setBirthday(new Date());
    System.out.println(user); // User{id=null, username='mybatis112221', birthday=Thu Dec 27 17:50:48 CST 2018, sex='男', address='深圳市龙华区'}

    userDao.saveUser(user);
    System.out.println(user);// User{id=54, username='mybatis112221', birthday=Thu Dec 27 17:50:48 CST 2018, sex='男', address='深圳市龙华区'}
    // Setting autocommit to false on JDBC Connection 
	sqlSession.commit(); // 一定要主动提交！！！否则会出现上面的错误信息
}
```

> 1. ##### 添加了下面的标签及查询，会自动将id查找赋值给user对象：
>
> ```xml
> <selectKey keyProperty="id" keyColumn="id" order="AFTER" resultType="Integer">
>     select last_insert_id();
> </selectKey>
> ```
>
> 2. ##### 带参数格式：#{类对应的属性名称}
>
> ```sql
> insert into user(username,address,sex,birthday) values(#{username},#{address},#{sex},#{birthday});
> ```

### 1.3、更新

```xml
<update id="updateUser" parameterType="com.eoony.domain.User">
    update user set username=#{username},address=#{address},sex=#{sex},birthday=#{birthday} where id=#{id};
</update>
```

```java
public void updateTest(){
    User user = new User();
    user.setId(51);
    user.setUsername("javaEE");
    user.setAddress("北京市东环1圈");
    user.setSex("男");
    user.setBirthday(new Date());

    userDao.updateUser(user);
}
```

### 1.4、删除

```xml
<delete id="deleteUser" parameterType="Integer">
    delete from user where id=#{uid};
</delete>
```

> ##### 只有一个参数，参数名称可以任意写，不一定要是属性名称！

```java
public void deleteTest(){
    userDao.deleteUser(48);
}
```

### 1.5、模糊查询

```xml
<select id="findByName" parameterType="string" resultType="com.eoony.domain.User">
    select * from user where username like #{name};
    <!--select * from user where username like '%${value}%'-->
</select>
```

```java
public void findByNameTest(){
    List<User> users= userDao.findByName("%王%");
    //List<User> users= userDao.findByName("王");
    for (User user:users){
        System.out.println(user);
    }
}
```

> 模糊查询两种配置：
>
> ```sql
> -- 推荐使用, 使用了预处理对象。 
> select * from user where username like #{name}; 
> ```
>
> ```sql
> -- 不推荐使用，直接使用的是Statement对象
> select * from user where username like '%${value}%'
> ```

### 1.6、使用聚合函数查询

```xml
<select id="findCount" resultType="Integer">
    select count(*) from user ;
</select>
```

```java
public void testFindCount(){
    int count = userDao.findCount();
    System.out.println(count);
}
```



## 二、Mybatis 的参数深入

### 2.1、parameterType 配置参数

- #### 类型选择： 

  基本类型、引用类型（String类型）、实体类型（POJO类型）

- #### 传递 pojo 包装对象案例：

  ```xml
  <select id="findByVo" parameterType="com.eoony.domain.QueryVo" resultType="com.eoony.domain.User">
      select * from user where username like #{user.username};
  </select>
  ```

  > 1. ##### 使用pojo包装对象时，配置文件参数表达要使用：OGNL表达式！
  >
  >    ```properties
  >    OGNL表达式: Object Graphic Navigation Language
  >    		    对象    图      导航        语言
  >    		    
  >    它是通过对象的取值方法来获取数据。在写法上把get给省略了，即属性。
  >    	比如：我们获取用户的名称
  >    		类中的写法：user.getUsername();
  >    		OGNL表达式写法：user.username	
  >            
  >    看看前面的CRUD，mybatis中为什么能直接写username，而不是user.username呢？ 
  >    : 因为在parameterType中已经提供了属性所属的类，所以此时不需要写对象名!!
  >    ```
  >
  > 2. ##### 使用pojo包装多个对象，方便复杂的多表查询；

  ```java
  // POJO对象
  public class QueryVo {
      private User user;
      public User getUser() {
          return user;
      }
      public void setUser(User user) {
          this.user = user;
      }
  }
  
  public void testFindByVo(){
      QueryVo vo = new QueryVo();
      User u = new User();
      u.setUsername("%王%");
      vo.setUser(u);
      List<User> users = userDao.findByVo(vo);
      for (User user:users){
          System.out.println(user);
      }
  }
  ```


## 三、Mybatis 的输出结果封装

### 3.1、resultType 配置结果类型

- #### 示范

  ```java
  /**
  * Dao接口
  * 查询所有用户 * @return
  */
  List<User> findAll();
  ```

  ```xml
  <!-- 配置查询所有操作 -->
  <select id="findAll" resultType="com.itheima.domain.User">
      select * from user
  </select>
  ```

- #### 特殊情况示例：

  ##### domain的属性与数据库字段不一致，即对应不上！

  ```java
  // domain 后面是数据库对应字段
  public class User implements Serializable {
      private Integer userId;  // id
      private String userName; // username
      private Date userBirthday; // birthday
      private String userSex;  // sex
      private String userAddress; // address
  }
  ```

  ```java
  /**
  * Dao接口
  * 用户保存
  */
  void saveUser(User user);
  ```

  ```xml
  <insert id="saveUser" parameterType="com.eoony.domain.User">
      <selectKey keyProperty="userId" keyColumn="id" order="AFTER" resultType="Integer">
          select last_insert_id();
      </selectKey>
      insert into user(username,address,sex,birthday) values(#{userName},#{userAddress},#{userSex},#{userBirthday});
  </insert>
  ```

  > ##### 注意：sql对应的属性也要改变！！

  #### 问题：此时对应查询情况！如下：

  ```xml
  <!-- 没有变化 -->
  <select id="findAll" resultType="com.eoony.domain.User">
      select * from user;
  </select>
  ```

  > ##### 结果集：只有userName有值！！why？ 
  >
  > ##### 答：mysql在windows，mac下是不区分大小写的。所有能赋值，其余字段不一致就无法赋值！Linux是严格区分大小写的，都没有值。
  >
  > ```java
  > User{userId=null, userName='老王', userBirthday=null, userSex='null', userAddress='null'}
  > User{userId=null, userName='小二王', userBirthday=null, userSex='null', userAddress='null'}
  > User{userId=null, userName='小二王', userBirthday=null, userSex='null', userAddress='null'}
  > User{userId=null, userName='传智播客', userBirthday=null, userSex='null', userAddress='null'}
  > User{userId=null, userName='老王', userBirthday=null, userSex='null', userAddress='null'}
  > User{userId=null, userName='javascript', userBirthday=null, userSex='null', userAddress='null'}
  > User{userId=null, userName='老王', userBirthday=null, userSex='null', userAddress='null'}
  > ```

  #### 问题该如何解决？ 3种方案：

  ##### 1. 默认设置：将属性与数据库字段一一对应即可

  ##### 2. sql语句里面起别名: 效率最高（等同字段一致效率）

  ```xml
  <select id="findAll" resultType="com.eoony.domain.User">
      select id as userId,username as userName,address as userAddress,sex as userSex, birthday as userBirthday from user;
  </select>
  ```

  ##### 3. 在`<mapper>`定义一个`<resultMap>`，将domain的属性与数据库列依依对应即可！

  ```xml
  <resultMap id="userMap" type="com.eoony.domain.User">
      <!-- 主键用ID 标签 -->
      <id property="userId" column="id"/>
      <!-- 非主键用result标签 -->
      <result property="userName" column="username"/>
      <result property="userAddress" column="address"/>
      <result property="userSex" column="sex"/>
      <result property="userBirthday" column="birthday"/>
  </resultMap>
  
  <select id="findAll" resultMap="userMap">
      select * from user;
  </select>
  ```

  > ##### 注意：select标签，不再是resultType收集了，而是resultMap，与上面定义的对应；
  >
  > ##### 这样写，提高了开发效率，所有的维护都在IUserDao.xml！但是运行效率没有前面2种好！



## 四、自定义mybatis基于dao方式的实现（了解）

这个其实在上节讲了一个selectList方法实现。

#### 4.1、查询所有

```java
public class UserDaoImpl implements IUserDao {
    private SqlSessionFactory factory;
    public UserDaoImpl(SqlSessionFactory factory) { this.factory = factory;
                                                  }
    @Override
    public List<User> findAll() {
        SqlSession session = factory.openSession();
        List<User> users = session.selectList("com.itheima.dao.IUserDao.findAll");
        session.close();
        return users; 
    }
    ......
}    
```

#### 4.2、查询一个

```java
User user = session.selectOne("com.itheima.dao.IUserDao.findById",userId);
```

#### 4.3、保存

```java
int res = session.insert("com.itheima.dao.IUserDao.saveUser",user); 
session.commit();
```

#### 4.4、更新

```java
int res = session.update("com.itheima.dao.IUserDao.updateUser",user);
session.commit();
```

#### 4.5、删除

```java
int res = session.delete("com.itheima.dao.IUserDao.deleteUser",userId);
session.commit();
```

#### 4.6、聚合函数使用

```java
int res = session.selectOne("com.itheima.dao.IUserDao.findTotal");
```



## 五、mybatis实现分析

5.1、如何实现CRUD的？



5.2、代理dao是如何实现的？



## 六、SqlMapConfig.xml配置文件

#### SqlMapConfig.xml中配置的内容和顺序

```shell
-properties(属性) 
	--property
-settings(全局配置参数) 
	--setting
-typeAliases(类型别名) 
	--typeAliase
	--package
-typeHandlers(类型处理器)
-objectFactory(对象工厂) 
-plugins(插件) 
-environments(环境集合属性对象)
	--environment(环境子属性对象)
		---transactionManager(事务管理) 
		---dataSource(数据源)
-mappers(映射器) 
	--mapper
	--package
```

##### 下面主要介绍：properties、typeAliases、mappers三个配置项！！

### 6.1、properties(属性)

- #### 开始是如此配置mysql。

```xml
<configuration>
    <environments default="mysql">
        <environment id="mysql">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
            </dataSource>
        </environment>
    </environments>
</configuration>
```

- #### 使用properties属性，可以如下：

  ##### 第1种：

  ```xml
  <configuration>
      <properties>
          <property name="driver" value="com.mysql.jdbc.Driver"/>
          <property name="url" value="jdbc:mysql://localhost:3306/mybatis"/>
          <property name="username" value="root"/>
          <property name="password" value="root"/>
      </properties>
  
      <environments default="mysql">
          <environment id="mysql">
              <transactionManager type="JDBC"/>
              <dataSource type="POOLED">
                  <property name="driver" value="${driver}"/>
                  <property name="url" value="${url}"/>
                  <property name="username" value="${username}"/>
                  <property name="password" value="${password}"/>
              </dataSource>
          </environment>
      </environments>
  </configuration>
  ```

  ##### 第2种：添加properties文件

  ```properties
  #jdbcConfig.properties
  jdbc.driver=com.mysql.jdbc.Driver
  jdbc.url=jdbc:mysql://localhost:3306/mybatis
  jdbc.username=root
  jdbc.password=root
  ```

  - ##### resource属性操作：注意若properties带有jdbc.前缀，下面也要有前缀

  ```xml
  <configuration>
      <properties resource="jdbcConfig.properties"/>
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
  </configuration>
  ```

  - ##### url属性操作：file协议

  ```xml
  <properties resource="file:///Volumes/D/maven/day03_mybatis/src/main/resources/jdbcConfig.properties"/>
  ```


### 6.2、typeAliases(类型别名)

```xml
<typeAliases>
    <!-- 单个别名定义 -->
    <typeAlias alias="user" type="com.itheima.domain.User"/>
    <!-- 批量别名定义,扫描整个包下的类,别名为类名(首字母大写或小写都可以) -->
    <package name="com.itheima.domain"/> 
    <package name="其它包"/>
</typeAliases>
```

```xml
<!-- IUserDao.xml -->
<select id="findAll" resultType="USer">
    select * from user;
</select>
```

> 1. 配置别名后，在使用时就不区分大小写了！
> 2. package标签：代表包下的所有类全部带别名，不区分大小写；

 

### 6.3、mappers(映射器)

前面讲过，两个属性：

- ##### resource : 使用相对于类路径的资源

- ##### class : 使用 mapper 接口类路径

```xml
<mapper resource="com/itheima/dao/IUserDao.xml" />
<mapper class="com.itheima.dao.UserDao"/>
```

> ##### 两个作用等效！！！

- ##### Package属性: 注册指定包下的所有 mapper 接口

```xml
<package name="cn.itcast.mybatis.mapper"/>
```

> ##### 可以替换上面，同时可以简化多个操作dao配置！









