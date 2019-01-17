# SpringMVC框架_03

#### 前提：保证各个框架先能自行跑起来！

#### 原则：spring整合springMVC，spring整合Mybatis！

## 一、SSM整合

#### 1、搭建整合环境

- ##### 整合说明:SSM整合可以使用多种方式。咱们会选择XML + 注解的方式; 

- ##### 整合的思路

  ```
  1. 先搭建整合的环境
  2. 先把Spring的配置搭建完成
  3. 再使用Spring整合SpringMVC框架 
  4. 最后使用Spring整合MyBatis框架
  ```

- ##### 创建数据库和表结构

  ```sql
  create database if not exists ssm default character set utf8;
  use ssm_web;
  create table account(
  	id int primary key auto_increment,
  	name varchar(30) not null,
  	money double default null
  ) Engine=InnoDB default charset=utf8;
  ```

- ##### 创建Maven工程 ： 导入相关依赖

- ##### 编写相关代码，domain，dao，service，controller



## 二、Spring框架代码的编写

### 1、搭建和测试Spring的开发环境 -- (主要就是IoC， AOP，tx)

##### Ioc：控制反转 （不直接创建对象，而是通过Spring的工厂模式注入，达到解耦，减少依赖！）

##### AOP：代理增强－－ 对应的应用就是：事务声明管理 tx

- ##### 在ssm_web项目中创建applicationContext.xml的配置文件,编写具体的配置信息。

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!--开启注解扫描-->
    <context:component-scan base-package="com.eoony">
        <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
    </context:component-scan>

    <!--1. spring整合mybatis-->
    
    <!--第一步：配置连接池-->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="com.mysql.jdbc.Driver"/>
        <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/ssm?useUnicode=true&amp;characterEncoding=UTF-8"/>
        <property name="user" value="root"/>
        <property name="password" value="root"/>
    </bean>

    <!--第二步：配置SqlSessionFactory工厂-->
    <bean id="factory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!--第三步：配置IAccountDao接口所在包-->
    <bean id="mapperScanner" class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="basePackage" value="com.eoony.dao"/>
    </bean>

    <!--2. spring声明事务管理-->
    <!--2.1 配置事务管理器-->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!--2.2 配置事务通知-->
    <tx:advice id="txAdvice">
        <tx:attributes>
            <tx:method name="find*" propagation="SUPPORTS" read-only="true"/>
            <tx:method name="*"/>
        </tx:attributes>
    </tx:advice>

    <!--2.3 配资AOP增强-->
    <aop:config>
        <aop:pointcut id="pt1" expression="execution(* com.eoony.service.impl.*.*(..))"/>
        <aop:advisor advice-ref="txAdvice" pointcut-ref="pt1"/>
    </aop:config>
</beans>
```



## 三、Spring整合SpringMVC框架

### 1、搭建和测试SpringMVC的开发环境

- ##### web.xml配置 -- (spring 整合springMVC主要将此文件修正)

  - 添加监听器：将spring的配置加入Tomcat中来；如同springmvc.xml加入tomcat一样，在DispatcherServlet初始化时添加进来；
  - 默认情况是找不到resources目录的applicationContext.xml，所以需要配置context-param参数；

```xml
<!DOCTYPE web-app PUBLIC
 "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
 "http://java.sun.com/dtd/web-app_2_3.dtd" >

<web-app>
  <display-name>Archetype Created Web Application</display-name>
  
  <filter>
    <filter-name>characterEncodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
      <param-name>encoding</param-name>
      <param-value>UTF-8</param-value>
    </init-param>
    <init-param>
      <param-name>forceEncoding</param-name>
      <param-value>true</param-value>
    </init-param>
  </filter>

  <filter-mapping>
    <filter-name>characterEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>

  <!--配置spring的监听器, 默认只加载WEB-INF目录下的applicationContext.xml配置文件-->
  <listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
  </listener>
  <!--设置配置文件的路径-->
  <context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:applicationContext.xml</param-value>
  </context-param>

  <servlet>
    <servlet-name>dispatcherServlet</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>classpath:springmvc.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
  </servlet>
  <servlet-mapping>
    <servlet-name>dispatcherServlet</servlet-name>
    <url-pattern>/</url-pattern>
  </servlet-mapping>
