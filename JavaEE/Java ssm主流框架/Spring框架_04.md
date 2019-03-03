# Spring框架_04

## 一、spring中的JdbcTemplate

#### 1.1、JdbcTemplate的作用：

​		它就是用于和数据库交互的，实现对表的CRUD操作；

#### 1.2、JdbcTemplate CRUD：

```java
public class Demo2 {

    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        JdbcTemplate jt = ac.getBean("template",JdbcTemplate.class);

        // 1. 查询所有
//        List<Account> accounts = jt.query(" select * from account where money > ? ", new AccountRowMapper(), 1000);
        List<Account> accounts = jt.query(" select * from account where money > ? ", new BeanPropertyRowMapper<Account>(Account.class), 1000);
        for (Account a: accounts) {
            System.out.println(a);
        }
        
        // 2. 查询一个，可用上面的查询集合，取第一个元素即可。也可以使用下面的queryForObject方法
        Account account = jt.queryForObject(" select * from account where id=? ", new BeanPropertyRowMapper<Account>(Account.class), 5);
        System.out.println(account);
        
        // 3. 更新
        jt.update(" update account set name=?,money=? where id=?", "niu",4000,3);
        
        // 4. 删除
        jt.update(" delete from account where id=? ",3 );
        
        // 5. 添加
        jt.update(" insert into account (name,money) values (?,?) ","jeo",5000);
        
        // 6. 聚合函数 -- 一般使用Long类型接收，因为数据超出int范围就会出错！
        Long count = jt.queryForObject(" select count(*) from account ",Long.class);
        System.out.println(count);
        
        // 7. 扩充说明 (queryForList只能对1列数据进行封装，所以实际作用不大)
        List<String> names = jt.queryForList(" select name from account where money > ? ", String.class, 1000);
        for(String name:names){
            System.out.println(name);
        }
    }
}

/**
 * 自定义结果集封装。但是一般情况，都用spring提供的BeanPropertyRowMapper对结果集进行封装
 */
class AccountRowMapper implements RowMapper<Account>{
    @Override
    public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
        Account account = new Account();
        account.setId(rs.getInt("id"));
        account.setName(rs.getString("name"));
        account.setMoney(rs.getFloat("money"));
        return account;
    }
}
```

> ##### dbutils对结果集封装是一个统一的：`ResultSetHandler<T>` . 注意与JdbcTemplate的区别，JdbcTemplate可以使用不同方法对不同结果集进行封装！



### 1.3、JdbcTemplate的dao层封装

```java
public class AccountDaoImpl extends JdbcDaoSupport implements IAccountDao {

    @Override
    public List<Account> findAll() {
        List<Account> accounts = getJdbcTemplate().query(" select * from account ", new BeanPropertyRowMapper<Account>(Account.class));
        return accounts;
    }

    @Override
    public Account findOne(int id) {
        Account account = getJdbcTemplate().queryForObject(" select * from account where id=? ", new BeanPropertyRowMapper<>(Account.class), id);
        return account;
    }

	...
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="accountDao" class="com.eoony.dao.impl.AccountDaoImpl">
        <!--<property name="template" ref="template"/>-->
        <property name="dataSource" ref="dmd"/>
    </bean>

    <bean id="dmd" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/spring"/>
        <property name="username" value="root"/>
        <property name="password" value="root"/>
    </bean>

    <!--<bean id="template" class="org.springframework.jdbc.core.JdbcTemplate">-->
        <!--<property name="dataSource" ref="dmd"/>-->
    <!--</bean>-->
</beans>
```

> 继承JdbcDaoSupport：省去了公共代码，适合xml IoC配置。但是若是注解IoC就有点麻烦。
>
> 不继承JdbcDaoSupport：无法省去公共代码，但是适合注解配置IoC，也相当于简化了代码；参考如下：
>
> ```java
> @Repository("accountDao2") // 就是注解，xml混合搭配
> public class AccountDao2Impl implements IAccountDao {
> 
>     @Autowired
>     private JdbcTemplate template;
> //    public void setTemplate(JdbcTemplate template) {
> //        this.template = template;
> //    }
> 
>     @Override
>     public List<Account> findAll() {
>         List<Account> accounts = template.query(" select * from account ", new BeanPropertyRowMapper<Account>(Account.class));
>         return accounts;
>     }
> }
> ```
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
> 
>     <!--<bean id="accountDao1" class="com.eoony.dao.impl.AccountDaoImpl">-->
>         <!--&lt;!&ndash;<property name="template" ref="template"/>&ndash;&gt;-->
>         <!--<property name="dataSource" ref="dmd"/>-->
>     <!--</bean>-->
> 
>     <bean id="dmd" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
>         <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
>         <property name="url" value="jdbc:mysql://localhost:3306/spring"/>
>         <property name="username" value="root"/>
>         <property name="password" value="root"/>
>     </bean>
> 
>     <bean id="template" class="org.springframework.jdbc.core.JdbcTemplate">
>         <property name="dataSource" ref="dmd"/>
>     </bean>
> </beans>
> ```



## 二、Spring 中的事务控制

### 2.1、Spring 事务控制我们要明确的

- 第一、JavaEE 体系进行分层开发,事务处理位于业务层,Spring 提供了分层设计业务层的事务处理解决方 案。  

- 第二、spring 框架为我们提供了一组事务控制的接口。具体在后面的第二小节介绍。这组接口是在 spring-tx-5.0.2.RELEASE.jar 中。 

- 第三、spring 的事务控制都是基于 AOP 的,它既可以使用编程的方式实现,也可以使用配置的方式实现。**我 们学习的重点是使用配置的方式实现。**  							 						 

### 2.2、Spring 中事务控制的 API 介绍

- #### PlatformTransactionManager接口

  是 spring 的事务管理器,它里面提供了我们常用的操作事务的方法；

```java
// 获取事务状态信息
TransactionStatus getTransaction(TransactionDefinition definition);

