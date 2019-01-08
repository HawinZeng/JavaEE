# Spring框架_02

## 一、基于注解的 IOC 配置

上一节，主要讲IOC的xml配置！这一节将介绍IoC注解配置方式。

#### 回顾xml配置：

```xml
<bean id="accountService" class="com.itheima.service.impl.AccountServiceImpl"
      scope=""  init-method="" destroy-method="">
    <property name=""  value="" | ref=""></property>
</bean>
```

> #### 上面配置，主要有4个点：
>
> 1. ##### 用于创建对象的；
>
> 2. ```
>    在XML配置文件中编写一个<bean>标签实现的功能；
>    ```
>
> 3. ##### 用于注入数据的；
>
>    ```
>    在xml配置文件中的bean标签中写一个<property>标签的作用；
>    ```
>
> 4. ##### 用于改变作用范围的；
>
>    ```
>    在bean标签中使用scope属性实现的功能；
>    ```
>
> 5. ##### 和生命周期相关的（了解）；
>
>    ```
>    在bean标签中使用init-method和destroy-methode的作用；
>    ```



### 1.1、快速入门 —> 用于创建对象的:  Component注解）

```java
/**
* Component注解
* 作用：用于把当前类对象存入spring容器中
*     属性：value -> 用于指定bean的id。当我们不写时，它的默认值是当前类名，且首字母改小写。
*     
*	   Controller：一般用在表现层
*      Service：一般用在业务层
*      Repository：一般用在持久层
*      以上三个注解他们的作用和属性与Component是一模一样。
*      他们三个是spring框架为我们提供明确的三层使用的注解，使我们的三层对象更加清晰
*/
@Component("userService") 
public class UserServiceImpl implements IUserService {

    public UserServiceImpl(){
        System.out.println("创建UserServiceImpl对象");
    }

    public void save(){
        // userDao.save();
    }
}
```

> ##### 单独这样配置还是不行的！还需要修正bean.xml文件！！！
>
> ##### 需要添加xmlns:context这个约束，从而使用 `<context:component-scan.../>`标签！
>
> ```xml
> <?xml version="1.0" encoding="UTF-8"?>
> <beans xmlns="http://www.springframework.org/schema/beans"
>        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
>        xmlns:context="http://www.springframework.org/schema/context"
>        xsi:schemaLocation="http://www.springframework.org/schema/beans
>         http://www.springframework.org/schema/beans/spring-beans.xsd
>         http://www.springframework.org/schema/context
>         http://www.springframework.org/schema/context/spring-context.xsd">
> 
>     <context:component-scan base-package="com.eoony"/>
> </beans>
> ```

- #### 问题：如何创建带参数的构造器的对象？(待解决！)





### 1.2、用于注入数据的：Autowired、Qualifier、Resource注解

```java
/**
* Autowired:
*   作用：自动按照类型注入。只要容器中有唯一的一个bean对象类型和要注入的变量类型匹配，就可以注入成功
*          如果ioc容器中没有任何bean的类型和要注入的变量类型匹配，则报错。
*          如果Ioc容器中有多个类型匹配时：
*   出现位置：
*       可以是变量上，也可以是方法上
*   细节：
*       在使用注解注入时，set方法就不是必须的了。
*
* Qualifier:
*   作用：在按照类中注入的基础之上再按照名称注入。它在给类成员注入时不能单独使用(必须与Autowired配合使用！)。但是在给方法参数注入时可以！
*   属性：
*       value：用于指定注入bean的id。
*
* Resource (这个是javax.annotation的注解，spring利用了java的原始注解！)
*   作用：直接按照bean的id注入。它可以独立使用
*   属性：
*       name：用于指定bean的id。
*以上三个注入都只能注入其他bean类型的数据，而基本类型和String类型无法使用上述注解实现。
*      另外，集合类型的注入只能通过XML来实现。
*
* Value
*   作用：用于注入基本类型和String类型的数据
*   属性：
*      value：用于指定数据的值。它可以使用spring中SpEL(也就是spring的el表达式）
*             SpEL的写法：${表达式}
*/
@Component("userService") 
public class UserServiceImpl implements IUserService {
    public UserServiceImpl(){
        System.out.println("创建UserServiceImpl对象");
    }

//    @Autowired
//    @Qualifier("userDao2")
    @Resource(name = "userDao1")
    private IUserDao userDao;

    public void save(){
        userDao.save();
    }
}
```



