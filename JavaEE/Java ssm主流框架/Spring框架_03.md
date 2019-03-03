# Spring框架_03

## 一、银行转账案例分析

```java
public void transfer(String sourceName, String targetName, Float money) {
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
    int i = 1/0;
    accountDao.updateAccount(target);
}

public class AccountDaoImpl implements IAccountDao {
    private QueryRunner runner;
    public void setRunner(QueryRunner runner) {
        this.runner = runner;
    }

    public void updateAccount(Account at) {
        try {
            runner.update(" update account set name=?,money=? where id=? ",at.getName(),at.getMoney(),at.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Account findAccountByName(String name) {
        try{
            List<Account> accounts = runner.query("select * from account where name = ? ",new BeanListHandler<Account>(Account.class),name);
            if(accounts == null || accounts.size() == 0){
                return null;
            }
            if(accounts.size() > 1){
                throw new RuntimeException("结果集不唯一，数据有问题");
            }
            return accounts.get(0);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    ...
}
```

#### 上述代码，转账过程发生异常，就会导致转账出错！why？

> ```java
> // 1. 相当于runner调用一次query，获取第1次connection，然后释放。
> Account source = accountDao.findAccountByName(sourceName);
> // 2. 相当于runner调用一次query，获取第2次connection，然后释放。
> Account target = accountDao.findAccountByName(targetName);
> // 3. 相当于runner调用一次update，获取第3次connection，然后释放。
> accountDao.updateAccount(source);
> int i = 1/0;
> // 4. 相当于runner调用一次update，获取第4次connection，然后释放。
> accountDao.updateAccount(target);
> ```
>
> 也就是，上述操作进行了4次完整的事务操作，相互独立了！才致使转账发生异常。其应该是，将整个操作放在一个事务中进行执行，而不是各个相互独立的事务。
>
> 那么该如何做？进过分析，上传4个操作，进行了4次connection获取，那么我们将修正为1次connection获取操作即可！

#### 步骤：

第一步、将connection与当前线程TreadLocal进行绑定；

```java
public class ConnectionUtils {
    private ThreadLocal<Connection> tl = new ThreadLocal<Connection>();
    private DataSource ds;
    public void setDs(DataSource ds) {
        this.ds = ds;
    }

    public Connection getThreadLocalConnection(){
        try {
            Connection conn = tl.get();
            if(conn == null) {
                conn = ds.getConnection();
                tl.set(conn);
            }
            return conn;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 把连接与线程解绑
     */
    public void removeConnection(){
        tl.remove();
    }
}
```

第二步、编写事务操作类

```java
public class TransactionManager {
    private ConnectionUtils connectionUtils;
    public void setConnectionUtils(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }

    public void beginTransaction(){
        try{
            connectionUtils.getThreadLocalConnection().setAutoCommit(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void commit(){
        try{
            connectionUtils.getThreadLocalConnection().commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void rollback(){
        try{
            connectionUtils.getThreadLocalConnection().rollback();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void release(){
        try{
            connectionUtils.getThreadLocalConnection().close(); // 换回连接池
            connectionUtils.removeConnection(); // 与当前线程解绑
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```

第三步、dao调用runner下面的操作方法；

```java
public int update(Connection conn, String sql, Object... params) throws SQLException {
    return this.update(conn, false, sql, params); // 这个false，就是不关闭connection，保持继续使用(即不放回连接池)
}

public <T> T query(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException {
    return this.query(conn, false, sql, rsh, params);
}

public Account findAccountByName(String name) {
    try{
        List<Account> accounts = runner.query(connectionUtils.getThreadLocalConnection(),"select * from account where name = ? ",new BeanListHandler<Account>(Account.class),name);
        if(accounts == null || accounts.size() == 0){
            return null;
        }
        if(accounts.size() > 1){
            throw new RuntimeException("结果集不唯一，数据有问题");
        }
        return accounts.get(0);
    }catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```

第四步、service实现类修正

