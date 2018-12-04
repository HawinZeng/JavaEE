# 第十四节 Response、ServletContext

## 一、Http协议：响应消息

> **回顾：请求消息数据格式：**
>
> 1. 请求行 ; 
> 2. 请求头; 
> 3. 请求空行
> 4. 请求体

#### 响应消息：服务器端发送给客户端的数据

数据格式：

1. ##### 响应行：协议/版本 响应状态码 状态码描述  ` HTTP/1.1 200 `

   - **响应状态码：**服务器告诉客户端浏览器本次请求和响应的一个状态，状态码都是3位数字 ;

     分类：

     - 1xx：服务器就收客户端消息，但没有接受完成，等待一段时间后，发送1xx多状态码;

     - 2xx：成功。代表：200

     - 3xx：重定向。代表：302(重定向)，304(访问缓存);

     -  4xx：客户端错误。

       404（请求路径没有对应的资源） 

       405：请求方式没有对应的doXxx方法

     - 5xx：服务器端错误。代表：500(服务器内部出现异常)

     > 详情参考：百度百科！

2. ##### 响应头：格式 ( 头名称： 值 )

    常见的响应头：

   - **Content-Type：**服务器告诉客户端本次响应体数据格式以及编码格式;
   - **Content-disposition：**服务器告诉客户端以什么格式打开响应体数据;
     - in-line：默认值，在当前页面内打开;
     - attachment;filename=xxx：以附件形式打开响应体，文件下载;

3. ##### 响应空行

4. ##### 响应体:传输的数据

```properties
HTTP/1.1 200 OK
Set-Cookie: JSESSIONID=27389A114676385D191501CE65EB262E; Path=/user_login; HttpOnly
Content-Type: text/html;charset=UTF-8
Content-Length: 102
Date: Mon, 03 Dec 2018 07:33:23 GMT

<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>
    Hello , jsp!!!!
  </body>
</html>
```



## 二、Response对象

### 2.1、功能：设置响应消息

- #### 设置响应行：

  ```java
  设置状态码：setStatus(int sc) 
  ```

- #### 设置响应头：

  ```java
  setHeader(String name, String value) 
  ```

- #### 设置响应体：

  ```java
  // 使用步骤：
  1. 获取输出流
  	字符输出流：PrintWriter getWriter()
  	字节输出流：ServletOutputStream getOutputStream()
  
  2. 使用输出流，将数据输出到客户端浏览器
  ```



### 2.2、案例：

#### 2.2.1. 完成重定向：资源跳转的方式

- ##### 代码实现：

  ```java
  //1. 设置状态码为302
  response.setStatus(302);
  //2.设置响应头location
  response.setHeader("location","/day15/responseDemo2");
  
  //简单的重定向方法
  response.sendRedirect("/day15/responseDemo2");
  ```

- ##### 重定向的特点: redirect

  1. 地址栏发生变化;
  2. 重定向可以访问其他站点(服务器)的资源;
  3. 重定向是两次请求。不能使用request对象来共享数据;

- ##### 转发的特点：forward

  1. 转发地址栏路径不变;
  2. 转发只能访问当前服务器下的资源;
  3. 转发是一次请求，可以使用request对象来共享数据;

- ##### 路径写法：

  路径分类:

  1. **相对路径：通过相对路径不可以确定唯一资源;**

     ```properties
     1.1 格式: 不以[ ／ ] 开头，以[ . ] 开头路径
     如: ./index.html
     
     1.2 规则: 找到当前资源和目标资源之间的相对位置关系
     [./ ]: 代表当前目录。(可以省略)
     	<a href='./responseDemo2'>responseDemo2</a>
     	<a href='responseDemo2'>responseDemo2</a>
     	
     [../ ]: 代表后退一级目录
     ```

     > 一般不推荐使用相对路径！

  2. **绝对路径：通过绝对路径可以确定唯一资源;**

     ```properties
     2.1 格式: 以 ／ 开头
     如：http: //localhost/day15/responseDemo2		/day15/responseDemo2(简化)
     
     2.2 规则: 判断定义的路径是给谁用的？判断请求将来从哪儿发出
     * 给客户端浏览器使用: 需要加虚拟目录(项目的访问路径)
     	-> 建议虚拟目录动态获取: request.getContextPath(),这样能增强代码的维护性，即修改虚拟目录不影响功能项目；
     	-> <a> , <form> 重定向...: 需要虚拟目录
     
     * 给服务器使用: 不需要加虚拟目录
     	-> 转发路径,无需虚拟目录
     ```



#### 2.2.2、服务器输出字符数据到浏览器

```java
//获取流对象之前，设置流的默认编码：ISO-8859-1 设置为：GBK
response.setCharacterEncoding("utf-8");

//告诉浏览器，服务器发送的消息体数据的编码。建议浏览器使用该编码解码
response.setHeader("content-type","text/html;charset=utf-8");

//简单的形式，设置编码 [推荐方式！！！！！]
response.setContentType("text/html;charset=utf-8");

//1.获取字符输出流
PrintWriter pw = response.getWriter();
//2.输出数据
//pw.write("<h1>hello response</h1>");
pw.write("你好啊啊啊 response");
```