### 1.3、用于改变作用范围的：Scope注解

```java
/**
 *      Scope
 *          作用：用于指定bean的作用范围
 *          属性：
 *              value：指定范围的取值。常用取值：singleton prototype
 */
@Component("userService")
@Scope("singleton")// prototype (这两个为主要)
public class UserServiceImpl implements IUserService {

    public UserServiceImpl(){
        System.out.println("创建UserServiceImpl对象");
    }
    ...
}
```



### 1.4、和生命周期相关的（了解）：

```java
@Component("userService")
@Scope("singleton")
public class UserServiceImpl implements IUserService {
	...
    @PostConstruct
    public void init(){
        System.out.println(" init 初始化了！！！！！！1");
    }

    @PreDestroy
    public void destroy(){
        System.out.println(" destroy 销毁了 。。。。。。2");
    }
}

public class Client {
    public static void main(String[] args) {
//        ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");

        IUserService service  = (IUserService) ac.getBean("userService");
        service.save();

        ac.close(); // 调用close，才会执行destroy。singleton同容器一并生存！
    }
}
```



## 二、xml IoC 案例

- #### 主配置文件－－bean.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="userService" class="com.eoony.service.impl.UserServiceImpl">
        <property name="userDao" ref="userDao"/>
    </bean>

    <bean id="userDao" class="com.eoony.dao.impl.UserDaoImpl">
        <property name="runner" ref="runner"/>
    </bean>

     <!--runner配置，多例，防止多个dao调用，线程干扰-->
    <bean id="runner" class="org.apache.commons.dbutils.QueryRunner" scope="prototype">
        <constructor-arg name="ds" ref="dataSource"/>
    </bean>

    <!--<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">-->
        <!--<property name="driverClass" value="com.mysql.jdbc.Driver"/>-->
        <!--<property name="jdbcUrl" value="jdbc:mysql://localhost:3306/day03_mybatis?useUnicode=true&amp;characterEncoding=UTF-8"/>-->
        <!--<property name="user" value="root"/>-->
        <!--<property name="password" value="root"/>-->
    <!--</bean>-->

    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/day03_mybatis?useUnicode=true&amp;characterEncoding=UTF-8"/>
        <property name="username" value="root"/>
        <property name="password" value="root"/>
    </bean>
</beans>
```

> ##### runner 若是单例，多个dao调用runner，容易引发［线程干扰］！（？）
>
> ##### 由于不能dao的runner需要独立，不能共享，若共享，一些缓存数据可能会造成其它查询出现误差！
>
> ##### 若多个service调用dao，这个dao肯定是共享的，否则就dao就没有意义了！所以，dao设置单例为佳，而runner要设置为多例！（不一定要并发！！！）

```java
// 1. dao
public class UserDaoImpl implements IUserDao {
    private QueryRunner runner;
    public void setRunner(QueryRunner runner) {
        this.runner = runner;
    }
    
    public List<User> findAll() {
        ...
    }
    
    public User findOne(Integer id) {
       ...
    }
    ...
}

// 2. service
public class UserServiceImpl implements IUserService {
    private IUserDao userDao;
    public void setUserDao(IUserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public User findOne(Integer id) {
        return userDao.findOne(id);
    }
}

// 3. 调用
public class UserServiceTest {
    private IUserService service;

    @Before
    public void init(){
        ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
         service = (IUserService) ac.getBean("userService");
    }

    @Test
    public void testFindAll(){
        List<User> users = service.findAll();
        for (User user : users){
            System.out.println(user);
        }
    }

