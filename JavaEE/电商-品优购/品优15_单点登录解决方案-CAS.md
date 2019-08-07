# 品优15_单点登录解决方案-CAS

## 一、开源单点登录系统CAS入门

## 1.1 什么是单点登录

​	单点登录（Single Sign On），简称为 SSO，是目前比较流行的企业业务整合的解决方案之一。SSO的定义是在多个应用系统中，用户只需要登录一次就可以访问所有相互信任的应用系统。

​	我们目前的系统存在诸多子系统，而这些子系统是分别部署在不同的服务器中，那么使用传统方式的session是无法解决的，我们需要使用相关的单点登录技术来解决。



### 1.2  什么是CAS

CAS 是 Yale 大学发起的一个开源项目，旨在为 Web 应用系统提供一种可靠的单点登录方法，CAS 在 2004 年 12 月正式成为 JA-SIG 的一个项目。CAS 具有以下特点：

【1】开源的企业级单点登录解决方案。

【2】CAS Server 为需要独立部署的 Web 应用。

【3】CAS Client 支持非常多的客户端(这里指单点登录系统中的各个 Web 应用)，包括 Java, .Net, PHP, Perl, Apache, uPortal, Ruby 等。

从结构上看，CAS 包含两个部分： CAS Server 和 CAS Client。CAS Server 需要独立部署，主要负责对用户的认证工作；CAS Client 负责处理对客户端受保护资源的访问请求，需要登录时，重定向到 CAS Server。下图是 CAS 最基本的协议过程：



### 1.3  CAS服务端部署

Cas服务端其实就是一个war包。

在资源\cas\source\cas-server-4.0.0-release\cas-server-4.0.0\modules目录下 cas-server-webapp-4.0.0.war 。直接放入Tomcat webapps下即可。

##### 1） 默认账户/ 密码：  casuser /Mellon

##### 2）修改/新增用户： 可以修改配置文件，也可以从数据库导入.

##### < 修改 WEB-INF/deployerConfigContext.xml. 即可修改、新增用户。（数据库导入参考后续）>

```xml
<bean id="primaryAuthenticationHandler"
      class="org.jasig.cas.authentication.AcceptUsersAuthenticationHandler">
    <property name="users">
        <map>
            <entry key="casuser" value="Mellon"/>
            <entry key="admin" value="admin"/>
        </map>
    </property>
</bean>
```

##### 3）修改端口：两步走

- 先修改Tomcat端口： tomcat/conf/server.xml

  ```xml
  <Connector port="9100" protocol="HTTP/1.1"
                 connectionTimeout="20000"
                 redirectPort="8443" />
  ```

- 然后修改CAS配置文件 ：WEB-INF/cas.properties

  ```properties
  server.name=http://localhost:9100
  server.prefix=${server.name}/cas
  ```

> 上述问题弄好，我们发现，当



##### 4）去除https认证 ：三步走

​	CAS默认使用的是HTTPS协议，如果使用HTTPS协议需要SSL安全证书（需向特定的机构申请和购买） 。如果对安全要求不高或是在开发测试阶段，可使用HTTP协议。我们这里讲解通过修改配置，让CAS使用HTTP协议。 

##### 		- 默认使用Https，若不修正，在测试开发阶段，会无法实现单点认证功能。

- 修改cas的WEB-INF/deployerConfigContext.xml : 

  ```xml
  <!--增加参数p:requireSecure="false"，requireSecure属性意思为是否需要安全验证，即HTTPS，false为不采用-->
  <bean id="proxyAuthenticationHandler"
    class="org.jasig.cas.authentication.handler.support.HttpBasedServiceCredentialsAuthenticationHandler" p:httpClient-ref="httpClient" p:requireSecure="false" />
  ```

- 修改cas的/WEB-INF/spring configuration/ticketGrantingTicketCookieGenerator.xml

  找到下面配置: 

  ##### 	- cookieMaxAge 代表cookie时效性，避免每次都来登录。

  ```xml
  <!-- p:cookieSecure="true"，同理为HTTPS验证相关，TRUE为采用HTTPS验证，FALSE为不采用-->
  <bean id="ticketGrantingTicketCookieGenerator" class="org.jasig.cas.web.support.CookieRetrievingCookieGenerator"
        p:cookieSecure="false" 
        p:cookieMaxAge="3600" 
        p:cookieName="CASTGC"
        p:cookiePath="/cas" />
  ```

