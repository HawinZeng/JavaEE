# 第九节 JavaScript 高阶 BOM、DOM



## 一、DOM及事件简单入门

### 1.1、DOM

```javascript
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

#### 3.3.4、案例：动态表格

```javascript
// 方案一：使用XML－DOM对象编程
document.getElementById("btn_add").onclick = function(){
        //2.获取文本框的内容
        var id = document.getElementById("id").value;
        var name = document.getElementById("name").value;
        var gender = document.getElementById("gender").value;

        //3.创建td，赋值td的标签体
        //id 的 td
        var td_id = document.createElement("td");
        var text_id = document.createTextNode(id);
        td_id.appendChild(text_id);
        //name 的 td
        var td_name = document.createElement("td");
        var text_name = document.createTextNode(name);
        td_name.appendChild(text_name);
        //gender 的 td
        var td_gender = document.createElement("td");
        var text_gender = document.createTextNode(gender);
        td_gender.appendChild(text_gender);
        //a标签的td
        var td_a = document.createElement("td");
        var ele_a = document.createElement("a");
        ele_a.setAttribute("href","javascript:void(0);");
        ele_a.setAttribute("onclick","delTr(this);");
        var text_a = document.createTextNode("删除");
        ele_a.appendChild(text_a);
        td_a.appendChild(ele_a);

        //4.创建tr
        var tr = document.createElement("tr");
        //5.添加td到tr中
        tr.appendChild(td_id);
        tr.appendChild(td_name);
        tr.appendChild(td_gender);
        tr.appendChild(td_a);
        //6.获取table
        var table = document.getElementsByTagName("table")[0];
        table.appendChild(tr);
    }
```

```javascript
// 方案二：HTML－DOM innerHTML属性应用(简单)
document.getElementById("btn_add").onclick = function() {
        //2.获取文本框的内容
        var id = document.getElementById("id").value;
        var name = document.getElementById("name").value;
        var gender = document.getElementById("gender").value;

        //获取table
        var table = document.getElementsByTagName("table")[0];

        //追加一行
        table.innerHTML += "<tr>\n" +
            "        <td>"+id+"</td>\n" +
            "        <td>"+name+"</td>\n" +
            "        <td>"+gender+"</td>\n" +
            "        <td><a href=\"javascript:void(0);\" onclick=\"delTr(this);\" >删除</a></td>\n" +
            "    </tr>";
    }

    //删除方法
    function delTr(obj){
        var table = obj.parentNode.parentNode.parentNode;
        var tr = obj.parentNode.parentNode;

        table.removeChild(tr);
    }