// 提交事务
void commit(TransactionStatus status)
    
// 回滚事务
void rollback(TransactionStatus status) 
```

> 真正管理事务的对象
>
> ```java
> // 使用 Spring JDBC 或 iBatis 进行持久化数据时使用
> import org.springframework.jdbc.datasource.DataSourceTransactionManager 
> public class DataSourceTransactionManager extends AbstractPlatformTransactionManager
> 		implements ResourceTransactionManager, InitializingBean {
> 	private DataSource dataSource;
> 	private boolean enforceReadOnly = false;
> 	...
> }
> 
> // 使用 Hibernate 版本进行持久化数据时使用
> org.springframework.orm.hibernate5.HibernateTransactionManager 
> ```

- #### TransactionDefinition接口

```java
// 获取事务对象名称
String getName()；

// 获取事务隔离级别: 反映事务提交并发访问时的处理态度
int getIsolationLevel();
	//- ISOLATION_DEFAULT: 默认级别，归属下列某一种(当前数据库的隔离级别)
	//- ISOLATION_READ_UNCOMMITTED: 可以读取未提交数据
	//- ISOLATION_READ_COMMITTED: 只能读取已提交数据，解决脏读问题(Oracle默认级别)
	//- ISOLATION_REPEATABLE_READ: 是否读取其他事务提交修改后的数据，解决不可重复读问题(MySQL默认)
	//- ISOLATION_SERIALIZABLE: 是否读取其他事务提交添加后的数据，解决幻影读问题

// 获取事务传播行为
int getPropagationBehavior();
	//- REQUIRED:如果当前没有事务,就 新建一个事务,如果已经存在一个事务中,加入到这个事务 中。一般的选择(默认值)，增删改的选择，表示一定有事务
	//- 

// 获取事务超时时间
int getTimeout();

// 获取事务是否只读 -- 查询时设置为只读！
boolean isReadOnly();
```

- #### TransactionStatus接口

```

```



### 2.3、基于 XML 的声明式事务控制(配置方式) －重点！！！！

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- 配置业务层-->
    <bean id="accountService" class="com.itheima.service.impl.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"></property>
    </bean>

    <!-- 配置账户的持久层-->
    <bean id="accountDao" class="com.itheima.dao.impl.AccountDaoImpl">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!-- 配置数据源-->
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"></property>
        <property name="url" value="jdbc:mysql://localhost:3306/eesy"></property>
        <property name="username" value="root"></property>
        <property name="password" value="1234"></property>
    </bean>

    <!-- spring中基于XML的声明式事务控制配置步骤
        1、配置事务管理器
        2、配置事务的通知
                此时我们需要导入事务的约束 tx名称空间和约束，同时也需要aop的
                使用tx:advice标签配置事务通知
                    属性：
                        id：给事务通知起一个唯一标识
                        transaction-manager：给事务通知提供一个事务管理器引用
        3、配置AOP中的通用切入点表达式
        4、建立事务通知和切入点表达式的对应关系
        5、配置事务的属性
               是在事务的通知tx:advice标签的内部

     -->
    <!-- 配置事务管理器 -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!-- 配置事务的通知-->
    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <!-- 配置事务的属性
                isolation：用于指定事务的隔离级别。默认值是DEFAULT，表示使用数据库的默认隔离级别。
                propagation：用于指定事务的传播行为。默认值是REQUIRED，表示一定会有事务，增删改的选择。查询方法可以选择SUPPORTS。
                read-only：用于指定事务是否只读。只有查询方法才能设置为true。默认值是false，表示读写。
                timeout：用于指定事务的超时时间，默认值是-1，表示永不超时。如果指定了数值，以秒为单位。
                rollback-for：用于指定一个异常，当产生该异常时，事务回滚，产生其他异常时，事务不回滚。没有默认值。表示任何异常都回滚。
                no-rollback-for：用于指定一个异常，当产生该异常时，事务不回滚，产生其他异常时事务回滚。没有默认值。表示任何异常都回滚。
        -->
        <tx:attributes>
            <tx:method name="*" propagation="REQUIRED" read-only="false"/>
            <tx:method name="find*" propagation="SUPPORTS" read-only="true"></tx:method>
        </tx:attributes>
    </tx:advice>

    <!-- 配置aop-->
    <aop:config>
        <!-- 配置切入点表达式-->
        <aop:pointcut id="pt1" expression="execution(* com.itheima.service.impl.*.*(..))"></aop:pointcut>
        <!--建立切入点表达式和事务通知的对应关系 -->
        <aop:advisor advice-ref="txAdvice" pointcut-ref="pt1"></aop:advisor>
    </aop:config>
</beans>
```



