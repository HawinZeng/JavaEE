# 04_Mybatis缓存_注解开发

## 一、Mybatis 延迟加载策略

```properties
# 问题：在一对多中，当我们有一个用户，它有100个账户。
	在查询用户的时候，要不要把关联的账户查出来？
	在查询账户的时候，要不要把关联的用户查出来？

	在查询用户时，用户下的账户信息应该是，什么时候使用，什么时候查询的: 延迟加载
	在查询账户时，账户的所属用户信息应该是随着账户查询时一起查询出来: 立即加载
```

### 1.1、何为延迟加载?

```properties
延迟加载: 就是在需要用到数据时才进行加载,不需要用到数据时就不加载数据。延迟加载也称懒加载.  	

好处: 先从单表查询,需要时再从关联表去关联查询,大大提高数据库性能,因为查询单表要比关联查询多张表速度要快。

坏处: 因为只有当需要用到数据时,才会进行数据库查询,这样在大批量数据查询时,因为查询工作也要消耗时间,所以可能造成用户等待时间变长,造成用户体验下降。
```

### 1.2、什么是立即加载？

```properties
立即加载: 不管用不用，只要一调用方法，马上发起查询。
```

### 1.3、在对应的四种表关系中：加载情况如何？

```properties
一对多，多对多: 通常情况下我们都是采用延迟加载。
多对一，一对一: 通常情况下我们都是采用立即加载。
```

### 1.4、代码具体实现延迟策略

- #### 一对一：查询帐户及对应的用户（当只查账户，我们延迟不查询用户。）

```xml
<mapper namespace="com.eoony.dao.IAccountDao">
    <resultMap id="accountMap" type="account">
        <id property="ID" column="id" />
        <result property="UID" column="UID"/>
        <result property="MONEY" column="MONEY"/>
        <association property="user" column="uid" javaType="user" select="com.eoony.dao.IUserDao.findById"/>
    </resultMap>

    <select id="findAll" resultMap="accountMap">
        select * from account ;
        <!--select * from account;-->
    </select>
</mapper>   

<!-- 对应的IUserDao.xml配置findById查询语句 -->
<mapper namespace="com.eoony.dao.IUserDao">
    ...
    <select id="findById" parameterType="int" resultType="user">
        select * from user where id = #{uid};
    </select>
</mapper>
```

```java
public interface IUserDao {
    /**
     * 查找所有用户，并包含用户对应的账户
     */
    List<User> findAll();
	/**
     * 根据 account表的uid查找用户
     */
    User findById(int uid);
}
```

> 配置完，还需要主配置文件，添加延迟加载开关！
>
> ```xml
> <configuration>
>     <properties  url=".../resources/jdbcConfig.properties"/>
>     <!--要在properties标签下面，否则报错。主要标签依次顺序！！！-->
>     <settings>
>         <setting name="lazyLoadingEnabled" value="true"/>
>         <!--可以省略，3.4.1之后的版本默认为false-->
>         <setting name="aggressiveLazyLoading" value="false"/>
>     </settings>
> 	....
> </configuration>
> ```

- #### 一对多：查询用户及对应的账户（当只查用户，我们延迟不查询账户。）

```xml
<mapper namespace="com.eoony.dao.IUserDao">
    <resultMap id="userMap" type="uSer">
        <id property="id" column="id"/>
        <result property="username" column="username"/>
        <result property="address" column="address"/>
        <result property="sex" column="sex"/>
        <result property="birthday" column="birthday"/>
        <collection property="accounts" column="id" ofType="account" select="com.eoony.dao.IAccountDao.findByUserId">
        </collection>
    </resultMap>

    <select id="findAll" resultMap="userMap">
        select * from user ;
    </select>
</mapper>  

<!-- IAccountDao.xml配置findByUserId查询语句 -->
<mapper namespace="com.eoony.dao.IAccountDao">
	...
    <select id="findByUserId" parameterType="int" resultType="account">
         select * from account where uid = #{id};
    </select>
</mapper>
```

```java
public interface IAccountDao {
    List<Account> findAll();

    /**
     * 根据用户uid，查询所有账户
     * @param uid
     * @return
     */
    List<Account> findByUserId(int uid);
}
```



## 二、Mybatis中的缓存

- ##### 什么是缓存？

   存在于内存中的临时数据。

- ##### 为什么使用缓存?

  减少和数据库的交互次数，提高执行效率。

- ##### 什么样的数据能使用缓存，什么样的数据不能使用?

  ```properties
  适用于缓存: 1. 经常查询并且不经常改变的; 2. 数据的正确与否对最终结果影响不大的;
  不适用于缓存: 1. 经常改变的数据; 2. 数据的正确与否对最终结果影响很大的; (例如：商品的库存，银行的汇率，股市的牌价。)
  ```

### 2.1、Mybatis中的一级缓存

##### 它指的是Mybatis中SqlSession对象的缓存。

- 当我们执行查询之后，查询的结果会同时存入到SqlSession为我们提供一块区域中。
- 该区域的结构是一个Map。当我们再次查询同样的数据，mybatis会先去sqlsession中
- 查询是否有，有的话直接拿出来用。
- 当SqlSession对象消失时，mybatis的一级缓存也就消失了。

```java
    public void testFirstCache(){
        User user1 = userDao.findById(41);
        System.out.println(user1.hashCode());
//       1. 通过关闭session来清除缓存
//        sqlSession.close();
//        sqlSession = factory.openSession();
//        userDao = sqlSession.getMapper(IUserDao.class);
        
        // 2. 通过调用clearCache方法清除缓存，无需再new userDao
        sqlSession.clearCache();

        User user2 = userDao.findById(41);
        System.out.println(user2.hashCode());
        System.out.println(user1==user2);
    }
```

```java
  @Test
    public void testClearCache(){
        User user1 = userDao.findById(41);
        System.out.println(user1);
        
        user1.setUsername("alice clear cache");
        user1.setAddress("++++天门+++");
        userDao.updateUser(user1);

        System.out.println(user2);
        System.out.println(user1==user2);
    }
```

```xml
<!-- 若在select标签调用update语句，会直接更新数据库，同时更新缓存，user1==user2为true -->
<select id="updateUser" parameterType="user">
    update user set username=#{username},address=#{address} where id=#{id};
</select>

<update id="updateUser" parameterType="user">
    update user set username=#{username},address=#{address} where id=#{id};
</update>
```































