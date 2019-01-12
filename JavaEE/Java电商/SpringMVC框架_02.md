# SpringMVC框架_02

## 一、响应数据和结果视图

### 1.1、返回值分类

#### －返回字符串：

##### 1. Controller方法返回字符串可以指定逻辑视图的名称,根据视图解析器为物理视图的地址。

```java
@RequestMapping(value="/hello")
public String sayHello() {
    System.out.println("Hello SpringMVC!!"); 
    return "success"; // 返回这个success字符串，即视图解析器匹配到success.jsp页面
}
```

```xml
<!--配置视图解析器-->
<bean id="internalResourceViewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="prefix" value="/WEB-INF/pages/"/>
    <property name="suffix" value=".jsp"/>
</bean>
```

##### 2. 具体的应用场景

```java
@Controller
@RequestMapping("/user")
public class UserController {
    @RequestMapping(value="/initUpdate")
    public String initUpdate(Model model) {
        // 模拟从数据库中查询的数据
        User user = new User(); 
        user.setUsername("张三"); 
        user.setPassword("123"); 
        user.setAge(18); 
        model.addAttribute("user", user); 
        return "update"; // 跳转到update.jsp,并保存一个user到requestScope
    } 
} 
```

```jsp
<!--update.jsp-->
<h3>修改用户</h3>
${ requestScope }
<form action="user/update" method="post">
    姓名:<input type="text" name="username" value="${ user.username }"><br> 
    密码:<input type="text" name="password" value="${ user.password }"><br> 
    年龄:<input type="text" name="age" value="${ user.age }"><br> 
    <input type="submit" value="提交">
</form>
```

#### －返回值是void：

1. ##### 如果控制器的方法返回值编写成void,执行程序报404的异常,默认查找JSP页面没有找到。

   默认会跳转到/user/initUpdate.jsp的页面［以@RequestMapping(value="/initUpdate")为属性值“字符串”］，而在视图解析器路径下没有。

2. ##### 可以使用请求转发或者重定向跳转到指定的页面

   ```java
   @RequestMapping(value="/initAdd")
   public void initAdd(HttpServletRequest request,HttpServletResponse response) throws
       Exception {
       System.out.println("请求转发或者重定向");
       // 请求转发
       // request.getRequestDispatcher("/WEB-INF/pages/add.jsp").forward(request,
       response);
       // 重定向
       // response.sendRedirect(request.getContextPath()+"/add2.jsp");
       response.setCharacterEncoding("UTF-8");
       response.setContentType("text/html;charset=UTF-8");
       // 直接响应数据 
       response.getWriter().print("你好"); 
       // return;
   }
   ```

#### －返回值是ModelAndView对象：

```java
@RequestMapping("/testModelAndView")
    public ModelAndView testModelAndView(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("list"); // 使用这个字符串去查找对应的视图

        List<User> users = new ArrayList<User>();

        User user1 = new User();
        user1.setUsername("张三");
        user1.setAge(28);
        user1.setPassword("123456");
        users.add(user1);

        User user2 = new User();
        user2.setUsername("王五");
        user2.setAge(38);
        user2.setPassword("112233");
        users.add(user2);

        mv.addObject("users",users); // 相当于ModelMap.setAttribute,即存入requestScope
        return mv;
    }
```

```html
<!--  视图解析器下的 list.jsp  -->
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <h3>return ModelView</h3>
    <hr>
    <c:forEach items="${users}" var="user">
        ${user.username}<br>
    </c:forEach>
</body>
</html>
```

### 1.2、SpringMVC框架提供的转发和重定向（关键字）

```java
/*
 * 使用forward关键字进行请求转发
 * "forward:转发的JSP路径",不走视图解析器了,所以需要编写完整的路径 
 */
@RequestMapping("/delete")
public String delete() throws Exception {
    System.out.println("delete方法执行了...");
    // return "forward:/WEB-INF/pages/success.jsp"; 
    return "forward:/user/findAll";
}

/*
 * 重定向
 * @return
 */
@RequestMapping("/count")
public String count() throws Exception {
System.out.println("count方法执行了..."); 
    return "redirect:/add.jsp";
// return "redirect:/user/findAll";
}
```



### 1.3、ResponseBody响应json数据

