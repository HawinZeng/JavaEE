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

  1. #### $.ajax()

     ```java
     语法：：$.ajax({键值对});
     		$.ajax({
     	         url:"ajaxServlet1111" , // 请求路径
     	         type:"POST" , //请求方式
     	         //data: "username=jack&age=23",//请求参数
     	         data:{"username":"jack","age":23},
     	         success:function (data) {
     	            alert(data);
     	         },//响应成功后的回调函数
     	         error:function () {
     	            alert("出错啦...")
     	         },//表示如果请求响应出现错误，会执行的回调函数
     	         dataType:"text"//设置接受到的响应数据的格式
     	     });
     
     // test.jsp -- javascript
     		function fun() {
                 $.ajax({
                     url:"ajaxServlet",
                     type:"POST",
                     data:{"username":"jack","age":23},
                     success:function (data) {
                         alert(data);
                     },
                     error:function (data) {
                         alert("出错啦。。。")
                     },
                     dataType:"text"
                 });
             }
     
        // AjaxServlet
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
             String username = req.getParameter("username");
             String age = req.getParameter("age");
             try {
                 Thread.sleep(5000);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
     
             int i = 3/0;
     
             resp.getWriter().write("Hello: "+username+",oh,you are "+(age+10));
         }
     
     ```

  2. #### $.get()：发送get请求

     ```javascript
     	语法：$.get(url, [data], [callback], [type])
     				* 参数：
     					* url：请求路径
     					* data：请求参数
     					* callback：回调函数
     					* type：响应结果的类型
     
     function fun() {
         $.get("ajaxServlet",{"username":"jack","age":23},function (data) {
               alert(data);
          },"text");
     }
     ```

  3. #### $.post()：发送post请求，(同2，将get该post即可) 



## 二、JSON

### 2.1、概念

**JSON：JavaScript Object Notation**	JavaScript对象表示法

```javascript
	Person p = new Person();
	p.setName("张三");
	p.setAge(23);
	p.setGender("男");
	
	var p = {"name":"张三","age":23,"gender":"男"};
```

#### 作用：

- ##### json现在多用于存储和交换文本信息的语法;

- ##### 进行数据的传输;

- ##### JSON 比 XML 更小、更快，更易解析。



### 2.2、语法：

1. #### 基本规则：数据在名称/值对中：json数据是由键值对构成的

   - ##### 键用引号(单双都行)引起来，也可以不使用引号;

   - ##### 值得取值类型：

     - 数字（整数或浮点数）
     -  字符串（在双引号中）
     - 逻辑值（true 或 false）
     - 数组（在方括号中）	{"persons":[{},{}]}
     - 对象（在花括号中） {"address":{"province"："陕西"....}}
     - null 不常用

   - ##### 数据由逗号分隔：多个键值对由逗号分隔

   - ##### 花括号保存对象：使用{}定义json 格式

   - ##### 方括号保存数组：[]

2. #### 获取数据: （4种方式）

   - json对象.键名;
   - json对象["键名"]
   - 数组对象[索引]
   - 遍历

   ```javascript
   // 1.定义基本格式
   var person = {"name": "张三", age: 23, 'gender': true};
   alert(person.name);
   alert(person["name"]);
   
   // 2.嵌套格式
   var persons = ｛
   	persons:[
           {"name": "张三", age: 23, 'gender': true},
           {"name": "李四", "age": 24, "gender": true},
   		{"name": "王五", "age": 25, "gender": false}
       ]
   };
   alert(persons.persons[2].name);
   
   // 2. 数组对象
   var ps = [{"name": "张三", "age": 23, "gender": true},
   	{"name": "李四", "age": 24, "gender": true},
   	{"name": "王五", "age": 25, "gender": false}];
   alert(ps[2].name);
   
   // 3. 遍历
   //for in 循环
   for(var key in person){
       alert(key+":"+person[key]);
       // 不能如下写法
       alert(key+":"+person.key); // 相当于 person."name",无法获取数据
   }
   
   // 数组遍历，双层循环
   for (var i = 0; i < ps.length; i++) {
   	var p = ps[i];
   	for(var key in p){
   		 alert(key+":"+p[key]);
   	}
   }
   ```

### 2.3、JSON数据和Java对象的相互转换

- ##### 常见的JSON解析器：Jsonlib，Gson(Google)，fastjson(alibaba)，jackson(spring框架内置解析器)

