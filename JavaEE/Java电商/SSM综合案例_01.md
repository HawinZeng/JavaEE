#SSM综合案例_01 环境搭建／产品信息

SVN了解（略）

## 一、AdminLTE 

```properties
AdminLTE 是一款建立在bootstap和jquery之上的开源模版主题公斤，它提供了一系列响应的、可重复使用的组件，并内置了多个模版页面；同时自适应多种屏幕分辨率，兼容PC和移动端。通过AdminLTE ，我们可以快速的创建一个响应式的html5网站。AdminLTE 框架在网页架构与设计上，有很大的辅助作用，尤其是前端架构设计师，用好AdminLTE 不但美观，而且可以免去写很大CSS和JS的工作量；
```

### 1.1、GitHub获取AdminLTE 

```
https://github.com/almasaeed2010/AdminLTE
```

### 1.2、AdminLTE2-IT黑马－－定制版 （中文）

```sql
https://github.com/itheima2017/adminlte2-itheima
-- 在线浏览：
http://research.itcast.cn/adminlte2-itcast/release/dist/pages/all-admin-index.html
```

下载后，工程目录结构：assets、modules、pages、plugins都是前端开发时所使用的，最终发布的就是release。所以对于我们使用adminlte2来说，只需要关注release目录下的结构即可。

> 详情参考：[AdminLTE详解](attach/F0_AdminLTE介绍.pdf)



### 二、注意事项（缺！）

#### 2.1、Oracle连接问题

#### 2.2、类型转换方式

- 如页面传来了一个日期字符串（2019-02-22 15:34:22）为String字段，而实体类的日期为java.util.Date类型。此时，spring封装数据时就会报错。怎么解决呢？那就必须在实体类进行类型转换了。
- 类型转换的方式有几种：



## 三、SSM 环境搭建与产品操作

### 3.1、环境准备--数据库与表结构

- #### 创建用户与授权：

```properties
1. 创建用户: SQL语句创建、PL/SQL GUI创建；
 	1.1 SQL语句创建: 参考前面的内容Oracle。
 	1.2 PL/SQL GUI创建: 
 		1） 在dba／system用户下，找到Users表；
 		2） 右击new， 即可直接输入name、password；
        3） 赋权限: 
        	Object Privileges(对象权限): 指针对于某一张表的操作权限;
        	System Privileges(系统权限): 指对表的CRUD操作权限;
        	Role Privileges: 系统权限的集合;
        一般是设置角色权限,设置具体的resource与connect	！！
```

> 问题1：连接oracle是连接表空间，还是用户呢？答： 连接的时实例／表空间下的用户。
>
> 问题2：通过GUI创建用户，没有设置对应的表空间关联的物理文件（物理数据库），会造成程序调试，无法找到用户的表数据；所以一定要关联物理数据库。才能在java代码中通过oracle驱动连接找到表中的数据；
>
> 问题3：用户下的表用自己创建，若通过复制其他用户下的表，会造成部分oracle函数失效；

- #### 创建表 -- product

```sql
CREATE TABLE product(
  id varchar2(32) default SYS_GUID() PRIMARY KEY,
  productNum VARCHAR2(50) NOT NULL,
  productName VARCHAR2(50),
  cityName VARCHAR2(50),
  DepartureTime timestamp,
  productPrice Number,
  productDesc VARCHAR2(500),
  productStatus INT,
  CONSTRAINT product UNIQUE (id, productNum)
)
insert into PRODUCT (id, productnum, productname, cityname, departuretime, productprice, productdesc, productstatus)
values ('676C5BD1D35E429A8C2E114939C5685A', 'itcast-002', '北京三日游', '北京', to_timestamp('10- 10-2018 10:10:00.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 1200, '不错的旅行', 1);
insert into PRODUCT (id, productnum, productname, cityname, departuretime, productprice, productdesc, productstatus)
values ('12B7ABF2A4C544568B0A7C69F36BF8B7', 'itcast-003', '上海五日游', '上海', to_timestamp('25- 04-2018 14:30:00.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 1800, '魔都我来了', 0);
insert into PRODUCT (id, productnum, productname, cityname, departuretime, productprice, productdesc, productstatus)
values ('9F71F01CB448476DAFB309AA6DF9497F', 'itcast-001', '北京三日游', '北京', to_timestamp('10- 10-2018 10:10:00.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 1200, '不错的旅行', 1);
```