    @Test
    public void testFindOne(){
        User one = service.findOne(55);
        System.out.println(one);
    }
    ...
}
```



## 三、注解IoC案例

- #### 主配置文件－－bean.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

        <context:component-scan base-package="com.eoony"/>
    <!--runner配置，多例，防止多个dao调用，线程干扰-->
    <bean id="runner" class="org.apache.commons.dbutils.QueryRunner" scope="prototype">
        <constructor-arg name="ds" ref="dataSource"/>
    </bean>

    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/day03_mybatis?useUnicode=true&amp;characterEncoding=UTF-8"/>
        <property name="username" value="root"/>
        <property name="password" value="root"/>
    </bean>
</beans>
```

> ##### 由于QueryRunner，DruidDataSource在工程com.eoony包下没有继承子类，所有依然还是需要在xml配置引用过来。若不引用过来，相当于需要修改对应jar包了！
>
> ##### 可以参考后面新注解的解决办法！

```java
@Repository("userDao")
public class UserDaoImpl implements IUserDao {

    @Resource(name = "runner") // 不需要set方法了！！
    private QueryRunner runner;

    public List<User> findAll() {
        ...
    }

    public User findOne(Integer id) {
        ...
    }
    ...
}

@Service("userService")
public class UserServiceImpl implements IUserService {
    @Resource(name = "userDao")
    private IUserDao userDao;

    public List<User> findAll() {
        return userDao.findAll();
    }

    public User findOne(Integer id) {
        return userDao.findOne(id);
    }
    ...
}
```



## 四、新注解IoC配置(纯注解方式)

前面注解案例，遇到的问题。

1. 不在我们工程类是无法利用注解！
2. 注解还是依赖了bean.xml！

问题该如何解决？

### 4.1、新建一个配置类 

##### SpringConfiguration（类名随意，不与扫描包冲突即可）

#### spring中的新注解:

- Configuration : 指定当前类是一个配置类

  ```java
  /**
  *  1. 当另一个配置类是，主配置类的ComponentScan注解参数时，必须带@Configuration，不能省略
  */
  //@Configuration
  @ComponentScan(basePackages = {"com.eoony","config"})
  public class SpringConfiguration {
  
  }
  
  @Configuration
  public class JdbcConfig {
  
      @Bean(name = "runner")
      @Scope("prototype")
      public QueryRunner createQueryRunner(DataSource source){
          return new QueryRunner(source);
      }
  	...
  }
  ```