> 乱码问题：
> 1. PrintWriter pw = response.getWriter();获取的流的默认编码是ISO-8859-1
> 2. 设置该流的默认编码
> 3. 告诉浏览器响应体使用的编码



#### 2.2.3、服务器输出字节数据到浏览器

```java
response.setContentType("text/html;charset=utf-8");
//1.获取字节输出流
ServletOutputStream sos = response.getOutputStream();
//2.输出数据
sos.write("你好".getBytes("utf-8"));
```



#### 2.2.4、验证码  (重点)







## 三、ServletContext对象

- #### 概念：代表整个web应用，可以和程序的容器(服务器)来通信；

- #### 获取：

  1. 通过request对象获取  **`request.getServletContext();`**
  2. 通过HttpServlet获取 **`this.getServletContext();`**

- #### 功能：

  1. ##### 获取MIME类型：( MIME类型:在互联网通信过程中定义的一种文件数据类型 )

     格式： 大类型/小类型  		 text/html		image/jpeg

     获取：**`String getMimeType(String file) `**   －－> 根据文件名，获取对应的MIME类型；

     > **在tomcat的config web.xml中，可以查看所有的MIME类型，并对应的名称**；

  2. ##### 域对象：共享数据：

     setAttribute(String name,Object value)

     getAttribute(String name)

     removeAttribute(String name)

     **特别注意：**

     **1）ServletContext对象范围：所有用户所有请求的数据。**

     **2）而request只能在一次请求的范围**。

     > ServletContext对象由于为所有用户使用，要谨慎使用，会存在安全问题。同时，生命周期很长，容易出现内存泄漏或溢出的问题！

  3. ##### 获取文件的真实(服务器)路径：

     方法：**`String getRealPath(String path) `**

  ```java
  // 获取文件的服务器路径
  String b = context.getRealPath("/b.txt");//web目录下资源访问
  System.out.println(b);
  ---//result----
  /Volumes/D/ideaproject/servlet/out/artifacts/day15_response_war_exploded/b.txt
  
  String c = context.getRealPath("/WEB-INF/c.txt");//WEB-INF目录下的资源访问
  System.out.println(c);
  ---//result----
  /Volumes/D/ideaproject/servlet/out/artifacts/day15_response_war_exploded/WEB-INF/c.txt
  
  String a = context.getRealPath("/WEB-INF/classes/a.txt");//src目录下的资源访问
  System.out.println(a); 
  ---//result----
  /Volumes/D/ideaproject/servlet/out/artifacts/day15_response_war_exploded/WEB-INF/classes/a.txt
  ```



## 四、案例：文件下载

```properties
需求：
1. 页面显示超链接
2. 点击超链接后弹出下载提示框－－针对任何资源
3. 完成图片文件下载

分析：
1. 超链接指向的资源如果能够被浏览器解析，则在浏览器中展示，如果不能解析，则弹出下载提示框。不满足需求
2. 任何资源都必须弹出下载提示框；
3. 使用响应头设置资源的打开方式：
	content-disposition:attachment;filename=xxx
```

```java
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.获取请求参数，文件名称
        String filename = request.getParameter("filename");
        //2.使用字节输入流加载文件进内存
        //2.1找到文件服务器路径
        ServletContext servletContext = this.getServletContext();
        String realPath = servletContext.getRealPath("/img/" + filename);
        //2.2用字节流关联
        FileInputStream fis = new FileInputStream(realPath);

        //3.设置response的响应头
        //3.1设置响应头类型：content-type
        String mimeType = servletContext.getMimeType(filename);//获取文件的mime类型
        response.setHeader("content-type",mimeType);
        //3.2设置响应头打开方式:content-disposition

        //解决中文文件名问题
        //1.获取user-agent请求头、
        String agent = request.getHeader("user-agent");
        //2.使用工具类方法编码文件名即可
        filename = DownLoadUtils.getFileName(agent, filename);

        response.setHeader("content-disposition","attachment;filename="+filename);
        //4.将输入流的数据写出到输出流中
        ServletOutputStream sos = response.getOutputStream();
        byte[] buff = new byte[1024 * 8];
        int len = 0;
        while((len = fis.read(buff)) != -1){
            sos.write(buff,0,len);
        }
        fis.close();
    }
```

- ### 中文文件乱吗问题

解决思路：
1. 获取客户端使用的浏览器版本信息；
2. 根据不同的版本信息，设置filename的编码方式不同；

```java
public class DownLoadUtils {
    public static String getFileName(String agent, String filename) throws UnsupportedEncodingException {
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            BASE64Encoder base64Encoder = new BASE64Encoder();
            filename = "=?utf-8?B?" + base64Encoder.encode(filename.getBytes("utf-8")) + "?=";
        } else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }
}
```