- 修改cas的WEB-INF/spring-configuration/warnCookieGenerator.xml

  ```xml
  <beans xmlns="http://www.springframework.org/schema/beans"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns:p="http://www.springframework.org/schema/p"
         xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
  	<description>
  		Defines the cookie that stores the TicketGrantingTicket.  You most likely should never modify these (especially the "secure" property).
  		You can change the name if you want to make it harder for people to guess.
  	</description>
  	<bean id="ticketGrantingTicketCookieGenerator" class="org.jasig.cas.web.support.CookieRetrievingCookieGenerator"
  		p:cookieSecure="false"
  		p:cookieMaxAge="3600"
  		p:cookieName="CASTGC"
  		p:cookiePath="/cas" />
  </beans>
  ```



### 1.4、CAS客户端入门小Demo

- #### web.xml配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns="http://java.sun.com/xml/ns/javaee"
	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
	version="2.5">	
	
	<!-- ======================== 单点登录开始 ======================== -->  
    <!-- 用于单点退出，该过滤器用于实现单点登出功能，可选配置 -->  
    <listener>  
        <listener-class>org.jasig.cas.client.session.SingleSignOutHttpSessionListener</listener-class>  
    </listener>  
  
    <!-- 该过滤器用于实现单点登出功能，可选配置。 -->  
    <filter>  
        <filter-name>CAS Single Sign Out Filter</filter-name>  
        <filter-class>org.jasig.cas.client.session.SingleSignOutFilter</filter-class>  
    </filter>  
    <filter-mapping>  
        <filter-name>CAS Single Sign Out Filter</filter-name>  
        <url-pattern>/*</url-pattern>  
    </filter-mapping>  
  
    <!-- 该过滤器负责用户的认证工作，必须启用它 -->  
    <filter>  
        <filter-name>CASFilter</filter-name>  
        <filter-class>org.jasig.cas.client.authentication.AuthenticationFilter</filter-class>  
        <init-param>  
            <param-name>casServerLoginUrl</param-name>  
            <param-value>http://localhost:9100/cas/login</param-value>  
            <!--这里是CAS server服务端的IP -->  
        </init-param>  
        <init-param>  
            <param-name>serverName</param-name>  
            <param-value>http://localhost:9001</param-value>
             <!--这里是当前web应用的IP -->  
        </init-param>  
    </filter>  
    <filter-mapping>  
        <filter-name>CASFilter</filter-name>  
        <url-pattern>/*</url-pattern>  
    </filter-mapping>  
  
    <!-- 该过滤器负责对Ticket的校验工作，必须启用它 -->  
    <filter>  
        <filter-name>CAS Validation Filter</filter-name>  
        <filter-class>  
            org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter</filter-class>  
        <init-param>  
            <param-name>casServerUrlPrefix</param-name>  
            <param-value>http://localhost:9100/cas</param-value>  
        </init-param>  
        <init-param>  
            <param-name>serverName</param-name>  
            <param-value>http://localhost:9001</param-value>
        </init-param>  
    </filter>  
    <filter-mapping>  
        <filter-name>CAS Validation Filter</filter-name>  
        <url-pattern>/*</url-pattern>  
    </filter-mapping>  
  
    <!-- 该过滤器负责实现HttpServletRequest请求的包裹， 比如允许开发者通过HttpServletRequest的getRemoteUser()方法获得SSO登录用户的登录名，可选配置。 -->  
    <filter>  
        <filter-name>CAS HttpServletRequest Wrapper Filter</filter-name>  
        <filter-class>  
            org.jasig.cas.client.util.HttpServletRequestWrapperFilter</filter-class>  
    </filter>  
    <filter-mapping>  
        <filter-name>CAS HttpServletRequest Wrapper Filter</filter-name>  
        <url-pattern>/*</url-pattern>  
    </filter-mapping>  
  
    <!-- 该过滤器使得开发者可以通过org.jasig.cas.client.util.AssertionHolder来获取用户的登录名。 比如AssertionHolder.getAssertion().getPrincipal().getName()。 -->  
    <filter>  
        <filter-name>CAS Assertion Thread Local Filter</filter-name>  
        <filter-class>org.jasig.cas.client.util.AssertionThreadLocalFilter</filter-class>  
    </filter>  
    <filter-mapping>  
        <filter-name>CAS Assertion Thread Local Filter</filter-name>  
        <url-pattern>/*</url-pattern>  
    </filter-mapping>  
    <!-- ======================== 单点登录结束 ======================== -->  
</web-app>
```

- #### 依赖 pom.xml

```XML
<dependencies>
    <!-- cas -->  
    <dependency>  
        <groupId>org.jasig.cas.client</groupId>  
        <artifactId>cas-client-core</artifactId>  
        <version>3.3.3</version>  
    </dependency>  		
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>servlet-api</artifactId>
        <version>2.5</version>  
        <scope>provided</scope>
    </dependency>
</dependencies>  
<build>  
    <plugins>
        <plugin>  
            <groupId>org.apache.maven.plugins</groupId>  
            <artifactId>maven-compiler-plugin</artifactId>  
            <version>2.3.2</version>  
            <configuration>  
                <source>1.7</source>  
                <target>1.7</target>  
            </configuration>  
        </plugin>  
        <plugin>
            <groupId>org.apache.tomcat.maven</groupId>
            <artifactId>tomcat7-maven-plugin</artifactId>
            <configuration>
                <!-- 指定端口 -->
                <port>9001</port>
                <!-- 请求路径 -->
                <path>/</path>
            </configuration>
        </plugin>
    </plugins>  
</build>
```

> 可以开启casclient_demo1，casclient_demo2两个工程一同测试！ 
>
> ##### 1）当访问casclient_demo1的index.jsp时，没有登录，那么就会先跳转到cas server 9100端口的登录页面先登录。
>
> ##### 2）登陆后，casclient_demo2访问自身的index.jsp就无需登录，也能抓取用户信息。



### 1.5、单点登出

##### 直接访问： cas/logout.  --->`http://localhost:9000/cas/logout`

##### 1） 为啥能直接访问logout即能退出？ 在前面配置了登出过滤器，登出的监听器！

```xml
<!-- 用于单点退出，该过滤器用于实现单点登出功能，可选配置 -->  
<listener>  
    <listener-class>org.jasig.cas.client.session.SingleSignOutHttpSessionListener</listener-class>  
</listener>  

<!-- 该过滤器用于实现单点登出功能，可选配置。 -->  
<filter>  
    <filter-name>CAS Single Sign Out Filter</filter-name>  
    <filter-class>org.jasig.cas.client.session.SingleSignOutFilter</filter-class>  
</filter>  
<filter-mapping>  
    <filter-name>CAS Single Sign Out Filter</filter-name>  
    <url-pattern>/*</url-pattern>  
</filter-mapping>  
```

##### 2） 单点退出后，跳转指定页面

```html
<a href="http://localhost:9100/cas/logout?service=http://www.baidu.com">退出登录</a>
```

退出后，跳转到baidu。 url如下书写格式。 但是要需要修改配置文件：

##### 修改cas系统的配置文件cas-servlet.xml : FALSE --> TRUE

```xml
<bean id="logoutAction" class="org.jasig.cas.web.flow.LogoutAction"
        p:servicesManager-ref="servicesManager"
        p:followServiceRedirects="${cas.logout.followServiceRedirects:true}"/>
```



## 二、CAS服务端数据源设置 

前面讲的账户密码全部都是在配置文件写死了，一般情况，我们单点登录的账户密码都是数据库 对应的 user表。那么CAS服务端 必须配置数据源！！

### 2.1、配置数据源

- ##### 第一步：修改cas服务端中web-inf下deployerConfigContext.xml ，添加如下配置 

``` xml
<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource"  
			  p:driverClass="com.mysql.jdbc.Driver"  
			  p:jdbcUrl="jdbc:mysql://127.0.0.1:3306/pinyougoudb?characterEncoding=utf8"  
			  p:user="root"  
			  p:password="123456" /> 
<bean id="passwordEncoder" 
class="org.jasig.cas.authentication.handler.DefaultPasswordEncoder"  
		c:encodingAlgorithm="MD5"  
		p:characterEncoding="UTF-8" />  
<bean id="dbAuthHandler"  
		  class="org.jasig.cas.adaptors.jdbc.QueryDatabaseAuthenticationHandler"  
		  p:dataSource-ref="dataSource"  
		  p:sql="select password from tb_user where username = ?"  
		  p:passwordEncoder-ref="passwordEncoder"/>
```

- ##### 第二步：替换 authenticationManager bean信息 primaryAuthenticationHandler

```xml
<bean id="authenticationManager" class="org.jasig.cas.authentication.PolicyBasedAuthenticationManager">
        <constructor-arg>
            <map>
               <entry key-ref="proxyAuthenticationHandler" value-ref="proxyPrincipalResolver" />
                <!--OLD: <entry key-ref="primaryAuthenticationHandler"-->
               <entry key-ref="dbAuthHandler" value-ref="primaryPrincipalResolver" />
            </map>
        </constructor-arg>
    ...
</bean>
```

- ##### 第三步：将以下三个jar包放入webapps\cas\WEB-INF\lib下

```properties
c3p0.jar
cas-server-support-jdbc.jar
mysql-connector-java.jar
```



## 三、CAS服务端界面改造 

就是将目录登录页面，替换CAS本身的默认登录页面即可！

#### 1）由于CAS是 jsp, 需要将html换成jsp , 并添加进jsp引入的jstl标签！

```shell
D:\apache-tomcat-8.5.31\webapps\cas\WEB-INF\view\jsp\default\ui

将静态原型额login.html copy 到上目录， 改名为 casLoginView.jsp

将头标签引入：
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
```

#### 2）将html的form 用默认的form标签表单替换；

```jsp
<%--1、替换form表单：<form class="sui-form">--%>
<form:form class="sui-form" method="post" id="fm1" commandName="${commandName}" htmlEscape="true">				
```

#### 3）账户/密码框，登录按钮也要替换；

```JSP
<%-- 2、替换对应的输入框--%>
<div class="input-prepend"><span class="add-on loginname"></span>
    <%-- <input id="prependedInput" type="text" placeholder="邮箱/用户名/手机号" class="span2 input-xfat"> --%>
    <form:input type="text" placeholder="邮箱/用户名/手机号" class="span2 input-xfat" id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="off" htmlEscape="true" />
</div>
<div class="input-prepend"><span class="add-on loginpwd"></span>
    <%--<input id="prependedInput" type="password" placeholder="请输入密码" class="span2 input-xfat">--%>
    <form:password type="password" placeholder="请输入密码" class="span2 input-xfat" id="password" size="25" tabindex="2" path="password"  accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" />
</div>

<div class="logined">
    <%--<a class="sui-btn btn-block btn-xlarge btn-danger" href="home-index.html" >登&nbsp;&nbsp;录</a>--%>
    <input type="hidden" name="lt" value="${loginTicket}" />
    <input type="hidden" name="execution" value="${flowExecutionKey}" />
    <input type="hidden" name="_eventId" value="submit" />

    <input class="sui-btn btn-block btn-xlarge btn-danger" name="submit" accesskey="l" value="登&nbsp;&nbsp;录" tabindex="4" type="submit" />
</div>		
```

#### 4）添加错误提示标签：

```jsp
<span class="forget">忘记密码？</span>
<form:errors path="*" id="msg" cssClass="errors" element="div" htmlEscape="false" />
```

> 注意：i18n 国际化配置！！
>
> ```shell
> # 默认使用 下面路径的配置
> D:\apache-tomcat-8.5.31\webapps\cas\WEB-INF\classes\message.properties
> 
> # 第1步 那么修改为中文配置，添加两条
> D:\apache-tomcat-8.5.31\webapps\cas\WEB-INF\classes\message_zh_CN.properties
> 
> authenticationFailure.AccountNotFoundException=\u7528\u6237\u4E0D\u5B58\u5728\u6216\u5BC6\u7801\u9519\u8BEF.
> authenticationFailure.FailedLoginException=\u7528\u6237\u4E0D\u5B58\u5728\u6216\u5BC6\u7801\u9519\u8BEF.
> 
> #用户不存在或密码错误 \u7528\u6237\u4E0D\u5B58\u5728\u6216\u5BC6\u7801\u9519\u8BEF： 在eclispe工具的properties文件直接输入，即可得到转义中文对应码。
> 
> # 第2步，修改使用zh_CN配置
> D:\apache-tomcat-8.5.31\webapps\cas\WEB-INF\cas-servlet.xml 
> 
>   <!-- Locale Resolver -->
> <bean id="localeResolver" class="org.springframework.web.servlet.i18n.CookieLocaleResolver" p:defaultLocale="zh_CN" /> <!-- en -> zh_CN -->
> ```



## 四、CAS客户端与SpringSecurity集成（重点）

- spring-security.xml 集成CAS

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans:beans xmlns="http://www.springframework.org/schema/security"
	xmlns:beans="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
						http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security.xsd">
	
	<http pattern="/index2.html" security="none"></http>
	
	<!--   entry-point-ref  入口点引用 -->
	<http use-expressions="false" entry-point-ref="casProcessingFilterEntryPoint">  <!-- 入口点只是一个接口而已，具体操作是CAS入口点配置 -->
        <intercept-url pattern="/**" access="ROLE_USER"/>   
        <csrf disabled="true"/>  
        <!-- custom-filter为过滤器， position 表示将过滤器放在指定的位置上，before表示放在指定位置之前  ，after表示放在指定的位置之后  -->           
        <custom-filter ref="casAuthenticationFilter"  position="CAS_FILTER" />      
        <custom-filter ref="requestSingleLogoutFilter" before="LOGOUT_FILTER"/>  
        <custom-filter ref="singleLogoutFilter" before="CAS_FILTER"/>  
    </http>
    
  	<!-- CAS入口点 开始 -->
    <beans:bean id="casProcessingFilterEntryPoint" class="org.springframework.security.cas.web.CasAuthenticationEntryPoint">  
        <!-- 单点登录服务器登录URL -->  
        <beans:property name="loginUrl" value="http://localhost:9100/cas/login"/>  
        <beans:property name="serviceProperties" ref="serviceProperties"/>  
    </beans:bean>      
    <beans:bean id="serviceProperties" class="org.springframework.security.cas.ServiceProperties">  
        <!--service 配置自身工程的根地址+/login/cas   -->  
        <beans:property name="service" value="http://localhost:9003/login/cas"/>
    </beans:bean>  
    <!-- CAS入口点 结束 -->
    
    <!-- 认证过滤器 开始 -->
    <beans:bean id="casAuthenticationFilter" class="org.springframework.security.cas.web.CasAuthenticationFilter">  
        <beans:property name="authenticationManager" ref="authenticationManager"/>  
    </beans:bean>  
		<!-- 认证管理器 -->
	<authentication-manager alias="authenticationManager">
		<authentication-provider  ref="casAuthenticationProvider">
		</authentication-provider>
	</authentication-manager>
		<!-- 认证提供者 -->
	<beans:bean id="casAuthenticationProvider"     class="org.springframework.security.cas.authentication.CasAuthenticationProvider">  
        <beans:property name="authenticationUserDetailsService">  
            <beans:bean class="org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper">  
                <beans:constructor-arg ref="userDetailsService" />  
            </beans:bean>  
        </beans:property>  
        <beans:property name="serviceProperties" ref="serviceProperties"/>  
        <!-- ticketValidator 为票据验证器 -->
        <beans:property name="ticketValidator">  
            <beans:bean class="org.jasig.cas.client.validation.Cas20ServiceTicketValidator">  
                <beans:constructor-arg index="0" value="http://localhost:9100/cas"/>  
            </beans:bean>  
        </beans:property>  
        <beans:property name="key" value="an_id_for_this_auth_provider_only"/> 
    </beans:bean>        
   		 <!-- 认证类 -->
	<beans:bean id="userDetailsService" class="cn.itcast.demo.service.UserDetailServiceImpl"/> 
	<!-- 认证过滤器 结束 -->
	
	<!-- 单点登出  开始 ：服务端真正处理单点登出的过滤器  -->     
    <beans:bean id="singleLogoutFilter" class="org.jasig.cas.client.session.SingleSignOutFilter"/>    
    <!-- 经过此配置，当用户在地址栏输入本地工程 /logout/cas： 映射关系  -->      
    <beans:bean id="requestSingleLogoutFilter" class="org.springframework.security.web.authentication.logout.LogoutFilter">  
        <beans:constructor-arg value="http://localhost:9100/cas/logout?service=http://localhost:9003/index2.html"/>  
        <beans:constructor-arg>  
            <beans:bean class="org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler"/>  
        </beans:constructor-arg>  
        <beans:property name="filterProcessesUrl" value="/logout/cas"/>  
    </beans:bean>  
    <!-- 单点登出  结束 -->  	
</beans:beans>
```



## 五、实现品优用户中心 -- 单点登录

参考讲义！