### 2.4、基于 注解 的声明式事务控制(配置方式)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">
	<!-- 配置spring创建容器时要扫描的包-->
    <context:component-scan base-package="com.eoony"/>
	<!-- 配置JdbcTemplate-->
    <bean id="template" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dmd"/>
    </bean>
 	<!-- 配置数据源-->
    <bean id="dmd" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/spring"/>
        <property name="username" value="root"/>
        <property name="password" value="root"/>
    </bean>
 	<!-- spring中基于注解 的声明式事务控制配置步骤
        1、配置事务管理器
        2、开启spring对注解事务的支持
        3、在需要事务支持的地方使用@Transactional注解
     -->
    <!-- 配置事务管理器 -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dmd"/>
    </bean>
	<!--开启spring对注解事务的支持-->
    <tx:annotation-driven transaction-manager="transactionManager"/>
</beans>
```

```java
// 注解事务在类上声明后，还需要在多个方法设置声明，因为属性不同。 所以相比，xml配置就简单很多
@Service("accountService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class AccountServiceImpl implements IAccountService {
    @Autowired
    private IAccountDao accountDao;

    @Override
    public Account findAccountById(Integer accountId) {
        return accountDao.findOne(accountId);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void transfer(String sourceName, String targetName, Float money) {
        System.out.println("transfer....");
            //2.1根据名称查询转出账户
            Account source = accountDao.findAccountByName(sourceName);
            //2.2根据名称查询转入账户
            Account target = accountDao.findAccountByName(targetName);
            //2.3转出账户减钱
            source.setMoney(source.getMoney()-money);
            //2.4转入账户加钱
            target.setMoney(target.getMoney()+money);
            //2.5更新转出账户
            accountDao.updateAccount(source);
//            int i=1/0;
            //2.6更新转入账户
            accountDao.updateAccount(target);
    }
}
```

> #### 注意：若是简单的事务控制，使用注解配置，也省去了AOP相关配置及jar引入！



- ### 扩展：1. 纯注解

```java
@Configuration
@ComponentScan("com.eoony")
@Import({JdbcConfig.class,TransactionConfig.class})
@PropertySource("classpath:jdbcConfig.properties")
@EnableTransactionManagement
public class SpringConfiguration {

}

public class JdbcConfig {

    @Value("${jdbc.driver}")
    private String driver;
    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate createJdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    @Bean(name="dataSource")
    public DataSource createDataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}

public class TransactionConfig {

    @Bean(name="transactionManager")
    public PlatformTransactionManager createTransactionManager(DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }
}

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfiguration.class)
public class AccountDaoTest {

    @Autowired
    private IAccountService accountService;

    @Test
    public void testTransfer(){
        accountService.transfer("eee","fff",100f);
    }
}
```

- #### 扩展：2. 编程式调用Spring的事务管理 

  （不推荐使用，因为又会增加重复代码，不符合AOP优化思想了）

```java
public class AccountServiceImpl implements IAccountService{
    private IAccountDao accountDao;
    private TransactionTemplate transactionTemplate;

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public void setAccountDao(IAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public Account findAccountById(Integer accountId) {
      return  transactionTemplate.execute(new TransactionCallback<Account>() {
            @Override
            public Account doInTransaction(TransactionStatus status) {
                return accountDao.findAccountById(accountId);
            }
        });
    }

    @Override
    public void transfer(String sourceName, String targetName, Float money) {
        transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                System.out.println("transfer....");
                //2.1根据名称查询转出账户
                Account source = accountDao.findAccountByName(sourceName);
                //2.2根据名称查询转入账户
                Account target = accountDao.findAccountByName(targetName);
                //2.3转出账户减钱
                source.setMoney(source.getMoney()-money);
                //2.4转入账户加钱
                target.setMoney(target.getMoney()+money);
                //2.5更新转出账户
                accountDao.updateAccount(source);
//                int i=1/0;
                //2.6更新转入账户
                accountDao.updateAccount(target);
                return null;
            }
        });
    }
}
```



## 三、Spring5的新特性

##### 3.1、JDK8 效率比JDK7 高100倍！

注意：idea JDK版本切换！

> ##### 为什么Idea引入依赖，工程module 编译默认就是jdk5？？

##### 3.2、其他参考资料！

- 核心容器的更新

  Spring Framework 5.0 现在支持候选组件索引作为类路径扫描的替代方案。该功能已经在类路径扫描器中
  添加,以简化添加候选组件标识的步骤。

  从索引读取实体而不是扫描类路径对于小于 200 个类的小型项目是没有明显差异。但对大型项目影响较大。
  加载组件索引开销更低。因此,随着类数的增加,索引读取的启动时间将保持不变。

- junit5
- kotlin
- 响应式编程
- 依赖类库的更新













