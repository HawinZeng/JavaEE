# 第九节 JavaScript 高阶 BOM、DOM



## 一、DOM及事件简单入门

### 1.1、DOM

```
* 功能：控制html文档的内容
* 获取页面标签(元素)对象：Element
	* document.getElementById("id值"):通过元素的id获取元素对象

* 操作Element对象：
	1. 修改属性值：
		1. 明确获取的对象是哪一个？
		2. 查看API文档，找其中有哪些属性可以设置
	2. 修改标签体内容：
		* 属性：innerHTML
		1. 获取元素对象
		2. 使用innerHTML属性修改标签体内容
```

### 1.2、事件

```javascript
* 功能： 某些组件被执行了某些操作后，触发某些代码的执行。

* 如何绑定事件
	1. 直接在html标签上，指定事件的属性(操作)，属性值就是js代码
		1. 事件：onclick--- 单击事件
	// 有明显的缺点：js与html耦合在一起了
	<head>
    <script>
        function fun() { alert("被点了");alert("又被点了"); }
    </script>
    </head>        
	<img id="light" src="../img/off.gif" onclick="fun()"/>
    

	2. 通过js获取元素对象，指定事件属性，设置一个函数
    <img id="light" src="../img/off.gif"/>
    <script>
        function fun() { alert("被点了"); }
        var light = document.getElementById("light");
        light.onclick = fun;
    </script>
```

> 注意：**js操作element对象，要在对象标签下面才可以！！！而没有操作element 对象，则可以放任意位置！！**

```javascript
<!--   开关切换   -->
<body>
<img id="light" src="../img/off.gif"/>
<script>
    var light = document.getElementById("light");
    var flag = false;
    light.onclick = function fun() {
        if (flag) {
            light.src = "../img/off.gif";
            flag = false;
        } else {
            light.src = "../img/on.gif";
            flag = true;
        }
    };
</script>
</body>
```



## 二、BOM

### 2.1、概念：

BOM：Browser Object Model 浏览器对象模型；将浏览器的各个组成部分封装成对象。

### 2.2、组成：五个对象

**Window：窗口对象**

Navigator：浏览器对象

Screen：显示器屏幕对象

**History：历史记录对象**

**Location：地址栏对象**

分析上面较重要的3个加粗的对象！！！



### 2.3、Window：窗口对象

- **创建：**不需要创建，直接使用即可；

- **方法：**由于可以直接使用，所以`window.`一般都省略！

  1.- **与弹出框有关的方法**：

  - **`alert()`:** 显示带有一段消息和一个确认按钮的警告框。

  - **`confirm()`:** 显示带有一段消息以及确认按钮和取消按钮的对话框。**这个会常用！！**！

    点击确认按钮，返回true；反之，返回false；

  - **`prompt()`:**  显示可提示用户输入的对话框。返回值：获取用户输入的值;



  2.- **与打开关闭有关的方法**：

  - **`close()`:**    关闭浏览器窗口。 **注意：谁调用我 ，我关谁！**
  - **`open()`:**      打开一个新的浏览器窗口。**返回新的Window对象**！

  ```javascript
  <input id="openBtn" type="button" value="打开新窗口"><br>
  <input id="closeBtn" type="button" value="关闭新窗口"><br>
  
  <script>
      var openBtn = document.getElementById("openBtn");
      var closeBtn = document.getElementById("closeBtn");
      var newWindow;
      openBtn.onclick = function () {
          newWindow = open("http://www.baidu.com");
      };
  
      closeBtn.onclick = function () {
          newWindow.close();
      };
  </script>
  ```

  3.- **与定时器有关的方式**:

  - **`setTimeout()`:**	在指定的毫秒数后调用函数或计算表达式。
  - **`clearTimeout()`:**	取消由 setTimeout() 方法设置的 timeout。
  - **`setInterval()`:**	按照指定的周期（以毫秒计）来调用函数或计算表达式。
  - **`clearInterval()`:**	取消由 setInterval() 设置的 timeout。

  ```javascript
  <!--  轮播图 -->
  <div>
      <img id="banner" src="../img/banner_1.jpg">
  </div>
  
  <script>
      var banner = document.getElementById("banner");
      var num = 1;
      function carousel() {
          num++;
          if(num>3){
              num = 1;
          }
          banner.src = "../img/banner_"+num+".jpg";
      }
      setInterval(carousel,2000);
  </script>    
  ```