##### 问题：  DispatcherServlet会拦截到所有的资源，导致一个问题就是静态资源(img、css、js)也会被拦截到,从而 不能被使用。

> ##### 解决问题就是需要配置静态资源不进行拦截,在springmvc.xml配置文件添加如下配置!
>
> ##### `mvc:resources`标签配置不过滤 
>
> 1. location元素表示webapp目录下的包下的所有文件;
> 2. mapping元素表示以/static开头的所有请求路径,如/static/a 或者/static/a/b ;
>
> ```xml
> <!-- 设置静态资源不过滤 -->
> <mvc:resources location="/css/" mapping="/css/**"/> <!-- 样式 --> 
> <mvc:resources location="/images/" mapping="/images/**"/> <!-- 图片 --> 
> <mvc:resources location="/js/" mapping="/js/**"/> <!-- javascript -->
> ```

- d

```jsp
<head>
    <title>Title</title>
    <script src="js/jquery-3.3.1.js" type="text/javascript"></script>
    <script>
        $(function () {
            $("#btn").click(function () {
                // alert("1111");
                //contentType:"application/json;charset=UTF-8", -- 明确告知服务器，传递参数是一个json，对应controller方法才能正确对数据进行封装，
                // 否则相对于无法找到正确的请求路径 415 错误
                $.ajax({ 
                    url:"user/testAjax",
                    type:"POST",
                    contentType:"application/json;charset=UTF-8",
                    // 一定要有单引号，否则不是json字符串，到后台那边也无法数据封装，报400错误
                    data:'{"username":"haha","password":"1234","age":"18"}', 
                    dataType:"json",
                    success:function (user) {
                        alert(user);
                        alert(user.username);
                        alert(user.password);
                        alert(user.age);
                    }
                });

                // $.post("testAjax",
                //     {"username":"haha","password":"1234","age":"18"},
                //     function (user) {
                //         alert(user);
                //         alert(user.username);
                //         alert(user.password);
                //         alert(user.age);
                //     },"json"
                // );
            });
        });
    </script>
</head>
```

> 注意：
>
> ```javascript
> $.ajax({}); ---> 
>     // 这个任意版本的jquery都支持。同时，支持多参数配置形式，如上面的contentType参数，用$.post()方法就无法做到，因为其是一个简单的$.ajax({})，只封装了4个常用的参数，url,data,function,dataType.选择有限。故，后续开发，推荐使用$.ajax({});，代码表述也清晰明了！
> $.post(); 
> 	// 需要jquery-3.3.1.js的支持！
> ```



## 二、SpringMVC实现文件上传

### 2.1、传统方式文件上传

- ##### 文件上传的必要前提

  ```properties
  A. form表单的enctype取值必须是: multipart/form-data 
  		(默认值是: application/x-www-form-urlencoded)
  		enctype: 是表单请求正文的类型
  		
  B. method属性取值必须是: Post		
  c. 提供一个文件选择域: <input type=”file” />
  ```

- ##### 1. 引入jar : commons-fileupload.jar,  commons-io.jar

  ```xml
  <dependency>
      <groupId>commons-fileupload</groupId>
      <artifactId>commons-fileupload</artifactId>
      <version>1.3.1</version>
  </dependency>
  
  <dependency>
      <groupId>commons-io</groupId>
      <artifactId>commons-io</artifactId>
      <version>2.5</version>
  </dependency>
  ```

- ##### 2. 编写文件上传的JSP页面

  ```jsp
  <body>
      <h3>传统方式文件上传</h3>
      <br/>
      <form action="file/fileupload1" method="post" enctype="multipart/form-data">
          请选择上传的文件：<input type="file" name="upload"/><br/>
          <input type="submit" value="提交"/>
      </form>
      <hr>
      <h3>SpringMVC方式文件上传</h3>
      <br/>
       <form action="file/fileupload2" method="post" enctype="multipart/form-data">
          请选择上传的文件：<input type="file" name="upload"/><br/>
          <input type="submit" value="提交"/>
      </form>
      <hr>
  </body>
  ```

