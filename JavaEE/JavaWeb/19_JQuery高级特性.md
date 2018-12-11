# 第十九节 JQuery高级特性

## 一、动画：三种方式显示和隐藏元素

#### 1.1、默认显示和隐藏方式

```javascript
1. show([speed,[easing],[fn]]) -- 三个参数都没有，没有动画效果！
参数说明：
	1.1 speed：动画的速度。三个预定义的值("slow","normal", "fast")或表示动画时长的毫秒数值(如：1000)
	1.2 easing：用来指定切换效果，默认是"swing"，可用参数"linear"
		* swing：动画执行时效果是 先慢，中间快，最后又慢；
		* linear：动画执行时速度是匀速的
	1.3 fn：在动画完成时执行的函数，每个元素执行一次。	
	
2. hide([speed,[easing],[fn]])
3. toggle([speed],[easing],[fn]) -- 显示，隐藏相互切换
```

#### 1.2、滑动显示和隐藏方式

```javascript
1. slideDown([speed],[easing],[fn])
2. slideUp([speed,[easing],[fn]])
3. slideToggle([speed],[easing],[fn])
```

#### 1.3、淡入淡出显示和隐藏方式

```javascript
1. fadeIn([speed],[easing],[fn])
2. fadeOut([speed],[easing],[fn])
3. fadeToggle([speed,[easing],[fn]])
```



##  二、遍历

#### 2.1、js的遍历方式

```
for(var i=0;i<condition; 步长)
```

#### 2.2、jq的遍历方式

1. ##### jq对象.each(callback)

   **语法：`jquery对象.each(function(index,element){});`**

   - index:就是元素在集合中的索引;
   - element：就是集合中的每一个元素对象
   - this: 集合中的每一个元素对象

   **回调函数返回值:**

   - true:如果当前function返回为false，则结束循环(break)。
   - false:如果当前function返回为true，则结束本次循环，继续下次循环(continue)

   ```javascript
   citys.each(function (index,element) {
        // alert(this.innerHTML);
        if("上海" == $(element).html()){
             return false; 
        }
        alert(index+":"+$(this).html());
    });
   ```

2. ##### $.each(object, [callback])

   ```javascript
   $.each(citys,function(index,element){
         alert(index+":"+$(element).html());
   });
   ```

3. #####  for..of: jquery 3.0 版本之后提供的方式

   ```javascript
   // for(元素对象 of 容器对象)
   for(var element of citys){
       alert($(element).html());
   }
   ```


## 三、事件绑定

#### 3.1、jquery标准的绑定方式

* **格式：`jq对象.事件方法(回调函数)；`**

  ```javascript
  $("#username").blur(function () { // 失去焦点事件触发
      var username = $(this).val();
   }
  ```

* 注：如果调用事件方法，不传递回调函数，则会触发浏览器默认行为。

  - 获取焦点：`$(#name).focus()`

  - 表单对象.submit(); ——> 让表单提交 

#### 3.2、on绑定事件/off解除绑定

- **格式：`jq对象.on("事件名称",回调函数)`**
- **格式：`jq对象.off("事件名称")`**
  - 如果off方法不传递任何参数，则将组件上的所有事件全部解绑;

```javascript
	$(function () {
            $("#btn").on("click",function () {
                alert("使用on绑定点击事件");
            });
            
            // $("#btn2").on("click2",function () {  --  不能用此方法解绑！！！
            //     $("#btn").off("click");
            // });
            
            $("#btn2").click(function () {
                $("#btn").off("click");
            });
        })
```

#### 3.3、事件切换：toggle

- **格式：`jq对象.toggle(fn1,fn2...)`**
  - 当单击jq对象对应的组件后，会执行fn1.第二次点击会执行fn2.....
- **注意：1.9版本 .toggle() 方法删除，jQuery Migrate（迁移）插件可以恢复此功能。**

```javascript
<script src="../js/jquery-migrate-1.0.0.js" type="text/javascript" charset="utf-8"></script>
    <script type="text/javascript">
        $(function () {
            $("#btn").toggle(function () {
                $("#myDiv").css("backgroundColor","green");
            },function () {
                $("#myDiv").css("backgroundColor","red");
            });
        });
    </script>
```



## 四、案例

#### 4.1、广告显示和隐藏

```javascript
 	<script>
        $(function () {
            setTimeout(show,3000);
            setTimeout(hide,8000);
        });

        function show() {
            $("#ad").show("slow");
        }

        function hide() {
            $("#ad").hide("slow");
        }
    </script>
```



#### 4.2、抽奖

```javascript
	<script>
		var index;
        $(function () {
            var startID = $("#startID");
            var stopID = $("#stopID");
            var start;
            startID.prop("disabled",false);
            stopID.prop("disabled",true);

            startID.click(function () {
                startID.prop("disabled",true);
                stopID.prop("disabled",false);
                start = setInterval(function () {
                    index = Math.floor(Math.random()*7);
                    $("#img1ID").prop("src",imgs[index]);
                },50);
            });

            stopID.click(function () {
                startID.prop("disabled",false);
                stopID.prop("disabled",true);
                clearInterval(start);
                var img = $("#img2ID");
                img.prop("src",$("#img1ID").prop("src")).hide(); // 可以用index替换
                // 增加平滑效果
                img.show(1000);
            });
        });
    </script>
```



## 五、插件：增强JQuery的功能

#### 实现方式：2种

```javascript
1. $.fn.extend(object)   // 增强通过Jquery获取的对象的功能  $("#id")

    <script type="text/javascript">
        $.fn.extend({
            check:function () {
                this.prop("checked",true);
            },
            uncheck:function () {
                this.prop("checked",false);
            }
        });

        $(function () {
            $("#btn-check").click(function () {
                $("input[type='checkbox']").check();
            });
            $("#btn-uncheck").click(function () {
                $("input[type='checkbox']").uncheck();
            });
        })
    </script>
```

```javascript
2. $.extend(object)   // 增强JQeury对象自身的功能  $/jQuery

    <script type="text/javascript">
        //对全局方法扩展2个方法，扩展min方法：求2个值的最小值；扩展max方法：求2个值最大值
        $.extend({
            max:function (a,b) {
                return a>=b?a:b;
            },
            min:function (a,b) {
                return a<=b?a:b;
            }
        });

        $(function () {
            var nums = $("#nums").html().split(",");
            // var showNums = $("#showNums");
            // var str = "";
            // for(var i=0,length=nums.length;i<length;i++){
            //     str = str+"<h4>"+nums[i]+"</h4>";
            // }
            // showNums.html(str);
            $("#max_btn").click(function () {
                var maxNum = nums[0];
                for(var i=1,length=nums.length;i<length;i++){
                    maxNum = $.max(maxNum,nums[i]);
                }
                $("#show_num").html("最大值："+maxNum);
            });

            $("#min_btn").click(function () {
                var minNum = nums[0];
                for(var i=1,length=nums.length;i<length;i++){
                    minNum = $.min(minNum,nums[i]);
                }
                $("#show_num").html("最小值："+minNum);
            });
        });
    </script>
```















