# 补充知识点：Rest

### 一、REST来源

REST：是一组架构约束条件和原则，REST是Roy Thomas Fielding在他2000年的博士论文中提出的。 

### 二、什么是REST

REST（Representational State Transfer）：表现层状态转移，一种软件架构风格，不是标准。既然不是标准，我可以遵守，也可以不遵守！！！ （表述性状态转移）

##### 什么是表现层状态转移：

 Representational （表现层） 
 State Transfer（状态转移）：通过HTTP动词实现。

> **总结：URL定位资源，HTTP动词（GET，POST，PUT，DELETE）描述操作。**



##### rest具体理解参照下图：

![](attach/restful.jpg)

##### 1、面向资源

Level0和Level1最大的区别，就是Level1拥有了Restful的第一个特征——**面向资源**，这对构建可伸缩、分布式的架构是至关重要的。同时，如果把Level0的数据格式换成Xml，那么其实就是**SOAP**，SOAP的特点是**关注行为和处理**，和面向资源的RESTful有很大的不同。

Level0和Level1，其实都很挫，他们都**只是把HTTP当做一个传输的通道**，没有把HTTP当做一种**传输协议**。

##### 2、配合http动词

Level2，真正将HTTP作为了一种传输协议，最直观的一点就是Level2使用了**HTTP动词**，GET/PUT/POST/DELETE/PATCH....,这些都是HTTP的规范，规范的作用自然是重大的，用户看到一个POST请求，就知道它不是**幂等**的，使用时要小心，看到PUT，就知道他是幂等的，调用多几次都不会造成问题，当然，这些的前提都是API的设计者和开发者也遵循这一套规范，确保自己提供的PUT接口是幂等的。

##### 3、HATEOAS （这个进度很难，一般满足前两者就很rest了）

Level3，关于这一层，有一个古怪的名词，叫**[HATEOAS]**（Hypertext As The Engine Of Application State），中文翻译为“将超媒体格式作为应用状态的引擎”，**核心思想就是每个资源都有它的状态，不同状态下，可对它进行的操作不一样。**

理解了这一层，再来看看REST的全称，Representational State Transfer，中文翻译为“表述性状态转移”，是不是好理解多了？

Level3的Restful API，给使用者带来了很大的便利，使用者**只需要知道如何获取资源的入口**，**之后的每个URI都可以通过请求获得，无法获得就说明无法执行那个请求**。

现在绝大多数的RESTful接口都做到了Level2的层次，做到Level3的比较少。当然，**这个模型并不是一种规范，只是用来理解Restful的工具**。所以，做到了Level2，也就是面向资源和使用Http动词，就已经很Restful了。**Restful本身也不是一种规范**，我比较倾向于用“**风格**"来形容它。 

参考：https://zhuanlan.zhihu.com/p/30396391?group_id=937244108725641216



### 三、什么是RESTful

 基于REST构建的API就是Restful风格。



### 四、为什么使用RESTful

1．JSP技术可以让我们在页面中嵌入Java代码，但是这样的技术实际上限制了我们的开发效率，因为需要我们Java工程师将html转换为jsp页面，并写一些脚本代码，或者前端代码。这样会严重限制我们的开发效率，也不能让我们的java工程师专注于业务功能的开发，所以目前越来越多的互联网公司开始实行前后端分离。 

2．近年随着移动互联网的发展，各种类型的Client层出不穷，RESTful可以通过一套统一的接口为Web，iOS和Android提供服务。另外对于广大平台来说，比如微博开放平台，微信开放平台等，它们不需要有显式的前端，只需要一套提供服务的接口，RESTful无疑是最好的选择。RESTful架构如下： 

### 五、如何设计Restful风格的API

##### 1.路径设计

 —>在RESTful架构中，每个网址代表一种资源（resource），所以网址中不能有动词，只能有名词，而且所用的名词往往与数据库的表名对应，一般来说，数据库中的表都是同种记录的”集合”（collection），所以API中的名词也应该使用复数。 
 —>举例来说，有一个API提供动物园（zoo）的信息，还包括各种动物和雇员的信息，则它的路径应该设计成下面这样。

```
https://api.example.com/v1/zoos 

https://api.example.com/v1/animals 

https://api.example.com/v1/employees
```



##### 2.HTTP动词设计

对于资源的具体操作类型，由HTTP动词表示，常用的HTTP动词如下：

| 请求方式 | 含义                                   |
| -------- | -------------------------------------- |
| GET      | 获取资源（一项或多项）                 |
| POST     | 新建资源                               |
| PUT      | 更新资源（客户端提供改变后的完整资源） |
| DELETE   | 删除资源                               |

如何通过路径和http动词获悉要调用的功能：

| 请求方式                   | 含义                                               |
| -------------------------- | -------------------------------------------------- |
| GET /zoos                  | 列出所有动物园                                     |
| POST /zoos                 | 新建一个动物园                                     |
| GET /zoos/ID               | 获取某个指定动物园的信息                           |
| PUT /zoos/ID               | 更新某个指定动物园的信息（提供该动物园的全部信息） |
| DELETE /zoos/ID            | 删除某个动物园                                     |
| GET /zoos/ID/animals       | 列出某个指定动物园的所有动物                       |
| DELETE /zoos/ID/animals/ID | 删除某个指定动物园的指定动物                       |

