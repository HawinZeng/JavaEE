# SSM综合案例_04 权限关联控制／AOP日志

## 一、权限关联

### 1.1、用户与角色关联

```java
@RequestMapping("/findUserByIdAndAllRole.do")
public ModelAndView findUserByIdAndAllRole(@RequestParam(name = "id",required = true) String userId) throws Exception{
    ModelAndView mv = new ModelAndView();
    UserInfo userInfo = userService.findById(userId);
    // 可以添加的其他角色集合
    List<Role> roleList = userService.findOtherRoles(userId);

    mv.setViewName("user-role-add");
    mv.addObject("user",userInfo);
    mv.addObject("roleList",roleList);
    return mv;
}

@RequestMapping("/addRoleToUser.do")
public String addRoleToUser(@RequestParam(name = "userId",required = true) String userId,
                            @RequestParam(name = "ids",required = true) String[] roleIds) throws Exception{

    userService.addRoleToUser(userId,roleIds);
    return "redirect:findAll.do";
}
```

```java
@Override
public UserInfo findById(String id) throws Exception {
    return userDao.findById(id);
}

@Override
public List<Role> findOtherRoles(String userId) throws Exception {
    return userDao.findOtherRoles(userId);
}

@Override
public void addRoleToUser(String userId, String[] roleIds) throws Exception {
    for(String roleId:roleIds){
        userDao.addRoleToUser(userId,roleId);
    }
}
```

```java
@Select(" select * from users where id=#{id} ")
@Results(value = {
    @Result(id = true, property = "id", column = "id"),
    @Result(property = "username", column = "username"),
    @Result(property = "email", column = "email"),
    @Result(property = "password", column = "password"),
    @Result(property = "phoneNum", column = "phoneNum"),
    @Result(property = "status", column = "status"),
    @Result(property = "roles", column = "id", javaType = List.class, many = @Many(select = "com.eoony.dao.IRoleDao.findByUserId",fetchType = FetchType.LAZY))
})
UserInfo findById(String id) throws Exception;

@Select(" select * from role where id not in ( select roleId from users_role where userId=#{userId} ) ")
List<Role> findOtherRoles(String userId) throws Exception;

@Insert(" insert into users_role(userId,roleId) values(#{userId},#{roleId}) ") // 多参数，需要指定参数名称
void addRoleToUser(@Param("userId") String userId, @Param("roleId") String roleId) throws Exception;
```

> #### 这里重点：多参数需要指出参数名称！！！前面好像没有遇到此类情况！！！

### 1.2、角色与权限关联

```java
@RequestMapping("/findRoleByIdAndAllPermission.do")
public ModelAndView findRoleByIdAndAllPermission(@RequestParam(name = "id",required = true) String roleId) throws Exception{
    ModelAndView mv = new ModelAndView();

    Role role = roleService.findById(roleId);
    List<Permission> permissionList = roleService.findOtherPermissions(roleId);

    mv.addObject("role",role);
    mv.addObject("permissionList",permissionList);
    mv.setViewName("role-permission-add");

    return mv;
}

@RequestMapping("/addPermissionToRole.do")
public String addPermissionToRole(@RequestParam(name = "roleId") String roleId,
                                  @RequestParam(name = "ids") String[] permissionIds) throws Exception{
    roleService.addPermissionToRole(roleId,permissionIds);
    return "redirect:findAll.do";
}
```

```java
@Select(" select * from role where id = #{roleId} ")
@Results({
    @Result(id = true,property = "id",column = "id"),
    @Result(property = "roleName",column = "roleName"),
    @Result(property = "roleDesc",column = "roleDesc"),
    @Result(property = "permissions",column = "id",javaType = List.class,many = @Many(select = "com.eoony.dao.IPermissionDao.findByRoleId",fetchType = FetchType.LAZY))
})
Role findById(String roleId) throws Exception;

@Select( " select * from permission where id not in (select permissionId from role_permission where roleId = #{roleId} ) ")
List<Permission> findOtherPermissions(String roleId) throws Exception;

@Insert(" insert into role_permission(roleId,permissionId) values(#{roleId},#{permissionId}) ")
void addPermissionToRole(@Param("roleId") String roleId, @Param("permissionId") String permissionId) throws Exception;
```



## 二、权限控制

### 2.1、服务器端方法级权限控制

在服务器端我们可以通过Spring security提供的注解对方法来进行权限控制。Spring Security在方法的权限控制上
支持三种类型的注解：