### 3.2、 Maven工程搭建

- #### 使用maven的拆分聚合搭建框架

  ```
  - ssm 父亲工程
  	- ssm-dao 持久层子工程  MyBatis
  	- ssm-domain 数据实体子工程 POJO对象
  	- ssm-service 业务层子工程 Spring
  	- ssm-utils 工具类子工程
  	- ssm-web 核心web层子工程 SpringMVC
  ```

  > ##### 注意点：父工程：依赖jar冲突问题解决！
  >
  > 1. 其他Spring，MyBatis，SpringMVC整合按照通常的要求将环境搭建OK。参考讲义！

- #### Spring，MyBatis，SpringMVC整合

  ##### 1) web子工程：applicationContext.xml

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns:context="http://www.springframework.org/schema/context"
         xmlns:aop="http://www.springframework.org/schema/aop"
         xmlns:tx="http://www.springframework.org/schema/tx"
         xsi:schemaLocation="http://www.springframework.org/schema/beans
  	http://www.springframework.org/schema/beans/spring-beans.xsd
  	http://www.springframework.org/schema/context
  	http://www.springframework.org/schema/context/spring-context.xsd
  	http://www.springframework.org/schema/aop
  	http://www.springframework.org/schema/aop/spring-aop.xsd
  	http://www.springframework.org/schema/tx
  	http://www.springframework.org/schema/tx/spring-tx.xsd">
  
      <!-- 开启注解扫描，管理service和dao -->
      <context:component-scan base-package="com.eoony.service"/>
      <context:component-scan base-package="com.eoony.dao"/>
  
      <context:property-placeholder location="classpath:db.properties"/>
      <!-- 配置连接池 -->
      <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
          <property name="driverClass" value="${jdbc.driver}"/>
          <property name="jdbcUrl" value="${jdbc.url}"/>
          <property name="user" value="${jdbc.username}"/>
          <property name="password" value="${jdbc.password}"/>
      </bean>
  
      <!--mybatis集成-->
      <!-- 把SqlSessionFactory交给IOC管理  -->
      <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
          <property name="dataSource" ref="dataSource"/>
          <!-- 注意其他配置:分页插件 -->
          <property name="plugins">
              <array>
                  <bean class="com.github.pagehelper.PageInterceptor">
                      <property name="properties">
                          <props>
                              <prop key="helperDialect">oracle</prop>
                              <prop key="reasonable">true</prop>
                          </props>
                      </property>
                  </bean>
              </array>
          </property>
      </bean>
      <!-- 扫描dao接口 -->
      <bean id="mapperScanner" class="org.mybatis.spring.mapper.MapperScannerConfigurer">
          <property name="basePackage" value="com.eoony.dao"/>
      </bean>
  
      <!-- 配置Spring的声明式事务管理 -->
      <!-- 配置事务管理器 -->
      <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
          <property name="dataSource" ref="dataSource"/>
      </bean>
  
      <!--开启事务注解支持-->
      <tx:annotation-driven transaction-manager="transactionManager"/>
  </beans>
  ```

  ##### 2) web子工程：springmvc.xml

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
         xmlns:mvc="http://www.springframework.org/schema/mvc"
         xmlns:context="http://www.springframework.org/schema/context"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns:aop="http://www.springframework.org/schema/aop"
         xsi:schemaLocation="
             http://www.springframework.org/schema/beans
             http://www.springframework.org/schema/beans/spring-beans.xsd
             http://www.springframework.org/schema/mvc
             http://www.springframework.org/schema/mvc/spring-mvc.xsd
             http://www.springframework.org/schema/context
             http://www.springframework.org/schema/context/spring-context.xsd
             http://www.springframework.org/schema/aop
  		http://www.springframework.org/schema/aop/spring-aop.xsd">
  
      <!-- 扫描controller的注解，别的不扫描 -->
      <context:component-scan base-package="com.eoony.controller"/>
  
      <!-- 配置视图解析器 -->
      <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
          <!-- JSP文件所在的目录 -->
          <property name="prefix" value="/pages/" />
          <!-- 文件的后缀名 -->
          <property name="suffix" value=".jsp" />
      </bean>
  
      <!-- 设置静态资源不过滤 -->
      <mvc:resources location="/css/" mapping="/css/**" />
      <mvc:resources location="/img/" mapping="/img/**" />
      <mvc:resources location="/js/" mapping="/js/**" />
      <mvc:resources location="/plugins/" mapping="/plugins/**" />
  
      <!-- 开启对SpringMVC注解的支持 -->
      <mvc:annotation-driven />
  
      <!--
          支持AOP的注解支持，AOP底层使用代理技术
          JDK动态代理，要求必须有接口
          cglib代理，生成子类对象，proxy-target-class="true" 默认使用cglib的方式
      -->
      <aop:aspectj-autoproxy proxy-target-class="true"/>
  </beans>
  ```

  ##### 3) web子工程：web.xml

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns="http://xmlns.jcp.org/xml/ns/javaee"
           xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
           version="3.1">
  
    <!-- 配置加载类路径的配置文件 -->
    <context-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>classpath*:applicationContext.xml;</param-value>
    </context-param>
  
    <!-- 配置监听器 -->
    <listener>
      <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
  
    <servlet>
      <servlet-name>dispatcherServlet</servlet-name>
      <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
      <!-- 配置初始化参数，创建完DispatcherServlet对象，加载springmvc.xml配置文件 -->
      <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:springmvc.xml</param-value>
      </init-param>
      <!-- 服务器启动的时候，让DispatcherServlet对象创建 -->
      <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
      <servlet-name>dispatcherServlet</servlet-name>
      <url-pattern>*.do</url-pattern>
    </servlet-mapping>
  
    <!-- 解决中文乱码过滤器 -->
    <filter>
      <filter-name>characterEncodingFilter</filter-name>
      <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
      <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
      </init-param>
    </filter>
    <filter-mapping>
      <filter-name>characterEncodingFilter</filter-name>
      <url-pattern>/*</url-pattern>
    </filter-mapping>
  
    <welcome-file-list>
      <welcome-file>index.html</welcome-file>
      <welcome-file>index.htm</welcome-file>
      <welcome-file>index.jsp</welcome-file>
      <welcome-file>default.html</welcome-file>
      <welcome-file>default.htm</welcome-file>
      <welcome-file>default.jsp</welcome-file>
    </welcome-file-list>
      
  </web-app>
  ```

  ##### 4) web子工程：db.properties

  ```properties
  jdbc.driver=oracle.jdbc.driver.OracleDriver
  jdbc.url=jdbc:oracle:thin:@192.168.186.141:1521:orcl
  jdbc.username=itheima
  jdbc.password=itheima
  ```


### 3.2 产品查询 / 商品添加

- ##### dao: IProductDao

```java
public interface IProductDao {

    @Select(" select * from product where id = #{id} ")
    Product findById(String id);

    @Select(" select * from product ")
    List<Product> findAll() throws Exception;

    @Insert(" insert into product(productNum,productName,cityName,departureTime,productPrice,productDesc,productStatus) values(#{productNum},#{productName},#{cityName},#{departureTime},#{productPrice},#{productDesc},#{productStatus}) ")
    void save(Product product) throws Exception;
}
```

- ##### service: IProductServiceImpl

```java
@Service
@Transactional // 默认 propagation.REQUIRED 一定要有事务，readyOnly false。 （spring的事务注解方式，没有什么执行顺序问题，因为其内部已经是环绕通知了）
public class ProductServiceImpl implements IProductService {

    @Autowired
    private IProductDao productDao;

    @Transactional(propagation = Propagation.SUPPORTS,readOnly = true) // 查询操作 SUPPORTS表示支持事务，也可以不需要，查询只能读取true
    @Override
    public List<Product> findAll() throws Exception {
        return productDao.findAll();
    }

    @Override
    public void save(Product product) throws Exception { // 没有设置事务，为啥能成功，是由于只有一条持久层的操作，本身自带提交功能。
        productDao.save(product);
    }
}
```























