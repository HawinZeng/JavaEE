# 第十三节  Servlet、Http、Request

## 一、Servlet深入

#### 1.1、Servlet的体系结构

```
	Servlet -- 接口
		|
	GenericServlet -- 抽象类
		|
	HttpServlet  -- 抽象类
	
	* GenericServlet：将Servlet接口中其他的方法做了默认空实现，只将service()方法作为抽象
		* 将来定义Servlet类时，可以继承GenericServlet，实现service()方法即可;
		
	* HttpServlet：对http协议的一种封装，简化操作 (实际开发使用这个！！！！)
		1. 定义类继承HttpServlet
		2. 复写doGet/doPost方法		
```

#### 1.2、Servlet相关配置

 urlpartten:Servlet访问路径：

- 一个Servlet可以定义多个访问路径 ： @WebServlet({"/d4","/dd4","/ddd4"})；

  ```java
  @WebServlet({"/d4","/dd4","/d5"})
  public class ServletDemo4 extends HttpServlet {
  	....
  }
  ```

- 路径定义规则：

  - /xxx：路径匹配；
  - /xxx/xxx:多层路径，目录结构
  - *.do：扩展名匹配； **注意前面不能加／；**

  ```java
  @WebServlet("/user/demo4")
  @WebServlet("/user/*") // user下任意目录都可以访问
  @WebServlet("/*") // 任意目录都可以访问，优先级最低
  public class ServletDemo4 extends HttpServlet { ... }
  
  
  @WebServlet("*.do")
  public class ServletDemo4 extends HttpServlet { ... }
  ```



## 二、HTTP：

### 2.1、概述

HTTP：Hyper Text Transfer Protocol 超文本传输协议；

- **传输协议：**定义了，客户端和服务器端通信时，发送数据的格式；
- **特点：**
  1. 基于TCP/IP的高级协议；
  2. 默认端口号:80；
  3. 基于请求/响应模型的:一次请求对应一次响应；
  4. 无状态的：每次请求之间相互独立，不能交互数据；
- 历史版本：
  - version 1.0：每一次请求响应都会建立新的连接；
  - version 1.1：复用连接；相当于只建立一次连接，提升了效率！

### 2.2、请求消息数据格式：四部分

1. #### 请求行

   ```java
   // 请求方式 请求url 请求协议/版本
   GET   /login.html	HTTP/1.1
   POST  /login.html	HTTP/1.1
   ```

   HTTP协议有7中请求方式，常用的有2种：GET ／ POST

   - GET:
     - 请求参数在请求行中，在url后。
     - 请求的url长度有限制的;
     - 不太安全;
   - POST:
     - 请求参数在请求体中;
     - 请求的url长度没有限制的;
     - 相对安全;

2. #### 请求头：客户端浏览器告诉服务器一些信息

   请求头信息格式 **［请求头名称: 请求头值］**

   常见的请求头信息：

   - **User-Agent：**浏览器告诉服务器，我访问你使用的浏览器版本信息

     - 可以在服务器端获取该头的信息，解决浏览器的兼容性问题；

   - **Referer：`http://localhost/login.html`**

     - 告诉服务器，我(当前请求)从哪里来？

     - 作用：
       1. 防盗链；
       2. 统计工作；

3. #### 请求空行

   空行，就是用于分割POST请求的请求头，和请求体的。

4. #### 请求体(正文)：

   Form Data: username=lolo

   封装POST请求消息的请求参数的。GET方式是没有请求体的！

```properties
Accept:text/html,application/xhtml+xm…plication/xml;q=0.9,*/*;q=0.8
Accept-Encoding:gzip, deflate
Accept-Language:zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2
Connection:keep-alive
Host:localhost:8080
Referer:http://localhost:8080/user_login/login.html
Upgrade-Insecure-Requests:1
User-Agent:Mozilla/5.0 (Macintosh; Intel …) Gecko/20100101 Firefox/63.0
```

![](attach/F0_requestinfo.png)

> **响应消息数据格式**：后续在详解！



## 三、Request：

### 3.1、request对象和response对象的原理

- request和response对象是由服务器创建的。我们来使用它们；
- request对象是来获取请求消息，response对象是来设置响应消息；



### 3.2、request对象继承体系结构：	