```



## 四、事件监听机制

#### 4.1、概念：某些组件被执行了某些操作后，触发某些代码的执行。

- **事件：**某些操作。如： 单击，双击，键盘按下了，鼠标移动了
- **事件源：**组件。如： 按钮 文本输入框...
- **监听器：**代码。
- **注册监听：**将事件，事件源，监听器结合在一起。 当事件源上发生了事件，则触发执行监听器代码。



#### 4.2、常见的事件：

- 点击事件：
  - onclick：单击事件
  - ondblclick：双击事件
- 焦点事件：
  - onblur：失去焦点
  - onfocus:元素获得焦点。
- 加载事件：
  - onload：一张页面或一幅图像完成加载。
- 鼠标事件：
  - onmousedown	鼠标按钮被按下。
  - onmouseup	鼠标按键被松开。
  - onmousemove	鼠标被移动。
  - onmouseover	鼠标移到某元素之上。
  - onmouseout	鼠标从某元素移开。
- 键盘事件：
  - onkeydown	某个键盘按键被按下。	
  - onkeyup		某个键盘按键被松开。
  - onkeypress	某个键盘按键被按下并松开。
- 选择和改变：
  - onchange	域的内容被改变。
  - onselect	文本被选中。
- 表单事件：
  - onsubmit	确认按钮被点击。
  - onreset	重置按钮被点击。



## 五、事件案例

1. #### 表格全选

   ```html
   <head>
   ...
       <style>
   		...
           .over{
               background-color: pink;
           }
           .out{
               background-color: white;
           }
       </style>
   
       <script>
           window.onload = function () {
               document.getElementById("selectAll").onclick = function(){
                   var cbs = document.getElementsByName("cb");
                   for(var i=0,length=cbs.length;i<length;i++){
                       cbs[i].checked = true;
                   }
               };
   
               document.getElementById("unSelectAll").onclick = function(){
                   var cbs = document.getElementsByName("cb");
                   for(var i=0,length=cbs.length;i<length;i++){
                       cbs[i].checked = false;
                   }
               };
   
               document.getElementById("selectRev").onclick = function(){
                   var cbs = document.getElementsByName("cb");
                   for(var i=0,length=cbs.length;i<length;i++){
                       cbs[i].checked = !cbs[i].checked;
                   }
               };
   
               document.getElementById("cb1").onclick = function () {
                   var cb1 = document.getElementById("cb1");
                   var cbs  = document.getElementsByName("cb");
                   for(var i=0,length=cbs.length;i<length;i++){
                       cbs[i].checked = cb1.checked;
                   }
               }
   
               // 在onload中添加改变背景色，不能直接定义function给标签使用，而只能给标签添加onmouseover，onmouseout
               var trs = document.getElementsByTagName("tr");
               for(var i=0,length =trs.length;i<length;i++){
                   trs[i].onmouseover = function () {
                       this.style.backgroundColor = "blue";
                   };
   
                   trs[i].onmouseout = function () {
                       this.style.backgroundColor = "white";
                   }
               }
   
               // 低级写法！！！！比较low
               // var cb1 = document.getElementById("cb1");
               // var cb2 = document.getElementById("cb2");
               // var cb3 = document.getElementById("cb3");
               // var cb4 = document.getElementById("cb4");
               //
               // cb1.onclick = function () {
               //     if(cb1.checked){
               //         cb2.checked = true;
               //         cb3.checked = true;
               //         cb4.checked = true;
               //     }else{
               //         cb2.checked = false;
               //         cb3.checked = false;
               //         cb4.checked = false;
               //     }
               // };
               //
               // selectAll.onclick = function () {
               //     cb1.checked = true;
               //     cb2.checked = true;
               //     cb3.checked = true;
               //     cb4.checked = true;
               // };
               //
               // unSelectAll.onclick  = function(){
               //     cb1.checked = false;
               //     cb2.checked = false;
               //     cb3.checked = false;
               //     cb4.checked = false;
               // };
               //
               // selectRev.onclick = function () {
               //     if(cb1.checked){
               //         cb1.checked = false;
               //     }else{
               //         cb1.checked = true;
               //     }
               //     if(cb2.checked){
               //         cb2.checked = false;
               //     }else{
               //         cb2.checked = true;
               //     }
               //     if(cb3.checked){
               //         cb3.checked = false;
               //     }else{
               //         cb3.checked = true;
               //     }
               //     if(cb4.checked){
               //         cb4.checked = false;
               //     }else{
               //         cb4.checked = true;
               //     }
               // };
           };
   		
           // 这种写法，最后放在标签加载的后面，防止异常事故发生，一般可能没有问题，毕竟是绑定事件，要页面出来才能发生事件效果。
           // function overTr(obj) {
           //     obj.style.backgroundColor = "red";
           //     // obj.className = "over";
           // }
           //
           // function outTr(obj){
           //     obj.style.backgroundColor = "white";
           //     // obj.className = "out";
           // }
       </script>
   </head>
   <table>
       <caption>学生信息表</caption>
       <tr onmouseover="overTr(this);" onmouseout="outTr(this);">
           <th><input type="checkbox" id="cb1" name="cb"></th>
           <th>编号</th>
           <th>姓名</th>
           <th>性别</th>
           <th>操作</th>
       </tr>
   
       <tr onmouseover="overTr(this);" onmouseout="outTr(this);">
           <td><input type="checkbox" id="cb2" name="cb"></td>
           <td>1</td>
           <td>令狐冲</td>
           <td>男</td>
           <td><a href="javascript:void(0);">删除</a></td>
       </tr>
   
       <tr onmouseover="overTr(this);" onmouseout="outTr(this);">
           <td><input type="checkbox" id="cb3" name="cb"></td>
           <td>2</td>
           <td>任我行</td>
           <td>男</td>
           <td><a href="javascript:void(0);">删除</a></td>
       </tr>
   
       <tr onmouseover="overTr(this);" onmouseout="outTr(this);">
           <td><input type="checkbox" id="cb4" name="cb"></td>
           <td>3</td>
           <td>岳不群</td>
           <td>?</td>
           <td><a href="javascript:void(0);">删除</a></td>
       </tr>
   </table>
   <div>
       <input type="button" id="selectAll" value="全选">
       <input type="button" id="unSelectAll" value="全不选">
       <input type="button" id="selectRev" value="反选">
   </div>
   ```

2. #### 表单验证

   ```html
   <script>
           window.onload = function () {
               // 表单提交验证，默认是true，点击就提交。若接受返回值false则不提交；
               document.getElementById("form").onsubmit = function () { 
                   return checkUserName() && checkPassword();
               };
   			// 焦点消失
               document.getElementById("username").onblur = checkUserName; 
               document.getElementById("password").onblur = checkPassword;
           };
   
           function checkUserName() {
               var username = document.getElementById("username");
               var reg  = /^\w{6,12}$/;
               var flag = reg.test(username.value);
               var e_username = document.getElementById("e_username");
               if(flag){
                   e_username.innerHTML = "<img width='25' height='25' src='img/gou.png'/>";
               }else{
                   e_username.innerHTML = "姓名输入格式有误";
               }
               return flag;
           }
   
           function checkPassword() {
               var password = document.getElementById("password");
               var reg  = /^\w{6,12}$/;
               var flag = reg.test(password.value);
               var e_password = document.getElementById("e_password");
               if(flag){
                   e_password.innerHTML = "<img width='35' height='35' src='img/gou.png'/>";
               }else{
                   e_password.innerHTML = "密码输入格式有误";
               }
               return flag;
           }
       </script>
   ```