  ```java
  /**
  *  2. 下面方式调用，JdbcConfig的configuration也可以省略
  */
  public class UserServiceTest {
  
      private IUserService service;
  
      @Before
      public void init(){
          ApplicationContext ac = new AnnotationConfigApplicationContext(SpringConfiguration.class, JdbcConfig.class);
  
          service = (IUserService) ac.getBean("userService");
      }
      ...
  }
  ```

- ComponentScan : 用于通过注解指定spring在创建容器时要扫描的包

- Bean : 用于把当前方法的返回值作为bean对象存入spring的ioc容器中

- Import : 用于导入其他的配置类

- PropertySource : 用于指定properties文件的位置

```java
/**
 * 该类是一个配置类，它的作用和bean.xml是一样的
 * spring中的新注解
 * Configuration
 *     作用：指定当前类是一个配置类
 *     细节：当配置类作为AnnotationConfigApplicationContext对象创建的参数时，该注解大多情况下可以不写。 但是有些情况必须写上！
 
 * ComponentScan
 *      作用：用于通过注解指定spring在创建容器时要扫描的包
 *      属性：
 *          value：它和basePackages的作用是一样的，都是用于指定创建容器时要扫描的包。
 *                 我们使用此注解就等同于在xml中配置了:
 *                      <context:component-scan base-package="com.itheima"/>
 
 *  Bean
 *      作用：用于把当前方法的返回值作为bean对象存入spring的ioc容器中
 *      属性:
 *          name:用于指定bean的id。当不写时，默认值是当前方法的名称
 *      细节：
 *          当我们使用注解配置方法时，如果方法有参数，spring框架会去容器中查找有没有可用的bean对象。
 *          查找的方式和Autowired注解的作用是一样的
 
 *  Import
 *      作用：用于导入其他的配置类
 *      属性：
 *          value：用于指定其他配置类的字节码。
 *                  当我们使用Import的注解之后，有Import注解的类就父配置类，而导入的都是子配置类
 
 *  PropertySource
 *      作用：用于指定properties文件的位置
 *      属性：
 *          value：指定文件的名称和路径。
 *                  关键字：classpath，表示类路径下
 */
//@Configuration
@ComponentScan("com.itheima")
@Import(JdbcConfig.class)
//@PropertySource("classpath:jdbcConfig.properties")
@PropertySource("classpath:/././jdbcConfig.properties") // 带包的情况
public class SpringConfiguration {


}
```

```java
public class JdbcConfig {

    @Value("${jdbc.driver}")
    private String driver;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Bean(name = "runner")
    @Scope("prototype")
    public QueryRunner createQueryRunner(@Qualifier("dataSource1") DataSource dataSource2){ // 若没有@Qualifier注解，会使用dataSource2的数据源，若使用了，则使用指定的数据源
        return new QueryRunner(dataSource2);
    }

    @Bean(name = "dataSource1")
    public DataSource createDataSource1(){
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    @Bean(name = "dataSource2")
    public DataSource createDataSource2(){
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/mybatis?useUnicode=true&characterEncoding=UTF-8");
        ds.setUsername("root");
        ds.setPassword("root");
        return ds;
    }
}
```

> 1. 纯xml比较繁琐！
> 2. 纯注解，也没有省事！
> 3. 一般推荐：引入的jar包使用xml，自己工程的类使用注解。这样方便！（混合模式）



## 五、Spring和Junit整合

```properties
1、应用程序的入口
	main方法
2、junit单元测试中，没有main方法也能执行
	junit集成了一个main方法
	该方法就会判断当前测试类中哪些方法有 @Test注解
	junit就让有Test注解的方法执行
3、junit不会管我们是否采用spring框架
	在执行测试方法时，junit根本不知道我们是不是使用了spring框架
	所以也就不会为我们读取配置文件/配置类创建spring核心容器
4、由以上三点可知
	当测试方法执行时，没有Ioc容器，就算写了Autowired注解，也无法实现注入
```

```java
/**
 * 使用Junit单元测试：测试我们的配置
 * Spring整合junit的配置
 *      1、导入spring整合junit的jar(坐标)
 *      2、使用Junit提供的一个注解把原有的main方法替换了，替换成spring提供的
 *             @Runwith
 *      3、告知spring的运行器，spring和ioc创建是基于xml还是注解的，并且说明位置
 *          @ContextConfiguration
 *                  locations：指定xml文件的位置，加上classpath关键字，表示在类路径下
 *                  classes：指定注解类所在地位置
 *
 *   当我们使用spring 5.x版本的时候，要求junit的jar必须是4.12及以上
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfiguration.class)
public class UserServiceTest {
    @Autowired
    private IUserService service = null;

    @Test
    public void testFindAll(){
        List<User> users = service.findAll();
        for (User user : users){
            System.out.println(user);
        }
    }

    @Test
    public void testFindOne(){
        User one = service.findOne(55);
        System.out.println(one);
    }
}

/**
* xml方式
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:bean.xml")
public class UserServiceTest {
    @Autowired
    private IUserService service;
    
    ...
}
```

```XML
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>5.0.2.RELEASE</version>
    </dependency>
    <!--导入spring整合junit的jar(坐标)-->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-test</artifactId>
        <version>5.0.2.RELEASE</version>
        <scope>test</scope>
    </dependency>
    ...
</dependencies>
```

































