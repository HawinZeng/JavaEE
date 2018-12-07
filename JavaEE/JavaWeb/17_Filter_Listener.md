# 第十七节 Filter 、Listener

##### web 三大组件：Servlet、Filter、Listener

## 一、Filter：过滤器

### 1.1、概念：

**web中的过滤器：**当访问服务器的资源时，过滤器可以将请求拦截下来，完成一些特殊的功能。

过滤器的作用：

- **一般用于完成通用的操作。如：**

  登录验证：即登录之后才能访问资源；

  统一编码处理：如获取数据时，都需设置编码；

  敏感字符过滤：过滤些骂人词汇等；



### 1.2、快速入门

- #### 步骤

  1. 定义一个类，实现接口Filter
  2. 复写方法
  3.  配置拦截路径 (与Servlet配置一样)
     -  web.xml
     - 注解

  ```java
  @WebFilter("/*") // 注解配置 ‘/*’: 代表过滤拦截所有资源访问
  public class FilterDemo1 implements Filter {
      public void destroy() {
      }
  
      public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
          System.out.println("filterDemo 执行了");
          // 放行代码,若没有此行，过滤器就拦截的所有访问
          chain.doFilter(req, resp);
      }
  
      public void init(FilterConfig config) throws ServletException {
  
      }
  }
  ```

- #### 过滤细节

  1. ##### web.xml配置	

     ```xml
     	<filter>
             <filter-name>demo1</filter-name>
             <filter-class>cn.itcast.web.filter.FilterDemo1</filter-class>
         </filter>
         <filter-mapping>
             <filter-name>demo1</filter-name>
     		<!-- 拦截路径 -->
             <url-pattern>/*</url-pattern>
         </filter-mapping>
     ```

  2. ##### 过滤器执行流程

     1. 执行过滤器
     2. 执行放行后的资源
     3. 回来执行过滤器放行代码下边的代码

     ```java
     @WebFilter("/*")
     public class FilterDemo2 implements Filter {
         
         public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
             //对request对象请求消息增强
             System.out.println("filterDemo2执行了...."); 
             // 请求访问不会执行chain.doFilter下面的
             
             //放行
             chain.doFilter(req, resp);
             
             // 回来只走chain.doFilter下面的
             //对response对象的响应消息增强
             System.out.println("filterDemo2回来了...");
         }
         ....
     }
     ```

  3. ##### 过滤器生命周期方法

     1. init:在服务器启动后，会创建Filter对象，然后调用init方法。**［只执行一次］**。**用于加载资源；**
     2. doFilter:每一次请求被拦截资源时，会执行。**［执行多次］。拦截逻辑在此实现；**
     3. destroy:在服务器关闭后，Filter对象被销毁。如果服务器是正常关闭，则会执行destroy方法。**［只执行一次］。用于释放资源；**

  4. ##### 过滤器配置详解

     #### - 拦截路径配置：

     1. 具体资源路径： /index.jsp   只有访问index.jsp资源时，过滤器才会被执行;
     2. 拦截目录： /user/*	        访问/user下的所有资源时，过滤器都会被执行;
     3. 后缀名拦截： *.jsp		访问所有后缀名为jsp资源时，过滤器都会被执行;
     4. 拦截所有资源：/*		         访问所有资源时，过滤器都会被执行

     #### - 拦截方式配置：资源被访问的方式

     ##### 注解配置：设置dispatcherTypes属性

     1. REQUEST：默认值。浏览器直接请求资源；
     2. FORWARD：转发访问资源；
     3. INCLUDE：包含访问资源；
     4. ERROR：错误跳转资源；
     5. ASYNC：异步访问资源；

     ```java
     @WebFilter(value = "/index.jsp",dispatcherTypes = {DispatcherType.FORWARD,DispatcherType.REQUEST})
     public class FilterDemo2 implements Filter {
         public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
             // 对request对象请求消息增强
             System.out.println("filterDemo2执行了....");
        }
        ...     
     }
     ```

     ##### web.xml配置：

     - 设置`<dispatcher></dispatcher>`标签即可

  5. ##### 过滤器链(配置多个过滤器)

     #### - 执行顺序：如果有两个过滤器：过滤器1和过滤器2

     1. 过滤器1
     2. 过滤器2
     3. 资源执行
     4. 过滤器2
     5. 过滤器1 

     #### - 过滤器先后顺序问题：

     1. 注解配置：按照类名的字符串比较规则比较，值小的先执行;

         如： AFilter 和 BFilter，AFilter就先执行了。

     2. web.xml配置： `<filter-mapping>`谁定义在上边，谁先执行



### 1.3、案例：

### 1.3.1、案例1——登录验证





- #### 请特别注意：

  1. 简单关闭浏览器，并不一定代表一次会话结束。如在Mac系统下，需要右击浏览器图标，点击退出才是一次会话结束！！





### 1.3.2、代理模式 －－（设计模式）

1. #### 作用： 增强对象的功能； （装饰模式也可以，但是没有代理模式灵活！）

2. #### 概念：

   - ##### 真实对象：被代理的对象；

   - ##### 代理对象：代表了真实对象；

   - ##### 代理模式：代理对象代理真实对象，达到增强真实对象功能的目的；

3. #### 实现方式：

   - ##### 静态代理：有一个类文件描述代理模式；

   - ##### 动态代理：在内存中形成代理类；（比静态代理更广泛应用）

     ##### 实现步骤：

     1. 代理对象和真实对象实现相同的接口；
     2. 代理对象 = Proxy.newProxyInstance();
     3. 使用代理对象调用方法；
     4. 增强方法；

     ##### 增强方式：

     1. 增强参数列表；
     2. 增强返回值类型；
     3. 增强方法体执行逻辑；



### 1.3.3、案例2——敏感词汇过滤





