- ##### 3. controller的文件上传方法

  ```java
  @Controller
  @RequestMapping("/file")
  public class FileController {
      /**
       * 传统方式文件上传
       */
      @RequestMapping("/fileupload1")
      public String fileupload1(HttpServletRequest request) throws Exception {
          System.out.println("传统方式文件上传 ！！！！！");
          // 指定文件上传位置
          String path = request.getSession().getServletContext().getRealPath("/uploads");
          File file = new File(path);
          if(!file.exists()){
              file.mkdirs();
          }
          // 创建磁盘文件项工厂
          DiskFileItemFactory factory = new DiskFileItemFactory();
          ServletFileUpload fileUpload = new ServletFileUpload(factory);
          // 解析request对象
          List<FileItem> list = fileUpload.parseRequest(request);
          for(FileItem item:list){
              // 判断文件项是普通字段,还是上传的文件
              if(item.isFormField()){
  
              }else{
                  String fileName = item.getName();
                  String uuid = UUID.randomUUID().toString().replace("-", "");
                  fileName = uuid +"_"+ fileName;
                  // 上传文件
                  item.write(new File(path,fileName));
                  // 删除临时文件
                  item.delete();
              }
          }
          return "success";
      }
  }
  ```

### 2.2、SpringMVC 方式文件上传

- ##### 在springmvc.xml中配置文件解析器对象 :

```xml
<!--配置一个文件解析器, id名称是固定的唯一形式：multipartResolver-->
<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
    <!--10M -->
    <property name="maxUploadSize" value="10485760"/>
</bean>
```

> ##### 注意：配置后就不能使用传统方式上传了，因为文件解析器会拦截request文件数据。
>
> ```java
> List<FileItem> list = fileUpload.parseRequest(request) // 将是一个空数据集合！！
> ```

- ##### 2. controller的文件上传方法

```java
/**
 * SpringMVC方式文件上传
 */
@RequestMapping("/fileupload2") 
// 特别注意：参数名upload一定要于jsp中<input type="file" name="upload"/>的name一致！！
public String fileupload2(HttpServletRequest request,MultipartFile upload) throws Exception {
    System.out.println("SpringMVC方式文件上传 ！！！！！");
    // 指定文件上传位置
    String path = request.getSession().getServletContext().getRealPath("/uploads");
    File file = new File(path);
    if(!file.exists()){
        file.mkdirs();
    }
    String fileName = upload.getOriginalFilename();
    String uuid = UUID.randomUUID().toString().replace("-", "");
    fileName = uuid +"_"+ fileName;
    // 上传文件
    upload.transferTo(new File(file,fileName));
    return "success";
}
```



### 2.3、SpringMVC跨服务器方式文件上传

- #### 1. 分服务器的目的

  ```properties
  在实际开发中,我们会有很多处理不同功能的服务器。例如:
    应用服务器: 负责部署我们的应用
    数据库服务器: 运行我们的数据库
    缓存和消息服务器: 负责处理大并发访问的缓存和消息
    文件服务器: 负责存储用户上传文件的服务器。
  	(注意,此处说的不是 服务器集群)
  ```

- #### 2. fileServer创建

  ##### 2.1  web.xml: DefaultServlet一定要修正可以写入数据，否则是无法上传成功！

  ```xml
  <web-app>
    <display-name>Archetype Created Web Application</display-name>
  
    <servlet>
      <servlet-name>default</servlet-name>
      <servlet-class>org.apache.catalina.servlets.DefaultServlet</servlet-class>
      <init-param>
        <param-name>debug</param-name>
        <param-value>0</param-value>
      </init-param>
      <!-- 添加 -->
      <init-param>
        <param-name>readonly</param-name>
        <param-value>false</param-value>
      </init-param>
      <!-- -->
      <init-param>
        <param-name>listings</param-name>
        <param-value>false</param-value>
      </init-param>
      <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
      <servlet-name>default</servlet-name>
      <url-pattern>/</url-pattern>
    </servlet-mapping>
  </web-app>
  ```

  > #### 为啥是DefaultServlet？ 待深入Tomcat！

