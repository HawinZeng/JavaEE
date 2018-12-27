# 第十二节 Tomcat & Servlet

## 一、web相关概念回顾

1. #### 软件架构

   C/S：客户端/服务器端

   B/S：浏览器/服务器端

2. #### 资源分类

   - 静态资源：所有用户访问后，得到的结果都是一样的，称为静态资源.静态资源可以直接被浏览器解析; 如：html、css、javascript
   - 动态资源:每个用户访问相同资源后，得到的结果可能不一样。称为动态资源。动态资源被访问后，需要先转换为静态资源，在返回给浏览器; 如： servlet／jsp、php、asp

3. #### 网络通信三要素

   - IP：电子设备(计算机)在网络中的唯一标识。

   -  端口：应用程序在计算机中的唯一标识。 0~65536

   - 传输协议：规定了数据传输的规则;

     基础协议：

     - TCP协议：安全协议，三次握手， 速度稍慢；
     - udp：不安全协议，速度快；



## 二、web服务器软件：

### 2.1、概述：

**服务器：**安装了服务器软件的计算机，非常高的配置电脑；

**服务器软件：**接收用户的请求，处理请求，做出响应；

**web服务器软件：**接收用户的请求，处理请求，做出响应。

- 在web服务器软件中，可以部署web项目，让用户通过浏览器来访问这些项目;
- 动态资源不能直接被浏览器访问，只能存在web服务器软件中，即web容器；



### 2.2、常见的java相关的web服务器软件：

- **webLogic：**oracle公司，大型的JavaEE服务器，支持所有的JavaEE规范，收费的。
- **webSphere：**IBM公司，大型的JavaEE服务器，支持所有的JavaEE规范，收费的。
-  **JBOSS：**JBOSS公司的，大型的JavaEE服务器，支持所有的JavaEE规范，收费的。
- **Tomcat：**Apache基金组织，中小型的JavaEE服务器，仅仅支持少量的JavaEE规范servlet/jsp。开源的，免费的。

> JavaEE：Java语言在企业级开发中使用的技术规范的总和，一共规定了13项大的规范;

#### 额外点：[Apache服务器 vs Tomcat服务器](md/Apache服务器_vs_Tomcat服务器.md)



### 2.3、 Tomcat：web服务器软件

1. #### 下载：http://tomcat.apache.org/

2. #### 安装：解压压缩包即可。

  * 注意：安装目录建议不要有中文和空格

3. #### 卸载：删除目录就行了

4. #### 启动：

   - bin/startup.bat，双击运行该文件即可（windows）；
   - Mac OS：bin/startup.sh.  在终端输入命令：sudo sh startup.sh。这时需要管理员权限，输入密码即可启动；

5. #### 访问：http://localhost:8080 回车访问自己；http://xxx ip:8080 访问别人；

6. #### 可能遇到的问题：

   - 黑窗口一闪而过：

     -->原因： 没有正确配置JAVA_HOME环境变量;

     -->解决方案：正确配置JAVA_HOME环境变量;

   - 启动报错：

     -->暴力：找到占用的端口号，并且找到对应的进程，杀死该进程

     ```java
     windows查看所有程序端口 DOS命令：netstat -ano
     ```

     -->温柔：修改自身的端口号

     ```xml
      conf/server.xml下面配置的port即可
      <Connector port="8888" protocol="HTTP/1.1" connectionTimeout="20000" redirectPort="8445" />
     ```

     > **一般会将tomcat的默认端口号修改为80。80端口号是http协议的默认端口号。**
     >
     > * **好处：在访问时，就不用输入端口号**

7. #### 关闭：

   - 正常关闭：

   ```
   windows
   1.双击:bin/shutdown.bat
   2.快捷：ctrl＋c
   ```

   -  强制关闭：点击启动窗口的×。不推荐，因为可能造成文件不保存、或者资源不会被释放！

8. #### 配置：

- #### 部署项目的方式：

  - **直接将项目放到webapps目录下即可。**

    1) /hello：项目的访问路径-->虚拟目录

    2) 简化部署：将项目打成一个war包，再将war包放置到webapps目录下。

    > **缺点：每次都要把文件拷贝到webapps下，比较麻烦！**

  - **配置conf/server.xml文件**

    在**`<Host>`**标签体中配置: `<Context docBase="D:\hello" path="/hehe" />`

    - docBase：项目存放的路径；
    - path：虚拟目录；

    > **缺点：修改了tomcat核心配置文件，很不安全，也不推荐此法部署！**

  - **在conf\Catalina\localhost创建任意名称的xml文件。**

    在文件中编写：**`<Context docBase="D:\hello" />`**

    - 虚拟目录：xml文件的名称 (去除了path)

    > **推荐部署方式！热部署方式：直接修改也能生效。而前两种，需要重启服务器！！**

- #### 静态项目和动态项目：

  静态项目：只存在html，css、javascript、音频视频文件；

  动态项目：java动态项目的目录结构

  ```
  -- 项目的根目录
  	-- WEB-INF目录：
  		-- web.xml：web项目的核心配置文件
  		-- classes目录：放置字节码文件的目录
  		-- lib目录：放置依赖的jar包
  	-- 静态资源
  ```



### 2.4、Tomcat集成到IDE工具中 －－IntelliJ

##### 步骤：

1. 在导航栏，Run菜单下，进入Edit Configurations；

