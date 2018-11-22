# 第 八节 JavaScript

## 一、概述

#### 1.1、概念：一门客户端脚本语言

- 运行在客户端浏览器中的。每一个浏览器都有JavaScript的解析引擎
- 脚本语言：不需要编译，直接就可以被浏览器解析执行了

#### 1.2 、功能：

可以来增强用户和html页面的交互过程，可以来控制html元素，让页面有一些动态的效果，增强用户的体验。

#### 1.3、JavaScript发展史：

- 1992年，Nombase公司，开发出第一门客户端脚本语言，专门用于表单的校验。命名为 ： C--	，后来更名为：ScriptEase；
- 1995年，Netscape(网景)公司，开发了一门客户端脚本语言：LiveScript。后来，请来SUN公司的专家，修改LiveScript，命名为JavaScript；
- 1996年，微软抄袭JavaScript开发出JScript语言；
- 1997年，ECMA(欧洲计算机制造商协会)，制定出客户端脚本语言的标准：ECMAScript，就是统一了所有客户端脚本语言的编码方式；

```javascript
JavaScript = ECMAScript + JavaScript自己特有的东西(BOM+DOM) －－>三部分
```



## 二、ECMAScript：客户端脚本语言的标准

### 2.1、基本语法：

- **与html结合方式：**

  1. 内部JS：定义`<script>`，标签体内容就是js代码
  2. 外部JS：定义`<script>`，通过src属性引入外部的js文件

  > 注意：
  > ```javascript
  > 1. <script>可以定义在html页面的任何地方。但是定义的位置会影响执行顺序。
  > 2. <script>可以定义多个。
  > 
  > <script>
  >     alert("Hello JavaScript!");
  > </script>
  > ```

- **注释：同java，没有文档注释**

  1. 单行注释 //
  2. 多行注释 /*多行注释 */

- **数据类型：分2类**

  1. 原始数据类型(基本数据类型)：5种类型
     - number：数字。 整数/小数/NaN(not a number 一个不是数字的数字类型)
     - string：字符串。 字符串  "abc" "a" 'abc'  (没有字符的概念，全都是字符串)
     - boolean：true / false
     - null：一个对象为空的占位符
     - undefined：未定义。如果一个变量没有给初始化值，则会被默认赋值为undefined
  2. 引用数据类型：对象

- **变量：一小块存储数据的内存空间**

  Java语言是强类型语言，而JavaScript是弱类型语言。

  - **强类型：**在开辟变量存储空间时，定义了空间将来存储的数据的数据类型。只能存储固定类型的数据。
  - **弱类型：**在开辟变量存储空间时，不定义空间将来的存储数据类型，可以存放任意类型的数据。

  ```javascript
  语法：关键字 var
  var name ＝ 初始化值;
  
  typeof运算符：获取变量的类型。[注：null运算后得到的是object]
  ```

- **运算符**

  1. 一元运算符：只有一个运算数的运算符

     ++，-- ， +(正号) 

     > 注意：在JS中，如果运算数不是运算符所要求的类型，那么js引擎会自动的将运算数进行类型转换;
     >
     > 其他类型转number：
     >
     > ​	string转number：按照字面值转换。如果字面值不是数字，则转为NaN（不是数字的数字）;
     >
     > ​	boolean转number：true转为1，false转为0;
     >
     > ```javascript
     > var a = +'123'; // 这个就是一个number 123；
     > var b = +"abc"; // NaN . 参与运算还是NaN 即 NaN+1 --> NaN
     > var c = +true; // 1
     > ```

  2. 算数运算符：+ - * / % ...

  3. 赋值运算符：= += -+....

  4. 比较运算符：>,<,>=, <=,==,===(全等于) **［java中只允许同类型比较，但是js可以不同类型比较］**

     - 类型相同：直接比较

       ( 字符串：按照字典顺序比较。按位逐一比较，直到得出大小为止。)

     - 类型不同：先进行类型转换，再比较

       ( ===：全等于。在比较之前，先判断类型，如果类型不一样，则直接返回false 。)

     ```javascript
     document.write("abc" < "acd"); // true
     document.write("123" < 122); // false
     document.write("123" == 123); // true,自动转换类型
     document.write("123" === 123); // false, 不会转换类型
     ```

  5. 逻辑运算符：&& || !

     其他类型转boolean：

     - number：0或NaN为假，其他为真

     - string：除了空字符串("")，其他都是true

     - null&undefined:都是false

     - 对象：所有对象都为true

     ```javascript
     使用&&、||时，一定要清楚两侧就是一个boolean值了，而如下操作是错误，但是还是有意义所在的：
     document.write((0 && 3)+"<br>"); // 0 -- 0(false)
     document.write((3 && 0)+"<br>"); // 0- 0(false)
     
     document.write((0 || 3)+"<br>"); // 3 -- 3(true)
     
     document.write((null || 3)+"<br>"); // 3 -- 3(true)
     document.write((null && 3)+"<br>"); // null --null(false)
     ```

  6. 

- 










