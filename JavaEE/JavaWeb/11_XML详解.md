# 第十一节 XML详解

## 一、概念

XML：Extensible Markup Language 可扩展标记语言；

- **可扩展**：标签都是自定义的。 `<user>  <student>`

- **功能：**存储数据
  1. 配置文件；
  2. 在网络中传输；
- **xml与html的区别：**
  1. xml标签都是自定义的，html标签是预定义；
  2. xml的语法严格，html语法松散；
  3. xml是存储数据的，html是展示数据；

> w3c:万维网联盟。
>
> w3c先推出html非常火爆，但是由于浏览器的恶性竞争，造成html语法很松散。w3c不能容忍，于是造出了xml来替代html，但是由于环境定型，很难替代html。于是，xml就必须找到自己的定位。
>
> xml存储数据优于properties，可读性大大增强！



## 二、语法

### 2.1、基本语法：

1. xml文档的后缀名 .xml
2. xml第一行必须定义为文档声明，注意**：即便第1行是空格也不行！！**
3. xml文档中有且仅有一个根标签
4. 属性值必须使用引号(单双都可)引起来
5. 标签必须正确关闭
6. xml标签名称区分大小写，而**［html不区分大小写、一般使用小写］**

```xml
<?xml version='1.0' ?>
<users>
    <user id='1'>
        <name>zhangsan</name>
        <age>23</age>
        <gender>male</gender>
        <br/>
    </user>

    <user id='2'>
        <name>lisi</name>
        <age>24</age>
        <gender>female</gender>
    </user>
</users>
```



### 2.2、组成部分：

- #### 文档声明：

  1. 格式 `<?xml  属性列表  ?>`

  2. 属性列表：
     - version：版本号，必须的属性; **一般version＝1.0**
     - encoding：编码方式。告知解析引擎当前文档使用的字符集，默认值：ISO-8859-1;(一定要注意：文档编码与encoding一致，否则报错！！)
     - standalone：是否独立; （取值：yes 不依赖其他文件、no 依赖其他文件），一般不设置；

- #### 指令(了解)：结合css的：可以让xml如同html给浏览器解析

  ```xml
  <?xml-stylesheet type="text/css" href="a.css" ?>
  ```

- #### 标签：标签名称自定义的。规则如下：

  - 名称可以包含字母、数字以及其他的字符 

  * 名称不能以数字或者标点符号开始 
  * 名称不能以字母 xml（或者 XML、Xml 等等）开始 
  * 名称不能包含空格 

- #### 属性： id属性值唯一

- #### 文本：CDATA区：在该区域中的数据会被原样展示

  格式：`  <![CDATA[ 数据 ]]>`



### 2.3、约束：规定xml文档的书写规则

- 约束使用者：一般是框架的使用者、即程序员；

  - 能够在xml中引入约束文档；
  - 能够简单的读懂约束文档；

- 分类：DTD 简单的约束技术；Schema 复杂的约束技术；