```java
public class AccountServiceImpl implements IAccountService {
    private IAccountDao accountDao;
    private TransactionManager tManager;

    public void settManager(TransactionManager tManager) {
        this.tManager = tManager;
    }

    public void setAccountDao(IAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public void updateAccount(Account account) {
        try{
            tManager.beginTransaction();
            accountDao.updateAccount(account);
            tManager.commit();
        }catch (Exception e){
            e.printStackTrace();
            tManager.rollback();
        }finally {
            tManager.release();
        }
    }

    public void transfer(String sourceName, String targetName, Float money) {
        try{
            tManager.beginTransaction();
            //2.执行操作
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

//            int i = 1/0;

            accountDao.updateAccount(target);
            tManager.commit();
        }catch (Exception e){
            e.printStackTrace();
            tManager.rollback();
        }finally {
            tManager.release();
        }
    }
    
    @Override
    public void test() { //它只是连接点，但不是切入点，因为没有被增强

    }
    ...
}
```

> #### 上述修改后，完成了所有操作在同一connection的事务下进行，保证转账安全！
>
> ##### 但是，看看service实现类，出现太多重复代码，即事务操作的重复代码？该如何优化呢？ 
>
> ##### 解决方案：动态代理！进行相关方法增强，优化代码并进行解耦！



## 二、动态代理（2类）

   * #### 动态代理：

         *  特点：字节码随用随创建，随用随加载
         *  作用：不修改源码的基础上对方法增强

* #### 动态分类：

     - 基于接口的动态代理 -- 接口实现类代理
     - 基于子类的动态代理 -- 没有实现任何接口

### 2.1、基于接口的动态代理

```java
public class Client {

    public static void main(String[] args) {
        final Producer producer = new Producer();

        /**
         *  基于接口的动态代理：
         *      涉及的类：Proxy
         *      提供者：JDK官方
         *  如何创建代理对象：
         *      使用Proxy类中的newProxyInstance方法
         *  创建代理对象的要求：
         *      被代理类最少实现一个接口，如果没有则不能使用
         *  newProxyInstance方法的参数：
         *      ClassLoader：类加载器
         *          它是用于加载代理对象字节码的。和被代理对象使用相同的类加载器。固定写法。
         *      Class[]：字节码数组
         *          它是用于让代理对象和被代理对象有相同方法。固定写法。
         *      InvocationHandler：用于提供增强的代码
         *          它是让我们写如何代理。我们一般都是些一个该接口的实现类，通常情况下都是匿名内部类，但不是必须的。
         *          此接口的实现类都是谁用谁写。
         */
       IProducer proxyProducer = (IProducer) Proxy.newProxyInstance(producer.getClass().getClassLoader(),
                producer.getClass().getInterfaces(),
                new InvocationHandler() {
                    /**
                     * 作用：执行被代理对象的任何接口方法都会经过该方法
                     * 方法参数的含义
                     * @param proxy   代理对象的引用
                     * @param method  当前执行的方法
                     * @param args    当前执行方法所需的参数
                     * @return        和被代理对象方法有相同的返回值
                     * @throws Throwable
                     */
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //提供增强的代码
                        Object returnValue = null;

                        //1.获取方法执行的参数
                        Float money = (Float)args[0];
                        //2.判断当前方法是不是销售
                        if("saleProduct".equals(method.getName())) {
                            returnValue = method.invoke(producer, money*0.8f);
                        }
                        return returnValue;
                    }
                });
        proxyProducer.saleProduct(10000f);
    }
}
```



### 2.2、基于子类的动态代理

```java
public class Client {
    public static void main(String[] args) {
        final Producer producer = new Producer();

        /**
         *  基于子类的动态代理：
         *      涉及的类：Enhancer
         *      提供者：第三方cglib库
         *  如何创建代理对象：
         *      使用Enhancer类中的create方法
         *  创建代理对象的要求：
         *      被代理类不能是最终类
         *  create方法的参数：
         *      Class：字节码
         *          它是用于指定被代理对象的字节码。
         *
         *      Callback：用于提供增强的代码
         *          它是让我们写如何代理。我们一般都是些一个该接口的实现类，通常情况下都是匿名内部类，但不是必须的。
         *          此接口的实现类都是谁用谁写。
         *          我们一般写的都是该接口的子接口实现类：MethodInterceptor
         */
        Producer cglibProducer = (Producer)Enhancer.create(producer.getClass(), new MethodInterceptor() {
            /**
             * 执行北地阿里对象的任何方法都会经过该方法
             * @param proxy
             * @param method
             * @param args
             *    以上三个参数和基于接口的动态代理中invoke方法的参数是一样的
             * @param methodProxy ：当前执行方法的代理对象
             * @return
             * @throws Throwable
             */
            @Override
            public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
               
                //提供增强的代码
                Object returnValue = null;

                //1.获取方法执行的参数
                Float money = (Float)args[0];
                //2.判断当前方法是不是销售
                if("saleProduct".equals(method.getName())) {
                    returnValue = method.invoke(producer, money*0.8f);
                }
                return returnValue;
            }
        });
        cglibProducer.saleProduct(12000f);
    }
}
```