2. 默认是没有Tomcat Server项，在Templates找到Tomcat Sever；若是本地选local，否则选Remote；

3. Application server 右侧的Configure按钮，出现弹窗，选择Tomcat路径即可;

4. 配置后，再次点击Edit Configurations，会发现左侧有一个Tomcat Server选项，子目录就是刚配置好的Tomcat Server实例。可以修改命名。同时，配置下面两项：

   ```java
   // 避免修改项目内容，需要重启服务器才能生效。这也是一种热部署方式
   On 'Update' action: Update resouces
   On frame deactivation: Update resouces 
   ```

5. 查看Edit Configurations 的Deployment: `Application context: /` 代表虚拟目录，默认打开项目下的index.jsp文件；后续会以项目名称作为虚拟目录！如 `/day13-tomcat`

   访问路径：`http://localhost:8080/day13-tomcat/index.jsp`


## 三、Servlet入门 

### 3.1、概念：运行在服务器端的小程序 （server applet）

- Servlet就是一个接口，定义了Java类被浏览器访问到(tomcat识别)的规则。
- 将来我们自定义一个类，实现Servlet接口，就可以被Tomcat识别，复写方法操作相应的动态资源。



### 3.2、快速入门：

#### 步骤

1. 创建JavaEE项目，而非前面的java项目；

2. 定义一个类，实现Servlet接口;

   ```java
   public class MyServlet implements Servlet{ ... }
   ```

3. 实现接口中的抽象方法;

4.  配置Servlet :  在web.xml中配置如下

   ```xml
       <!-- 配置servlet -->
       <servlet>
           <servlet-name>demo1</servlet-name>
           <servlet-class>com.lolo.servlet.MyServlet</servlet-class>
       </servlet>
   
       <servlet-mapping>
           <servlet-name>demo1</servlet-name>
           <url-pattern>/demo1</url-pattern>
       </servlet-mapping>
   ```



### 3.3、执行原理：步骤如下

1. 当服务器接受到客户端浏览器的请求后，会解析请求URL路径，获取访问的Servlet的资源路径
2. 查找web.xml文件，是否有对应的`<url-pattern>`标签体内容。
3. 如果有，则在找到对应的`<servlet-class>`全类名
4. tomcat会将字节码文件加载进内存，并且创建其对象
5. tomcat会调用servlet中的相应方法;



### 3.4、Servlet中的生命周期方法：

1. **被创建：执行init方法，只执行一次；**

   **Servlet具体什么时候被创建？？**

   - 默认情况下，第一次被访问时，Servlet被创建；
   - 可以配置执行Servlet的创建时机；

   ```xml
   在<servlet>标签下配置
   1. 第一次被访问时，创建
     	<load-on-startup>的值为负数
   2. 在服务器启动时，创建
   	<load-on-startup>的值为0或正整数
   ```

   **Servlet的init方法，只执行一次，说明一个Servlet在内存中只存在一个对象，Servlet是单例的!**

   - 多个用户同时访问时，可能存在线程安全问题；

   - 解决：尽量不要在Servlet中定义成员变量。即使定义了成员变量，也不要对修改值；

2. **提供服务：执行service方法，执行多次；**

   - 每次访问Servlet时，Service方法都会被调用一次；

3. **被销毁：执行destroy方法，只执行一次；**

   - Servlet被销毁时执行。服务器关闭时，Servlet被销毁；

   - 只有服务器正常关闭时，才会执行destroy方法；
   - destroy方法在Servlet被销毁之前执行，一般用于释放资源；



### 3.5、Servlet3.0：

#### 好处：

支持注解配置。可以不需要web.xml了。

#### 步骤：

1. 创建JavaEE项目，选择Servlet的版本3.0以上，可以不创建web.xml；
2. 定义一个类，实现Servlet接口；
3. 复写方法
4. 在类上使用@WebServlet注解，进行配置：**@WebServlet("资源路径")**

```java
public @interface WebServlet {
    String name() default "";

    String[] value() default {}; // value 是最重要的那个属性，即urlPatterns

    String[] urlPatterns() default {};

    int loadOnStartup() default -1;

    WebInitParam[] initParams() default {};

    boolean asyncSupported() default false;

    String smallIcon() default "";

    String largeIcon() default "";

    String description() default "";

    String displayName() default "";
}

// @WebServlet(urlPatterns = "/demo")  简化如下
// @WebServlet(value="/demo") // 只有一个值时，可以省略
@WebServlet("/demo")
public class MyServlet implements Servlet {
    ...
}
```



## 四、IDEA与tomcat的相关配置

1. **IDEA会为每一个tomcat部署的项目单独建立一份配置文件**

* 查看控制台的log：Using CATALINA_BASE:   "C:\Users\fqy\.IntelliJIdea2018.1\system\tomcat\_itcast"

> Mac OS下查看 -->  **CATALINA_BASE: /Users/iMac/Library/Caches/IntelliJIdea2018.2/tomcat**



2. **工作空间项目    和     tomcat部署的web项目 是两个不同位置**
* tomcat真正访问的是“tomcat部署的web项目”，"tomcat部署的web项目"对应着"工作空间项目" 的web目录下的所有资源

* WEB-INF目录下的资源不能被浏览器直接访问。


3. **断点调试：使用"小虫子"启动 dubug 启动**

















