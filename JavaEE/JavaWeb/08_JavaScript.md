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

  1. 原始数据类型(基本数据类型)：
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
  ```