- DTD：使用DTD约束，就必须先引人dtd文档到xml文档中；

  - 内部dtd：将约束规则定义在xml文档中； 不通用，了解即可！

  ```dtd
  <!DOCTYPE students [
  		<!ELEMENT students (student*) >
  		<!ELEMENT student (name,age,sex)>
  		<!ELEMENT name (#PCDATA)>
  		<!ELEMENT age (#PCDATA)>
  		<!ELEMENT sex (#PCDATA)>
  		<!ATTLIST student number ID #REQUIRED>
  		]>
  ```

  - 外部dtd：将约束的规则定义在外部的dtd文件中；
    - 本地：**`<!DOCTYPE 根标签名 SYSTEM "dtd文件的位置">`** 
    - 网络：**`<!DOCTYPE 根标签名 PUBLIC "dtd文件名字" "dtd文件的位置URL">`** 

  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE students SYSTEM "student.dtd">
  <students>
  	<student number="itcast_0001">
  		<name>tom</name>
  		<age>18</age>
  		<sex>male</sex>
  	</student>
  </students>
  ```

  > **注意：dtd只对标签进行约束，但并没有对标签中的内容有约束，会造成内容填写随意，可能致使程序报错！！！所有，引入Schema来增加约束范围！**

- Schema：非常严谨的约束技术！**［ .xsd文档 ］**

  **实例：**

  ```xml
  <?xml version="1.0"?>
  <xsd:schema xmlns="http://www.itcast.cn/xml"
          xmlns:xsd="http://www.w3.org/2001/XMLSchema"
          targetNamespace="http://www.itcast.cn/xml" elementFormDefault="qualified">
      <!-- element第1层级 根元素， type紧跟后面有定义-->
      <xsd:element name="students" type="studentsType"/>
      <xsd:complexType name="studentsType">
          <xsd:sequence>
              <xsd:element name="student" type="studentType" minOccurs="0" maxOccurs="unbounded"/>
          </xsd:sequence>
      </xsd:complexType>
      
      <xsd:complexType name="studentType">
          <xsd:sequence>
              <xsd:element name="name" type="xsd:string"/>
              <xsd:element name="age" type="ageType" />
              <xsd:element name="sex" type="sexType" />
          </xsd:sequence>
          <xsd:attribute name="number" type="numberType" use="required"/>
      </xsd:complexType>
      <xsd:simpleType name="sexType">
          <xsd:restriction base="xsd:string">
              <xsd:enumeration value="male"/>
              <xsd:enumeration value="female"/>
          </xsd:restriction>
      </xsd:simpleType>
      <xsd:simpleType name="ageType">
          <xsd:restriction base="xsd:integer">
              <xsd:minInclusive value="0"/>
              <xsd:maxInclusive value="256"/>
          </xsd:restriction>
      </xsd:simpleType>
      <xsd:simpleType name="numberType">
          <xsd:restriction base="xsd:string">
              <xsd:pattern value="heima_\d{4}"/>
          </xsd:restriction>
      </xsd:simpleType>
  </xsd:schema> 
  ```

  **引入：**

  1. 填写xml文档的根元素;
  2. 引入xsi前缀.  `xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"`
  3. 引入xsd文件命名空间.  `xsi:schemaLocation="http://www.itcast.cn/xml  student.xsd"`
  4. 为每一个xsd约束声明一个前缀,作为标识  `xmlns="http://www.itcast.cn/xml" `(缺省格式)

  ```xml
  <students   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  				xmlns="http://www.itcast.cn/xml"
  				xsi:schemaLocation="http://www.itcast.cn/xml  student.xsd">
  ```

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
      xmlns="http://www.springframework.org/schema/beans"
  	xmlns:context="http://www.springframework.org/schema/context"
      xmlns:mvc="http://www.springframework.org/schema/mvc"
      xsi:schemaLocation="
          http://www.springframework.org/schema/beans
          http://www.springframework.org/schema/beans/spring-beans.xsd
          http://www.springframework.org/schema/context 
          http://www.springframework.org/schema/context/spring-context.xsd
          http://www.springframework.org/schema/mvc
          http://www.springframework.org/schema/mvc/spring-mvc.xsd">
  
      <context:annotation-config />
      <context:component-scan base-package="cn.cisol.mvcdemo">
          <context:include-filter type="annotation"
              expression="org.springframework.stereotype.Controller" />
      </context:component-scan>
  
      <mvc:annotation-driven />
      <mvc:resources mapping="/resources/**" location="/resources/" />
  
      <bean class=".ContentNegotiatingViewResolver"> </bean>
  </beans>    
  ```



## 三、XML解析：操作xml文档

### 3.1、操作xml文档

- **解析(读取)：将文档中的数据读取到内存中; (重点！！！)**

- 写入：将内存中的数据保存到xml文档中。持久化的存储;


### 3.2、解析xml的方式：DOM & SAX

- **DOM ：将标记语言文档一次性加载进内存，在内存中形成一颗dom树；**

  优点：操作方便，可以对文档进行CRUD的所有操作；

  缺点：占内存；

- **SAX ：逐行读取，基于事件驱动的（编码复杂）；**

  优点：不占内存；

  缺点：只能读取，不能增删改；

>服务器：一般使用DOM思想解析。
>
>前端（移动Android）：一般使用sax思想解析。



### 3.3、xml常见的解析器：

- JAXP：sun公司提供的解析器，支持dom和sax两种思想。比较差；
- DOM4J：一款非常优秀的解析器；（自我补充）
- **Jsoup：**jsoup 是一款Java 的HTML解析器，当然也方便应用于xml解析（重点掌握！）
- PULL：Android操作系统内置的解析器，sax方式的。（自我补充）



### 3.4、Jsoup解析：

jsoup 是一款Java 的HTML解析器，可直接解析某个URL地址、HTML文本内容。它提供了一套非常省力的API，可通过DOM，CSS以及类似于jQuery的操作方法来取出和操作数据。

#### 快速入门：

步骤：导入jar包 -->获取Document对象 -->获取对应的标签Element对象 -->获取数据

```java
// 1 . 获取document对象
String path =JsoupDemo1.class.getClassLoader().getResource("student.xml").getPath();
Document document = Jsoup.parse(new File(path),"utf-8");
// 2. 获取元素对象 Element
Elements names = document.getElementsByTag("name");
Element name = names.get(0);
// 3. 获取数据
String nameText = name.text();
System.out.println(nameText);
```

> ```java
> // 特别注意，若直接写文件名，那该文件一定放在src目录下！！！否则就是nullpointerException
> JsoupDemo1.class.getClassLoader().getResource("student.xml")
> ```



#### 对象的使用：

- ##### Jsoup：工具类，`parse`方法可以解析html或xml文档，返回Document

  - parse(File in, String charsetName)：解析xml或html文件的。
  - parse(String html)：解析xml或html字符串; xml、html 文本当作字符串处理；
  - parse(URL url, int timeoutMillis)：通过网络路径获取指定的html或xml的文档对象，即获取页面的html源码；

- ##### Document：文档对象。代表内存中的dom树。作用：获取任意Element对象；

  - getElementById(String id)：根据id属性值获取唯一的element对象;
  - getElementsByTag(String tagName)：根据标签名称获取元素对象集合;
  - getElementsByAttribute(String key)：根据属性名称获取元素对象集合;
  - getElementsByAttributeValue(String key, String value)：根据对应的属性名和属性值获取元素对象集合;

- ##### Elements：元素Element对象的集合。可以当做 ArrayList`<Element>`来使用

- ##### Element：元素对象

  1.获取子元素对象，不是任意的

  - getElementById(String id)：根据id属性值获取唯一的element对象;
  - getElementsByTag(String tagName)：根据标签名称获取元素对象集合;
  - getElementsByAttribute(String key)：根据属性名称获取元素对象集合;
  - getElementsByAttributeValue(String key, String value)：同上！

  2.获取属性值

  - String attr(String key)：根据属性名称获取属性值

  3.获取文本内容

  - String text():获取文本内容;
  - String html():获取标签体的所有内容(包括字标签的字符串内容),innerHtml;

- #####  Node：节点对象

  Node是Document和Element的父类!



### 3.5、快捷查询方式：

- #### selector:选择器