下面主要介绍jackson解析：

#### 1）JSON转为Java对象

```java
String json = "{\"name\":\"张三\",\"age\":34,\"gender\":\"男\",\"birthday\":\"2018-12-11\"}";
ObjectMapper mapper = new ObjectMapper();
Person person = mapper.readValue(json, Person.class);
System.out.println(person);
--------------------------------------------
Person{name='张三', age=34, gender='男', birthday=Tue Dec 11 08:00:00 CST 2018}
```

#### 2）Java对象转换JSON

使用步骤：

- 导入jackson的相关jar包

- 创建Jackson核心对象 ObjectMapper

- 调用ObjectMapper的相关方法进行转换

  1. 转换方法：

     - ##### writeValue(参数1，obj):

       ##### 参数1：

       File：将obj对象转换为JSON字符串，并保存到指定的文件中;

        Writer：将obj对象转换为JSON字符串，并将json数据填充到字符输出流中

        OutputStream：将obj对象转换为JSON字符串，并将json数据填充到字节输出流中

     - ##### writeValueAsString(obj):将对象转为json字符串

     ```java
         public void Test1() throws Exception {
             Person person = new Person();
             person.setAge(34);
             person.setGender("男");
             person.setName("张三");
     
             ObjectMapper mapper = new ObjectMapper();
             // {"name":"张三","age":34,"gender":"男"}
             String json = mapper.writeValueAsString(person);
     
             // writeValue -- 到文件
     //        mapper.writeValue(new File("a.txt"),person);
     
             // writeValue -- 到流
             mapper.writeValue(new FileWriter("b.txt"),person);
         }
     ```

  2. 注解：**(可在成员变量上加注解，也可以在get方法上加注解)**

     - ##### @JsonIgnore：排除属性。

     - ##### @JsonFormat：属性值得格式化

     ```javascript
     public class Person {
         private String name;
         private int age;
         private String gender;
     //    @JsonIgnore //  忽略该键值对
         @JsonFormat(pattern = "yyyy-MM-dd") // 按指定的格式转换
         private Date birthday;
     
        ...
     
         public Date getBirthday() {
             return birthday;
         }
     
         public void setBirthday(Date birthday) {
             this.birthday = birthday;
         }
     }
     ```

  3. 复杂java对象转换:

     - List：数组
     - Map：对象格式一致

     ```java
     List<Person> list = new ArrayList<>();
     list.add(person1);
     list.add(person2);
     String json = mapper.writeValueAsString(list);
     -----json-------
     [{"name":"张三","age":34,"gender":"男","birthday":"2018-12-11"},{"name":"张三","age":34,"gender":"男","birthday":"2018-12-11"}]
     
     Map<String,Object> map = new HashMap<String,Object>();
     map.put("name","张三");
     map.put("salary",10000);
     map.put("address","北京");
     
     ObjectMapper mapper = new ObjectMapper();
     String json = mapper.writeValueAsString(map);
     -----json-------
     {"address":"北京","name":"张三","salary":10000}
     ```



## 三、案例

#### 需求：校验用户名是否存在

```javascript
		$("#username").blur(function () {
                var username = $(this).val();
                // {"userExsit":true,"msg":"用户名太流行了，已被注册！"}
                $.get("registerServlet",{"username":username},function (data) {
                    var span = $("#s_username");
                    if(data.userExist){
                        span.css("color","red");
                        span.html(data.msg);
                    }else{
                        span.css("color","green");
                        span.html(data.msg);
                    }
                },"json");
            });

// 若前端没有指定$.get(dataType),可以在服务器设置MIME类型，jQuery会智能获取对应MIME类型为数据类型；
protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        String username = req.getParameter("username");

        if(null == username || "".equals(username)) return;

        UserService service = new UserServiceImpl();
        User user = service.findUserByName(username);

        Map<String,Object> map = new HashMap<>();

        // {"userExist":true,"msg":"用户名太流行了，已被注册！"}
        if(user != null){
            // 用户名可用
            map.put("userExist",true);
            map.put("msg","用户名太流行了，已被注册！");
        }else{
            // 用户名不可用
            map.put("userExist",false);
            map.put("msg","用户名可用！");
        }

//        resp.setContentType("text/html;charset=utf-8");
        resp.setContentType("application/json;charset=utf-8");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(resp.getWriter(),map);
    }

```