</web-app>
```

- ##### springmvc.xml主配置文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <!--开启注解扫描: 只扫描controller目录下的controller对象-->
    <context:component-scan base-package="com.eoony">
        <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
    </context:component-scan>

    <!--配置视图解析器-->
    <bean id="internalResourceViewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/pages/"/>
        <property name="suffix" value=".jsp"/>
    </bean>

    <!--静态资源不过滤-->
    <mvc:resources mapping="/css/" location="/css/**"/>
    <mvc:resources mapping="/js/" location="/js/**"/>
    <mvc:resources mapping="/images/" location="/images/**"/>

    <!--开启springMVC注解的支持-->
    <mvc:annotation-driven />
</beans>
```

- ##### controller代码

```java
@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private IAccountService as;

    @RequestMapping("/findAll")
    public String findAll(Model model){
        System.out.println("表现层、查询所有的账户信息.....");
        List<Account> list = as.findAll();
        model.addAttribute("list",list);
        return "list";
    }

    @RequestMapping("/save")
    public String saveAccount(Account account){
        System.out.println("表现层、保存的账户信息.....");
        as.saveAccount(account);
        return "success";
    }

     /**
     * 也可以重定向
     */
    @RequestMapping("/save")
    public void saveAccount(Account account, HttpServletRequest request, HttpServletResponse response) throws IOException {
        as.saveAccount(account);
        response.sendRedirect(request.getContextPath()+"account/findAll");
    }
}
```



## 四、Spring整合MyBatis框架

### 1、搭建和测试MyBatis的环境 （采用注解方式）

- ##### IAccountDao

```java
@Repository
public interface IAccountDao {

    @Select(" select * from account ")
    List<Account> findAll();

    @Insert(" insert into account(name,money) values(#{name},#{money}) ")
    void saveAccount(Account account);
}
```

- ##### SqlMapConfig.xml主配置文件（整合到Spring后，可以删除）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!--配置环境-->
    <environments default="mysql">
        <environment id="mysql">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/ssm?useUnicode=true&amp;characterEncoding=UTF-8"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
            </dataSource>
        </environment>
    </environments>

    <mappers>
        <!--<mapper class="com.eoony.dao.IAccountDao"/>-->
        <package name="com.eoony.dao"/>
    </mappers>
</configuration>
```

- ##### service代码

```java

@Service("accountService")
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private IAccountDao accountDao;

    @Override
    public List<Account> findAll() {
        System.out.println("findAll 执行了！！！！");
        return accountDao.findAll();
    }

    @Override
    public void saveAccount(Account account) {
        accountDao.saveAccount(account);
    }
}
```

- ##### 测试代码

```java
public class AccountDaoTest {

    private IAccountDao dao;
    private SqlSession sqlSession;

    @Before
    public void init() throws IOException {
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(Resources.getResourceAsStream("SqlMapConfig.xml"));
         sqlSession = factory.openSession(true); // 这里才是自动提交
//        sqlSession.commit(false); // 这个方法不能实现自动提交，不管是true、还是false
         dao = sqlSession.getMapper(IAccountDao.class);
    }

    @After
    public void release(){
        sqlSession.close();
    }


    @Test
    public void testFindAll() throws IOException {
        List<Account> accounts = dao.findAll();
        for (Account account:accounts){
            System.out.println(account);
        }
    }

    @Test
    public void testSave(){
        Account account = new Account();
        account.setName("王五");
        account.setMoney(1000D);

        dao.saveAccount(account);
    }
}

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class AccountServiceTest {
    @Autowired
    private IAccountService as;

    @Test
    public void testFindAll(){
        as.findAll();
    }
}
```