- #### 3. 应用服务器

  ```java
  @RequestMapping("/fileupload3")
  public String fileupload3(MultipartFile upload) throws Exception {
      System.out.println("SpringMVC方式文件上传 ！！！！！");
      // 定位文件服务器的路径
      String path = "http://localhost:9090/fileupload/uploads/";
  	// 获取到上传文件的名称
      String fileName = upload.getOriginalFilename();
      String uuid = UUID.randomUUID().toString().replace("-", "");
      fileName = uuid +"_"+ fileName;
      // 向图片服务器上传文件
      // 1. 创建客户端对象
      Client client = Client.create();
  	// 2. 连接图片服务器
      WebResource webResource = client.resource(path+fileName);
  	// 3. 上传文件
      webResource.put(upload.getBytes());
      return "success";
  }
  ```

  ```jsp
  <h3>跨服务器方式文件上传</h3>
  <br/>
  
  <form action="file/fileupload3" method="post" enctype="multipart/form-data">
      请选择上传的文件：<input type="file" name="upload"/><br/>
      <input type="submit" value="提交"/>
  </form>
  <hr>
  ```



## 三、SpringMVC的异常处理

### 3.1、异常处理思路 

​    Controller调用service,service调用dao,异常都是向上抛出的,最终有DispatcherServlet找异常处理器进 行异常的处理。 

### 3.2、SpringMVC的异常处理

- #### 第一步：自定义异常类

  ```java
  public class SysException extends Exception {
      // 异常提示信息
      private String message;
  
      public String getMessage() {
          return message;
      }
      public void setMessage(String message) {
          this.message = message;
      }
  
      public SysException(String message) {
          this.message = message;
      }
  }
  ```

- #### 第二步：自定义异常处理器

  ```java
  public class SysExceptionResolver implements HandlerExceptionResolver {
  
      /**
       *  跳转到具体的错误页面的方法
       */
      @Override
      public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
          SysException ex;
          if(e instanceof SysException){
              ex = (SysException) e;
          }else{
              ex = new SysException("系统正则维护.....");
          }
  
          ModelAndView mv = new ModelAndView();
          mv.addObject("errorMsg",ex.getMessage());
          mv.setViewName("error");
          return mv;
      }
  }
  ```

- #### 第三步：配置异常处理器

  ```xml
   <!-- 配置异常处理器 -->
  <bean id="sysExceptionResolver" class="cn.itcast.exception.SysExceptionResolver"/>
  ```



## 四、SpringMVC的拦截器

### 4.1、拦截器的作用

##### Spring MVC 的处理器拦截器类似于 Servlet 开发中的过滤器 Filter,用于对处理器进行预处理和后处理。

##### 拦截器 vs Filter 区别：

1. 过滤器是servlet规范中的一部分,任何java web工程都可以使用。
2. 拦截器是 SpringMVC 框架自己的,只有使用了 SpringMVC 框架的工程才能用。
3. 过滤器在 url-pattern 中配置了/*之后,可以对所有要访问的资源拦截。
4. 拦截器它是只会拦截访问的控制器方法,如果访问的是 jsp,html,css,image 或者 js 是不会进行拦截的。
5. 拦截器是也是 AOP 思想的具体应用。



### 4.2、自定义拦截器的步骤

- #### 第一步:编写一个普通类实现 HandlerInterceptor 接口

  ```java
  public class MyInterceptor implements HandlerInterceptor {
  
      /**
       * controller方法执行前,进行拦截的方法
       * return true放行
       * return false拦截
       * 可以使用转发或者重定向直接跳转到指定的页面。
       */
      public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
          return false;
      }
  
      /**
       * 后处理方法，controller方法执行后，success.jsp执行之前
       */
      public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
  
      }
  
      /**
       * success.jsp页面执行后，该方法会执行
       *  作用：执行后一些资源释放
       */
      public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
  
      }
  }
  ```

- #### 第二步:配置拦截器

  ```xml
  <!--配置拦截过滤器-->
  <mvc:interceptors>
      <mvc:interceptor>
          <!-- 过滤路径：即目标controller对应的方法-->
          <mvc:mapping path="/user/*"/>
          <!--不过滤路径：两者配置一个即可-->
          <!--<mvc:exclude-mapping path=""/>-->
          <bean class="com.eoony.interceptor.MyInterceptor1"/>
      </mvc:interceptor>
      <mvc:interceptor>
          <!-- 过滤路径：即目标controller对应的方法-->
          <mvc:mapping path="/*"/>
          <!--不过滤路径：两者配置一个即可-->
          <!--<mvc:exclude-mapping path=""/>-->
          <bean class="com.eoony.interceptor.MyInterceptor2"/>
      </mvc:interceptor>
  </mvc:interceptors>
  ```






