```java
	ServletRequest		--	接口
		|	继承
	HttpServletRequest	-- 接口
		|	实现
	org.apache.catalina.connector.RequestFacade 类(tomcat) 
```



### 3.3、request功能：

- #### 获取请求消息数据

  1. #####  获取请求行数据 [ GET 	/day14/demo1?name=zhangsan&age=12 	HTTP/1.1 ]

     - 获取请求方式 ：GET   --> `String getMethod()`

     -  **(*)获取虚拟目录：/day14**  --> `String getContextPath()`

     - 获取Servlet路径: /demo1 --> `String getServletPath()`

     - 获取get方式请求参数：name=zhangsan&age=12 --> `String getQueryString()`

     - **(*)获取请求URI：/day14/demo1**

       ```java
       String getRequestURI():		/day14/demo1
       StringBuffer getRequestURL()  :http://localhost/day14/demo1
       
       URL:统一资源定位符 ： http://localhost/day14/demo1	相当于：中华人民共和国
       URI：统一资源标识符 : /day14/demo1					相当于：共和国
       
       结论：URI代表范围更大。 
       ```

     - 获取协议及版本：HTTP/1.1 --> `String getProtocol()`

     - 获取客户机的IP地址： --> `String getRemoteAddr()`

  2. ##### 获取请求头数据

     **(*)`String getHeader(String name):`**  通过请求头的名称获取请求头的值

     `Enumeration<String> getHeaderNames(): ` 获取所有的请求头名称

     ```java
     /**
     Host: localhost:8080
     Connection: keep-alive
     Content-Type: application/x-www-form-urlencoded
     User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36
     Accept: text/html,application/xhtml+xml,...
     Referer: http://localhost:8080/login.html
     */
       
     // Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36
     String agent = req.getHeader("User-Agent"); // 参数不区分大小写
     if(agent.contains("Chrome")){
         System.out.println("Chrome go go !!!");
     }else if(agent.contains("FireFox")){
          System.out.println("FireFox go go !!!");
     }else{
          System.out.println("default go go!!!");
     }
     // http://localhost:8080/login.html
     String referer = req.getHeader("referer");
          
     ```

  3. ##### 获取请求体数据

     **请求体：**只有POST请求方式，才有请求体，在请求体中封装了POST请求的请求参数 ;

     **步骤：**

     - 获取流对象：

     ```java
     BufferedReader getReader(); // 获取字符输入流，只能操作字符数据
     ServletInputStream getInputStream(); // 获取字节输入流，可以操作所有类型数据.
     ```

     - 再从流对象中拿数据：

     ```java
     protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
             System.out.println("doPost 把数据给Post了 ......");
             BufferedReader reader = req.getReader();
             String line;
             while((line = reader.readLine())!=null){
                 System.out.println(line);
             }
         }
     ---------------
     doPost 把数据给Post了 ......
     username=zhangsan&password=123 //  与GET格式一致，只是位置不同！
     ```

- #### 其他功能：（重点）

  1. ##### 获取请求参数通用方式：不论get还是post请求方式都可以使用下列方法来获取请求参数；

     ```java
     // username=zs&password=123
     1. String getParameter(String name):根据参数名称获取参数值  (常用)
     
     // hobby=xx&hobby=game 复选框
     2. String[] getParameterValues(String name):根据参数名称获取参数值的数组  
     
     3. Enumeration<String> getParameterNames():获取所有请求的参数名称
     
     4. Map<String,String[]> getParameterMap():获取所有参数的map集合  (常用)
     ```

     ##### 中文乱码问题：

     ```java
     1. get方式：tomcat 8 及以上版本 已经将get方式乱码问题解决了;
     2. post方式：会乱码；
     解决：在获取参数前，设置request的编码request.setCharacterEncoding("utf-8"); // 编码依据页面来定；
     ```

  2. ##### 请求转发：一种在服务器内部的资源跳转方式；

     为什么需要请求转发？项目工程比较大时， 不可能一个servlet包涵所有的功能，需要分工协作，这时一个请求可能有多个servlet依次相应提供数据，这就存在请求转发！！！！

     ```java
     // 1. 方式：
     req.getRequestDispatcher("/ServletDemo5").forward(req,resp);
     1. 通过request对象获取请求转发器对象：RequestDispatcher getRequestDispatcher(String path)；
     2. 使用RequestDispatcher对象来进行转发：forward(ServletRequest request, ServletResponse response)
     
     // 2. 特点：
     1. 浏览器地址栏路径不发生变化；
     2. 只能转发到当前服务器内部资源中；
     3. 转发是一次请求
     ```

  3. ##### 共享数据：

     **域对象：**一个有作用范围的对象，可以在范围内共享数据；

     **request域：**代表一次请求的范围，一般用于请求转发的多个资源中共享数据；

     ```java
     // 方法
     1. void setAttribute(String name,Object obj):存储数据
     2. Object getAttitude(String name):通过键获取值
     3. void removeAttribute(String name):通过键移除键值对
     ```

  4. ##### 获取ServletContext：

     ```java
     ServletContext getServletContext();
     ```