​	JSR-250注解、

​	@Secured注解

​	支持表达式的注解,

这三种注解默认都是没有启用的，需要单独通过global-method-security元素的对应属性进行启用。

#### 2.1.1、JSR-250注解

- 第一步：在spring-security.xml配置开启注解

  ```
  <security:global-method-security jsr250-annotations="enabled"/>
  ```

- 第二步：使用JSR-250注解

  ```java
  @RequestMapping("/findAll.do")
  @RolesAllowed("ADMIN")
  public ModelAndView findAll() throws Exception {
      ModelAndView mv = new ModelAndView();
      List<UserInfo> userList = userService.findAll();
      mv.setViewName("user-list");
      mv.addObject("userList",userList);
      return mv;
  }
  --------------------------------------------------------
  1. @RolesAllowed表示访问对应方法时所应该具有的角色
  2. @PermitAll表示允许所有的角色进行访问,也就是说不进行权限控制
  3. @DenyAll是和PermitAll相反的,表示无论什么角色都不能访问
  ```

- 第三步：在pom.xml导入注解依赖

  ```xml
  <dependency>
      <groupId>javax.annotation</groupId>
      <artifactId>jsr250-api</artifactId>
      <version>1.0</version>
  </dependency>
  ```

  > ##### 当权限不足，报403错误时，可以设计一个友好的显示界面403.jsp。
  >
  > ##### 在web.xml配置：
  >
  > ```xml
  >   <error-page>
  >     <error-code>403</error-code>
  >     <location>/403.jsp</location>
  >   </error-page>
  > ```



#### 2.1.2、@Secured注解

- 第一步：在spring-security.xml配置开启注解

  ```xml
  <security:global-method-security secured-annotations="enabled"/>
  ```

- 第二步：使用@Secured注解

  ```java
  @RequestMapping("/findAll.do")
  @Secured("ROLE_ADMIN") // error:－－> @Secured("ADMIN") 
  public ModelAndView findAll() throws Exception {
      ModelAndView mv = new ModelAndView();
      List<Role> roleList = roleService.findAll();
      mv.setViewName("role-list");
      mv.addObject("roleList",roleList);
      return mv;
  }
  ```

  > 上述设置权限后，有ADMIN权限的账户仍然打不开，权限不足，为啥？
  >
  > ##### 区分：JSR-250  vs  @Secured注解
  >
  > 1） jar-250会自动补全ROLE_前缀，然后组合成了ROLE_ADMIN权限名称，这才是security.xml配置的；而@Secured 不会自动补全。
  >
  > 2）JSR-250 需要导入依赖包，@Secured则无需额外导包。



#### 2.1.3、支持表达式的注解 (Spring EL / SPEL表达式)

- 第一步：在spring-security.xml配置开启注解

  ```xml
  <security:global-method-security pre-post-annotations="enabled"/>
  ```

- 第二步：使用表达式的注解

  ```java
  @RequestMapping("/findAll.do")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ModelAndView findAll() throws Exception {
      ModelAndView mv = new ModelAndView();
      List<UserInfo> userList = userService.findAll();
      mv.setViewName("user-list");
      mv.addObject("userList",userList);
      return mv;
  }
  
  
  @RequestMapping("/save.do")
  @PreAuthorize("authentication.principal.username == 'tom'")
  public String save(UserInfo userInfo) throws Exception{
      userService.save(userInfo);
      return "redirect:findAll.do";
  }
  ```

  > ##### @PreAuthorize 在方法调用之前,基于表达式的计算结果来限制对方法的访问:
  >
  > ```java
  > @PreAuthorize("#userId == authentication.principal.userId or  hasAuthority(‘ADMIN’)")
  > void changePassword(@P("userId") long userId ){
  >     ...
  > } 
  > 这里表示在changePassword方法执行之前,判断方法参数userId的值是否等于principal中保存的当前用户的 userId,或者当前用户是否具有ROLE_ADMIN权限,两种符合其一,就可以访问该方法。
  > ```
  >
  > ##### @PostAuthorize 允许方法调用,但是如果表达式计算结果为false,将抛出一个安全性异常:
  >
  > ##### @PostFilter 允许方法调用,但必须按照表达式来过滤方法的结果
  >
  > ##### @PreFilter 允许方法调用,但必须在进入方法之前过滤输入值

- #### 扩展SPEL