### 2.3、银行转账service改造

```java
/**
* 使用代理类增强，将事务操作封装带代理类中，同时又将TransactionManager与service解耦
*/
public class BeanFactory {
    private IAccountService accountService;
    private TransactionManager txManager;

    public final void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    public void setTxManager(TransactionManager txManager) {
        this.txManager = txManager;
    }

    public IAccountService getProxyAccountService(){
        return (IAccountService) Proxy.newProxyInstance(accountService.getClass().getClassLoader(),
                accountService.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if("test".equals(method.getName())){ 
                            return method.invoke(accountService,args);
                        }
                        // 进行方法的增强
                        Object obj = null;
                        try{
                            txManager.beginTransaction();
                            obj = method.invoke(accountService, args);
                            txManager.commit();
                        }catch (Exception e){
                            txManager.rollback();
                            e.printStackTrace();
                        }finally {
                            txManager.release();
                        }
                        return obj;
                    }
                });
    }
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="instanceFactory" class="com.eoony.factory.BeanFactory">
        <property name="accountService" ref="userService"/>
        <property name="txManager" ref="txManager"/>
    </bean>

    <bean id="proxyUserService" factory-bean="instanceFactory" factory-method="getProxyAccountService"/>

    <bean id="connectionUtils" class="com.eoony.utils.ConnectionUtils">
        <property name="ds" ref="dataSource"/>
    </bean>

    <bean id="txManager" class="com.eoony.utils.TransactionManager">
        <property name="connectionUtils" ref="connectionUtils"/>
    </bean>

    <bean id="userService" class="com.eoony.service.impl.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"/>
    </bean>

    <bean id="accountDao" class="com.eoony.dao.impl.AccountDaoImpl">
        <property name="runner" ref="runner"/>
        <property name="connectionUtils" ref="connectionUtils"/>
    </bean>

    <bean id="runner" class="org.apache.commons.dbutils.QueryRunner" scope="prototype">
    </bean>

    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/spring?useUnicode=true&amp;characterEncoding=UTF-8"/>
        <property name="username" value="root"/>
        <property name="password" value="root"/>
    </bean>

</beans>
```





## 三、AOP 的相关概念

### 3.1、什么是 AOP？

AOP:全称是 Aspect Oriented Programming 即:面向切面编程。

```properties
在软件业，AOP为Aspect Oriented Programming的缩写，意为：面向切面编程，通过预编译方式和运行期动态代理实现程序功能的统一维护的一种技术。AOP是OOP的延续，是软件开发中的一个热点，也是Spring框架中的一个重要内容，是函数式编程的一种衍生范型。利用AOP可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率。
```

> ##### 简单的说它就是把我们程序重复的代码抽取出来,在需要执行的时候,使用动态代理的技术,在不修改源码的基础上,对我们的已有方法进行增强。

- #### AOP 的作用及优势

  ```
  作用:
    在程序运行期间,不修改源码对已有方法进行增强。
  优势:
    减少重复代码
    提高开发效率
    维护方便
  ```

- #### AOP 的实现方式－－动态代理技术



## 四、Spring 中的 AOP[掌握]

##### 学习 spring 的 aop，就是通过配置的方式，实现前面银行转账的动态代理功能。

### 4.1、AOP 相关术语