- **属性：**

  1. 获取其他BOM对象：
     history
     location
     Navigator
     Screen:
  2. 获取DOM对象
     document

- ##### 特点:

  Window对象不需要创建可以直接使用 window使用。 window.方法名();

  window引用可以省略。  方法名();



### 2.4、Location：地址栏对象

- **创建(获取)：**

  **window.location**

  **location**

- **方法：**

  `reload()`  重新加载当前文档。刷新;

- **属性：**

  href	设置或返回完整的 URL。

```html
<!--  自动跳转页面 -->
<p><span id="time">5</span> 秒之后自动跳转到主页...</p>
<script>
    var time  = document.getElementById("time");
    var second = 5;
    function showTime() {
        second--;
        time.innerHTML = second+"";

        if(!second){
            location.href = "http://www.baidu.com";
        }
    }
    setInterval(showTime,1000);
</script>
```



### 2.5、History：历史记录对象

- **创建(获取)：**

  **window.history**

  **history**

- **方法：**

  - `back()`  重新加载当前文档。刷新;
  - `forward()`  重新加载当前文档。刷新;
  - `go(params)`  重新加载当前文档。刷新;

- **属性：**

  length	返回当前窗口历史列表中的 URL 数量。



## 三、DOM

### 3.1、概念： 

DOM ：Document Object Model 文档对象模型

将标记语言文档的各个组成部分，封装为对象。可以使用这些对象，对标记语言文档进行CRUD的动态操作；



### 3.2、W3C DOM 标准被分为 3 个不同的部分：

- 核心 DOM - 针对任何结构化文档的标准模型
  - **Document：文档对象**：重要
  - **Element：元素对象**：重要
  - Attribute：属性对象
  - Text：文本对象
  - Comment:注释对象
  - **Node：节点对象，其他5个的父对象**：重要
- XML DOM - 针对 XML 文档的标准模型
- HTML DOM - 针对 HTML 文档的标准模型



### 3.3、核心DOM模型

#### 3.3.1、Document：文档对象

- 创建(获取)：在html dom模型中可以使用window对象来获取

- 方法：

  - 获取Element对象：

    `getElementById()`	： 根据id属性值获取元素对象。id属性值一般唯一;

    `getElementsByTagName()`：根据元素名称获取元素对象们。返回值是一个数组;

    `getElementsByClassName()`：根据Class属性值获取元素对象们。返回值是一个数组;

    `getElementsByName()`：根据name属性值获取元素对象们。返回值是一个数组

  - 创建其他DOM对象：

    `createAttribute(name)`
    `createComment()`
    `createElement()`
    `createTextNode()`

- 属性

#### 3.3.2、Element：元素对象

- 获取/创建：通过document来获取和创建
- 方法：
  - removeAttribute()：删除属性
  - setAttribute()：设置属性

```javascript
    <a id="aid">点我试一试！</a> <br>
    <input id="btn_set" type="button" value="设置link">
    <input id="btn_remove" type="button" value="去除link">

    <script>
        var a = document.getElementById("aid");
        var btn_set = document.getElementById("btn_set");
        var btn_remove = document.getElementById("btn_remove");
        btn_set.onclick = function(){
            a.setAttribute("href","https://www.baidu.com");
        }

        btn_remove.onclick = function () {
            a.removeAttribute("href");
        }
    </script>
```



#### 3.3.3、Node：节点对象，其他5个的父对象

- 特点：所有dom对象都可以被认为是一个节点
- 方法：**CRUD dom树;**
  - appendChild()：向节点的子节点列表的结尾添加新的子节点。
  - removeChild()	：删除（并返回）当前节点的指定子节点。
  -  replaceChild()：用新节点替换一个子节点。
- 属性：parentNode 返回节点的父节点。

```javascript
<!--   添加／删除子节点  -->
<div id="div1">
    <div id="div2"> div2</div>
    div1
</div>

<input id="btn_remove" type="button" value="删除子标签">
<input id="btn_add" type="button" value="添加子标签">

<script>
    var element_div1 = document.getElementById("div1");
    var element_div2 = document.getElementById("div2");

    var btn_add = document.getElementById("btn_add");
    var btn_remove = document.getElementById("btn_remove");

    btn_remove.onclick = function () {
        element_div1.removeChild(element_div2);
    }

    btn_add.onclick = function () {
        var element_div3 = document.createElement("div");
        element_div3.setAttribute("id","div3");
        element_div1.appendChild(element_div3);
    }
</script>
```