Spring Security允许我们在定义URL访问或方法访问所应有的权限时使用Spring EL表达式，在定义所需的访问权限时如果对应的表达式返回结果为true则表示拥有对应的权限，反之则无。Spring Security可用表达式对象的基类是SecurityExpressionRoot，其为我们提供了如下在使用Spring EL表达式对URL或方法进行权限控制时通用的内置表达式。

| **表达式**                     | **描述**                                                     |
| ------------------------------ | ------------------------------------------------------------ |
| hasRole([role])                | 当前用户是否拥有指定角色。                                   |
| hasAnyRole([role1,role2])      | 多个角色是一个以逗号进行分隔的字符串。如果当前用户拥有指定角色中的任意一个则返回true。 |
| hasAuthority([auth])           | 等同于hasRole                                                |
| hasAnyAuthority([auth1,auth2]) | 等同于hasAnyRole                                             |
| Principle                      | 代表当前用户的principle对象                                  |
| authentication                 | 直接从SecurityContext获取的当前Authentication对象            |
| permitAll                      | 总是返回true，表示允许所有的                                 |
| denyAll                        | 总是返回false，表示拒绝所有的                                |
| isAnonymous()                  | 当前用户是否是一个匿名用户                                   |
| isRememberMe()                 | 表示当前用户是否是通过Remember-Me自动登录的                  |
| isAuthenticated()              | 表示当前用户是否已经登录认证成功了。                         |
| isFullyAuthenticated()         | 如果当前用户既不是一个匿名用户，同时又不是通过Remember-Me自动登录的，则返回true。 |

###  

### 2.2、页面端标签控制权限

- #### 第一步：导入依赖

  ```xml
  <dependency>
      <groupId>org.springframework.security</groupId>
      <artifactId>spring-security-taglibs</artifactId>
      <version>version</version>
  </dependency>
  ```

- #### 第二步：页面标签导入

  ```xml
  <%@taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
  ```

- #### 第三步：在页面使用权限标签

  1. ##### authentication标签：可以获取当前正在操作的用户信息

     显示对应用户在线：在header.jsp、aside.jsp中添加

  ```xml
  <p><security:authentication property="principal.username"/></p> 
  ```

  > ##### 属性必须是：principal.username！！！

  2. ##### authorize标签：用于控制页面上某些标签是否可以显示

     对应用户，显示不同功能栏目：(栏目可见／不可见)

  ```xml
  <security:authorize access="hasRole('ADMIN')">
  	<li id="system-setting">
      	<a href="${pageContext.request.contextPath}/user/findAll.do"> 
          	<i class="fa fa-circle-o"></i> 
          	用户管理
  		</a>
      </li>
  </security:authorize>
  ```

  > 说明两点：由于在页面使用了SPEL表达式，在security.xml配置要做相应处理！
  >
  > ##### 1. 不改 http标签，那么添加一个DefaultWebSecurityExpressionHandler bean对象；
  >
  > ```xml
  >  <security:http auto-config="true" use-expressions="false">
  >         <!-- 配置具体的拦截的规则 pattern="请求路径的规则" access="访问系统的人，必须有ROLE_USER的角色" -->
  >         <security:intercept-url pattern="/**" access="ROLE_USER,ROLE_ADMIN"/>
  >      ....
  > </security:http>
  > 
  >  <bean id="webexpressionHandler" class="org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler" />
  > ```
  >
  > ##### 2. 不添加bean对象，则需要修改http标签配置；
  >
  > ```xml
  > <security:http auto-config="true" use-expressions="true">
  >     <!-- 配置具体的拦截的规则 pattern="请求路径的规则" access="访问系统的人，必须有ROLE_USER的角色" -->
  >     <security:intercept-url pattern="/**" access="hasAnyRole('ROLE_USER','ROLE_ADMIN')"/>
  > </security:http>
  > ```



## 三、SSMAOP日志

- #### SysLog

```java
// 从日志的实体数据看，日志主要作用：
// 1. 可以统计方法运行的时长，作为后期优化观察点
// 2. 可以统计访问的峰值时间点
// 3. 可以统计哪个用户，哪个url访问次数，业务数据观察点
public class Syslog {
    private String id; // 主键 无意义uuid
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date visitTime; // 访问时间
    private String visitTimeStr; // 用于显示
    private String username; // 操作者用户名
    private String ip; // 访问ip
    private String url; // 访问资源url
    private Long executionTime; // 执行时长
    private String method; // 访问方法
}
```