```properties
Joinpoint(连接点 ): 所谓连接点是指那些被拦截到的点。在 spring 中,这些点指的是方法,因为 spring 只支持方法类型的连接点。
Pointcut(切入点 ): 所谓切入点是指我们要对哪些 Joinpoint 进行拦截的定义。
区分: 所有的连接点是切入点，但切入点并不一定是连接点；
	
Advice(通知/增强): 所谓通知是指拦截到 Joinpoint 之后所要做的事情就是通知。 通知的类型:前置通知,后置通知,异常通知,最终通知,环绕通知。
	
Introduction(引介 ): 引介是一种特殊的通知在不修改类代码的前提下, Introduction 可以在运行期为类动态地添加一些方法或 Field。 

Target(目标对象 ): 代理的目标对象。

Weaving(织入 ): 是指把增强应用到目标对象来创建新的代理对象的过程。
	spring 采用动态代理织入,而 AspectJ 采用编译期织入和类装载期织入。 
	
Proxy(代理) : 一个类被 AOP 织入增强后,就产生一个结果代理类。 
	
Aspect(切面 ): 是切入点和通知(引介)的结合。
```

```java
public IAccountService getProxyAccountService(){
      return (IAccountService) Proxy.newProxyInstance(accountService.getClass().getClassLoader(),
                accountService.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override     // 整个的invoke方法在执行就是环绕通知
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if("test".equals(method.getName())){ // [连接点]
                            return method.invoke(accountService,args);
                        }

                        // 进行方法的增强， [切入点]
                        Object obj = null;
                        try{
                            txManager.beginTransaction(); // 前置通知
                            obj = method.invoke(accountService, args); // 在环绕通知中明确切入点的方法调用！！！
                            txManager.commit();  // 后置通知 
                        }catch (Exception e){
                            e.printStackTrace();
                            txManager.rollback(); // 异常通知
                        }finally {
                            txManager.release(); // 最终通知
                        }
                        return obj;
                    }
                });
}
```



### 4.2、学习 spring 中的 AOP 要明确的事

#### a、开发阶段(我们做 的)

​	编写核心业务代码(开发主线) : 大部分程序员来做,要求熟悉业务需求。

​	把公用代码抽取出来,制作成通知。(开发阶段最后再做) : AOP 编程人员来做。

​	在配置文件中，声明切入点与通知间的关系，即切面 : AOP 编程人员来做。

#### b、运行阶段(Spring 框架完成的)

​	Spring 框架监控切入点方法的执行。一旦监控到切入点方法被运行,使用代理机制,动态创建目标对 象的代理对象,根据通知类别,在代理对象的对应位置,将通知对应的功能织入,完成完整的代码逻辑运行。	

> ####  关于代理的选择: 在 spring 中,框架会根据目标类是否实现了接口来决定采用哪种动态代理的方式。	(前面讲的基于接口，基于子类两种方式！)



### 4.3、基于 XML 的 AOP 配置

- #### Logger: 用于记录日志的工具类，它里面提供了公共的代码

  ```java
  public class Logger {
      /**前置通知*/
      public  void beforePrintLog(){
          System.out.println("前置通知Logger类中的beforePrintLog方法开始记录日志了。。。");
      }
      /**后置通知*/
      public  void afterReturningPrintLog(){
          System.out.println("后置通知Logger类中的afterReturningPrintLog方法开始记录日志了。。。");
      }
      /**异常通知*/
      public  void afterThrowingPrintLog(){
          System.out.println("异常通知Logger类中的afterThrowingPrintLog方法开始记录日志了。。。");
      }
      /**最终通知*/
      public  void afterPrintLog(){
          System.out.println("最终通知Logger类中的afterPrintLog方法开始记录日志了。。。");
      }
  }
  ```