## 四、案例：用户登录

```properties
# src 下druid.proerties 
driverClassName=com.mysql.jdbc.Driver
url=jdbc:mysql:///day14
username=root
password=root
initialSize=5
maxActive=10
maxWait=3000
```

```java
public class JDBCUtils {

    private static DataSource ds;

    static {
        try{
            // 1.加载配置文件
            Properties pro = new Properties();
            String path = JDBCUtils.class.getClassLoader().getResource("druid.properties").getPath();
            pro.load(new FileInputStream(path));

            ds = DruidDataSourceFactory.createDataSource(pro);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static DataSource getDataSource() {
        return ds;
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
```

```java
// 创建类UserDao,提供login方法
/**
 *   操作数据库中User表的类
 */
public class UserDao {
	// 使用Spring中JdbcTemplate操控sql，必须引入spring相关jar
    private JdbcTemplate template  = new JdbcTemplate(JDBCUtils.getDataSource());

    /**
     *  登录方法
     *
     * @param loginUser 只有username ，password
     * @return User 包含数据库中所有信息
     */
    public User login(User loginUser){
        /*
        不处理，会如此报错，不友好！
        org.springframework.dao.EmptyResultDataAccessException: Incorrect result size: expected 1, actual 0
         */
        try{
            String sql = "select * from user where name=? and password=?";
            User user = template.queryForObject(sql, new BeanPropertyRowMapper<>(User.class),loginUser.getName(), loginUser.getPassword());
            return user;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}

public class User {
    private int id;
    private String name;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
```

```java
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loginUser = new User();
//        loginUser.setName(request.getParameter("username"));
//        loginUser.setPassword(request.getParameter("password"));
        try {
            BeanUtils.populate(loginUser,request.getParameterMap());
            UserDao dao = new UserDao();
            User user  = dao.login(loginUser);
            if(user == null){
                request.getRequestDispatcher("/failServlet").forward(request,response);
            }else{
                request.setAttribute("user",user);
     		request.getRequestDispatcher("/successServlet").forward(request,response);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
```



## 五、BeanUtils工具类，简化数据封装

##### `org.apache.commons.beanutils.BeanUtils`：用于封装JavaBean的！

#### 5.1、JavaBean：标准的Java类

- 要求：
  1. 类必须被public修饰；
  2. 必须提供空参的构造器；
  3. 成员变量必须使用private修饰；
  4. 提供公共setter和getter方法；
- 功能：封装数据

> ```java
> User user = template.queryForObject(sql, new BeanPropertyRowMapper<>(User.class),loginUser.getName(), loginUser.getPassword());
> 
> // 也请特别注意：BeanPropertyRowMapper封装数据，JavaBean的成员变量一定要与数据库的字段一一对应，否则取出来的数据，某些属性就是null；
> ```



#### 5.2、成员变量 & 属性：

成员变量：类中、方法体外的变量，同时非静态变量；

属性：setter和getter方法截取后的产物；例如：getUsername() --> Username--> username；

大多情况成员变量与属性是一致的，但也有个别不同，参考下面：

```java
class User{
    private String gender; // 成员变量 gender,属性 kGender
    public String getKGender(){
        return gender;
    }
    public void setKGender(String gender){
        this.gender = gender;
    }
}
```



#### 5.3 、方法：

```java
1. setProperty()
2. getProperty()
// 重点，封装数据成JavaBean
3. populate(Object obj , Map map):将map集合的键值对信息，封装到对应的JavaBean对象中
```