> 如何记录上面的数据：
>
> ##### 可以通过AOP切面编程，在方法调用前（前置通知），方法调用后（最终通知）分别记录某些数据，即可得到相应的数据项！ 注意是用（前置通知@Before）和（最终通知@After），而后置通知@AfterReturning，异常通知@AfterThrowing使用会有顺序问题，请注意！同时，若实在业务需要使用这两个通知，也请在环绕通知里面调用，防止顺序异常问题！！

- #### LogAOP切面

```java
@Component
@Aspect
public class LogAop {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ISysLogService logService;
    
    private Date visitTime;
    // 访问的类
    private Class clazz;
    // 访问的方法
    private Method method;
    private String url = "";
    private  String ip;
    private String username;

    @Pointcut("execution(* com.eoony.controller.*.*(..))")
    public void ft(){ }

    @Before("ft()")
    public void doBefore(JoinPoint jp) throws NoSuchMethodException {
        visitTime = new Date();
        clazz = jp.getTarget().getClass();
        String methodName = jp.getSignature().getName();
        Object[] args = jp.getArgs();
        if(args==null || args.length ==0){
            method = clazz.getMethod(methodName);
        }else{
            int length = args.length;
            Class[] clzs = new Class[length];
            for(int i=0;i<length;i++){
                clzs[i] = args[i].getClass();
            }
            method = clazz.getMethod(methodName,clzs);
        }
    }

    @After("ft()")
    public void doAfter(JoinPoint jp) throws Exception {

        if(clazz == null|| method==null ||clazz == SysLogController.class || clazz == LogAop.class ) return; // 不记录

        long time = new Date().getTime() - visitTime.getTime(); // 访问的时长

        RequestMapping classAnnotation = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
        RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);

        StringBuilder sb = new StringBuilder();

        if(classAnnotation!=null){
            sb.append(classAnnotation.value()[0]);
        }
        if(methodAnnotation!=null){
            sb.append(methodAnnotation.value()[0]);
        }
        url = sb.toString();

        ip = request.getRemoteAddr();
        SecurityContext context = SecurityContextHolder.getContext();
        User user = (User) context.getAuthentication().getPrincipal();
        username = user.getUsername();
//        username = (String) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT"); //? user or username
        Syslog syslog = new Syslog();
        syslog.setExecutionTime(time);
        syslog.setIp(ip);
        syslog.setUrl(url);
        syslog.setVisitTime(visitTime);
        syslog.setUsername(username);
        syslog.setMethod(method.getName());

        logService.save(syslog); // 保存日志记录
    }
}
```

> #### 注意几点：
>
> 1. ##### 在LogAOP中使用HttpServletRequest ，需要在web.xml配置监听器RequestContextListener
>
>    ```xml
>    <listener>
>        <listener-class>
>            org.springframework.web.context.request.RequestContextListener
>        </listener-class>
>    </listener>
>    ```
>
> 2. ##### 过滤一些非必要数据记录，如记录日志操作，或者操作LogAop的操作过滤
>
>    ```java
>    if(clazz == null|| method==null ||clazz == SysLogController.class || clazz == LogAop.class ) return; // 不记录
>    ```
>
> 3. ##### 反射调用，方法带参数，不能是基本类型，最好转化包装类型
>
>    ```java
>    @RequestMapping("/findPage.do")
>    public ModelAndView findPage(int page,int size) throws Exception {...}
>    // -->
>    @RequestMapping("/findPage.do")
>    public ModelAndView findPage(Integer page,Integer size) throws Exception {...}
>    ```

- #### ISysLogDao

```java
public interface ISysLogDao {

    @Insert("insert into sysLog(visitTime,username,ip,url,executionTime,method) values(#{visitTime},#{username},#{ip},#{url},#{executionTime},#{method}) ")
    void save(Syslog syslog) throws Exception;

    @Select("select * from sysLog")
    List<Syslog> findAll() throws Exception;
}
```

- #### SysLogController

```java
@Controller
@RequestMapping("/sysLog")
public class SysLogController {

    @Autowired
    private ISysLogService sysLogService;

    @RequestMapping("/findAll.do")
    public ModelAndView findAll() throws Exception{
        ModelAndView mv = new ModelAndView();
        List<Syslog> syslogList = sysLogService.findAll();
        mv.setViewName("syslog-list");
        mv.addObject("sysLogs",syslogList);
        return mv;
    }
}
```