- #### bean.xml 需要引入xmlns:aop

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns:aop="http://www.springframework.org/schema/aop"
         xsi:schemaLocation="http://www.springframework.org/schema/beans
          http://www.springframework.org/schema/beans/spring-beans.xsd
          http://www.springframework.org/schema/aop
          http://www.springframework.org/schema/aop/spring-aop.xsd">
  
      <!-- 配置srping的Ioc,把service对象配置进来-->
      <bean id="accountService" class="com.itheima.service.impl.AccountServiceImpl"/>
  
      <!--spring中基于XML的AOP配置步骤
          1、把通知Bean也交给spring来管理
          2、使用aop:config标签表明开始AOP的配置
          3、使用aop:aspect标签表明配置切面
                  id属性：是给切面提供一个唯一标识
                  ref属性：是指定通知类bean的Id。
          4、在aop:aspect标签的内部使用对应标签来配置通知的类型
                 我们现在示例是让printLog方法在切入点方法执行之前之前：所以是前置通知
                 aop:before：表示配置前置通知
                      method属性：用于指定Logger类中哪个方法是前置通知
                      pointcut属性：用于指定切入点表达式，该表达式的含义指的是对业务层中哪些方法增强
  
              切入点表达式的写法：
                  关键字：execution(表达式)
                  表达式：
                      访问修饰符  返回值  包名.包名.包名...类名.方法名(参数列表)
                  标准的表达式写法：
                      public void com.itheima.service.impl.AccountServiceImpl.saveAccount()
                  访问修饰符可以省略
                      void com.itheima.service.impl.AccountServiceImpl.saveAccount()
                  返回值可以使用通配符，表示任意返回值
                      * com.itheima.service.impl.AccountServiceImpl.saveAccount()
                  包名可以使用通配符，表示任意包。但是有几级包，就需要写几个*.
                      * *.*.*.*.AccountServiceImpl.saveAccount())
                  包名可以使用..表示当前包及其子包
                      * *..AccountServiceImpl.saveAccount()
                  类名和方法名都可以使用*来实现通配
                      * *..*.*()
                  参数列表：
                      可以直接写数据类型：
                          基本类型直接写名称           int
                          引用类型写包名.类名的方式   java.lang.String
                      可以使用通配符表示任意类型，但是必须有参数
                      可以使用..表示有无参数均可，有参数可以是任意类型
                  全通配写法：
                      * *..*.*(..)
  
                  实际开发中切入点表达式的通常写法：
                      切到业务层实现类下的所有方法
                          * com.itheima.service.impl.*.*(..)
      -->
  
      <!-- 配置Logger类 -->
      <bean id="logger" class="com.itheima.utils.Logger"/>
  
      <!--配置AOP-->
      <aop:config>
          <!--配置切面 -->
          <aop:aspect id="logAdvice" ref="logger">
              <!-- 配置通知的类型，并且建立通知方法和切入点方法的关联-->
              <aop:before method="beforePrintLog" pointcut="execution(* com.itheima.service.impl.*.*(..))"/>
          </aop:aspect>
      </aop:config>
  </beans>
  ```

- #### AOP通知类型配置细说：

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns:aop="http://www.springframework.org/schema/aop"
         xsi:schemaLocation="http://www.springframework.org/schema/beans
          http://www.springframework.org/schema/beans/spring-beans.xsd
          http://www.springframework.org/schema/aop
          http://www.springframework.org/schema/aop/spring-aop.xsd">
  		...
  
      <!--配置AOP-->
      <aop:config>
          <!-- 配置切入点表达式 id属性用于指定表达式的唯一标识。expression属性用于指定表达式内容
                此标签写在aop:aspect标签内部只能当前切面使用。
                它还可以写在aop:aspect外面，此时就变成了所有切面可用(一定要按xsd约束来，必须在<aop:aspect../>标签的前面)
            -->
          <aop:pointcut id="pt1" expression="execution(* com.itheima.service.impl.*.*(..))"></aop:pointcut>
          <!--配置切面 -->
          <aop:aspect id="logAdvice" ref="logger">
              <!-- 配置前置通知：在切入点方法执行之前执行-->
              <aop:before method="beforePrintLog" pointcut-ref="pt1" />
  
              <!-- 配置后置通知：在切入点方法正常执行之后值。它和异常通知永远只能执行一个-->
              <aop:after-returning method="afterReturningPrintLog" pointcut-ref="pt1"/>
  
              <!-- 配置异常通知：在切入点方法执行产生异常之后执行。它和后置通知永远只能执行一个-->
              <aop:after-throwing method="afterThrowingPrintLog" pointcut-ref="pt1"/>
  
              <!-- 配置最终通知：无论切入点方法是否正常执行它都会在其后面执行-->
              <aop:after method="afterPrintLog" pointcut-ref="pt1"></aop:after>
  
              <!-- 配置环绕通知 详细的注释请看Logger类中-->
              <aop:around method="aroundPringLog" pointcut-ref="pt1"></aop:around>
          </aop:aspect>
      </aop:config>
  </beans>
  ```

