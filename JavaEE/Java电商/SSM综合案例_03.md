# SSM综合案例_03 权限控制／Spring Security

## 一、数据库与表的分析

user －－ role： 用户与角色多对多关系；

role －－ permission：用户与权限资源多对多关系；

```sql
CREATE TABLE users(
    id varchar2(32) default SYS_GUID() PRIMARY KEY,
    email VARCHAR2(50) UNIQUE NOT NULL,
    username VARCHAR2(50),
    PASSWORD VARCHAR2(50),
    phoneNum VARCHAR2(20),
    STATUS INT
)

 
CREATE TABLE role(
    id varchar2(32) default SYS_GUID() PRIMARY KEY,
    roleName VARCHAR2(50) ,
    roleDesc VARCHAR2(50)
)

CREATE TABLE users_role(
    userId varchar2(32),
    roleId varchar2(32),
    PRIMARY KEY(userId,roleId),
    FOREIGN KEY (userId) REFERENCES users(id),
    FOREIGN KEY (roleId) REFERENCES role(id)
)

 CREATE TABLE permission(
    id varchar2(32) default SYS_GUID() PRIMARY KEY,
    permissionName VARCHAR2(50) ,
    url VARCHAR2(50)
)

 CREATE TABLE role_permission(
    permissionId varchar2(32),
    roleId varchar2(32),
    PRIMARY KEY(permissionId,roleId),
    FOREIGN KEY (permissionId) REFERENCES permission(id),
    FOREIGN KEY (roleId) REFERENCES role(id)
)
```



## 二、Spring Security

### 2.1 Spring Security介绍 

Spring Security 的前身是 Acegi Security ,是 Spring 项目组中用来提供安全认证服务的框架。 (https://projects.spring.io/spring-security/) Spring Security 为基于J2EE企业应用软件提供了全面安全服务。特别 是使用领先的J2EE解决方案-Spring框架开发的企业软件项目。人们使用Spring Security有很多种原因,不过通常吸 引他们的是在J2EE Servlet规范或EJB规范中找不到典型企业应用场景的解决方案。 特别要指出的是他们不能再 WAR 或 EAR 级别进行移植。这样,如果你更换服务器环境,就要,在新的目标环境进行大量的工作,对你的应用 系统进行重新配 置安全。使用Spring Security 解决了这些问题,也为你提供很多有用的,完全可以指定的其他安 全特性。 安全包括两个主要操作。 

“认证”,是为用户建立一个他所声明的主体。主题一般式指用户,设备或可以在你系 统中执行动作的其他系 统。
 “授权”指的是一个用户能否在你的应用中执行某个操作,在到达授权判断之前,身份的主题已经由 身份验证 过程建立了。 

这些概念是通用的,不是Spring Security特有的。在身份验证层面,Spring Security广泛支持各种身份验证模式, 这些验证模型绝大多数都由第三方提供,或则正在开发的有关标准机构提供的,例如 Internet Engineering Task Force.作为补充,Spring Security 也提供了自己的一套验证功能。 

### 2.2、Spring Security快速入门

- #### pom.xml maven依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-web</artifactId>
        <version>5.0.1.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-config</artifactId>
        <version>5.0.1.RELEASE</version>
    </dependency>
</dependencies>
```































