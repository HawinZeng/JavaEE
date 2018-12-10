# 第二十节 AJAX 、JSON

## 一、AJAX

### 1.1、概念：中文（ 阿贾克斯）

- **AJAX：**Asynchronous JavaScript And XML	异步的JavaScript 和 XML

- ##### 异步和同步：客户端和服务器端相互通信的基础上

  **同步：**客户端必须等待服务器端的响应。在等待的期间客户端不能做其他操作。

  **异步：**客户端不需要等待服务器端的响应。在服务器处理请求的过程中，客户端可以进行其他的操作。

- ##### Ajax 是一种在无需重新加载整个网页的情况下，能够更新部分网页的技术。 [1] 

  通过在后台与服务器进行少量数据交换，Ajax 可以使网页实现异步更新。这意味着可以在不重新加载整个网页的情况下，对网页的某部分进行更新。

  传统的网页（不使用 Ajax）如果需要更新内容，必须重载整个网页页面。

- ##### 作用：提升用户的体验



### 1.2、实现方式：

- #### 原生的JS实现方式（了解）

  ```javascript
  // 第1步 创建核心对象 xmlhttp
              var xmlhttp;
              if (window.XMLHttpRequest)
              {// code for IE7+, Firefox, Chrome, Opera, Safari
                  xmlhttp=new XMLHttpRequest();
              }
              else
              {// code for IE6, IE5
                  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
              }  
              
  // 第2步 建立连接  
  			/*
                  参数：
                      1. 请求方式：GET、POST
                          * get方式，请求参数在URL后边拼接。send方法为空参
                          * post方式，请求参数在send方法中定义
                      2. 请求的URL：
                      3. 同步或异步请求：true（异步）或 false（同步）
  
               */
  			xmlhttp.open("GET","ajaxServlet?username=tom",true);
  
  //3.发送请求
              xmlhttp.send();
  
  //4.接受并处理来自服务器的响应结果
              //获取方式 ：xmlhttp.responseText
              //什么时候获取？当服务器响应成功后再获取
  
              //当xmlhttp对象的就绪状态改变时，触发事件onreadystatechange。
              xmlhttp.onreadystatechange=function()
              {
                  //判断readyState就绪状态是否为4，判断status响应状态码是否为200
                  if (xmlhttp.readyState==4 && xmlhttp.status==200)
                  {
                     //获取服务器的响应结果
                      var responseText = xmlhttp.responseText;
                      alert(responseText);
                  }
              }
  ```

- ####  JQeury实现方式