- #### AOP的环绕标签：

  ```java
  public class Logger {
   ....
      /**
       * 环绕通知
       * 问题：
       *      当我们配置了环绕通知之后，切入点方法没有执行，而通知方法执行了。
       * 分析：
       *      通过对比动态代理中的环绕通知代码，发现动态代理的环绕通知有明确的切入点方法调用，而我们的代码中没有。
       * 解决：
       *      Spring框架为我们提供了一个接口：ProceedingJoinPoint。该接口有一个方法proceed()，此方法就相当于明确调用切入点方法。
       *      该接口可以作为环绕通知的方法参数，在程序执行时，spring框架会为我们提供该接口的实现类供我们使用。
       *
       * spring中的环绕通知：
       *      它是spring框架为我们提供的一种可以在代码中手动控制增强方法何时执行的方式。
       */
      public Object aroundPringLog(ProceedingJoinPoint pjp){
          Object rtValue = null;
          try{
              Object[] args = pjp.getArgs();//得到方法执行所需的参数
  
              System.out.println("Logger类中的aroundPringLog方法开始记录日志了。。。前置");
  
              rtValue = pjp.proceed(args);//明确调用业务层方法（切入点方法）
  
              System.out.println("Logger类中的aroundPringLog方法开始记录日志了。。。后置");
  
              return rtValue;
          }catch (Throwable t){
              System.out.println("Logger类中的aroundPringLog方法开始记录日志了。。。异常");
              throw new RuntimeException(t);
          }finally {
              System.out.println("Logger类中的aroundPringLog方法开始记录日志了。。。最终");
          }
      }
  }
  ```



### 4.4、AOP注解配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop.xsd 
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">
    
    <context:component-scan base-package="com.eoony"/>
    <!--配置Spring开启AOP注解的支持-->
    <aop:aspectj-autoproxy/>
</beans>
```

```java
@Component("logger")
@Aspect
public class Logger {
    @Pointcut("execution(* com.eoony.service.impl.*.*(..))")
    public void pt1(){}

    /**
     * 前置通知
     */
    @Before("pt1()")
    public  void beforePrintLog(){
        System.out.println("前置通知Logger类中的beforePrintLog方法开始记录日志了。。。");
    }

    /**
     * 后置通知
     */
    @AfterReturning("pt1()")
    public  void afterReturningPrintLog(){
        System.out.println("后置通知Logger类中的afterReturningPrintLog方法开始记录日志了。。。");
    }
    /**
     * 异常通知
     */
    @AfterThrowing("pt1()")
    public  void afterThrowingPrintLog(){
        System.out.println("异常通知Logger类中的afterThrowingPrintLog方法开始记录日志了。。。");
    }

    /**
     * 最终通知
     */
    @After("pt1()")
    public  void afterPrintLog(){
        System.out.println("最终通知Logger类中的afterPrintLog方法开始记录日志了。。。");
    }

//    @Around("pt1()")
    public Object aroundPrintLog(ProceedingJoinPoint pjp){
        Object obj;
        try{
            beforePrintLog();

            Object[] args = pjp.getArgs();
            obj = pjp.proceed(args);

            afterReturningPrintLog();
        }catch (Throwable t){
            afterThrowingPrintLog();
            throw new RuntimeException(t);
        }finally {
            afterPrintLog();
        }
        return obj;
    }
}
```

> #### 注意：Spring注解的前面4个通知，有执行顺序问题。
>
> #### 所以，AOP使用注解配置，一般采用环绕通知形式！

- #### 纯注解方式：

```java
@Configuration @ComponentScan(basePackages="com.itheima") 
@EnableAspectJAutoProxy // 添加此条注解即可！
public class SpringConfiguration {
    
}
```









