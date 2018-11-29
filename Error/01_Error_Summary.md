# 01 Error Summary

**Issue-01:**

```
'while' statement cannot complete without throwing an exception.

Reports for, while, or do statements which can only exit by throwing an exception. While such statements may be correct, they are often a symptom of coding errors.
```

> If so**,** you **can** add this above your method containing the infinite loop to suppress warnings: 
>
> 无限循环引发的可能性错误，压制warning即可！@SuppressWarnings("InfiniteLoopStatement")
>
> infinite:无限的  suppress:压制



**Issue-02:**

```java
Unchecked call to 'getConstructor(Class<?>...)' as a member of raw type 'java.lang.Class' less... (⌘F1) 
Inspection info: Signals places where an unchecked warning is issued by the compiler, for example:
-----------------------------------------------------------------------------------------
        Class cls = Class.forName(className);
        Constructor con= cls.getConstructor(String.class);
        Method method = cls.getMethod(methodName);
```

> Firstly this is just **a warning and should not cause you undue alarm when working with reflection and types that are unknown at compile time**. The virtue of generics is stronger compile time type checking and **all that goes out the window once you call Class.forName(className).**
>
> `Class<?> cls = Class.forName(className);`,solve this issue!



**issue-03:**

```
ERROR 1820 (HY000): You must reset your password using ALTER USER statement before executing this statement.
```

> 1、 修改用户密码
> mysql> alter user 'root'@'localhost' identified by 'youpassword';  
>
> 或者       
>
> mysql> set password=password("youpassword");
>
> 2、刷新权限
>
> mysql> flush privileges;



**issue-04:**

```java
'catch' branch identical to 'ClassNotFoundException' branch less... 

Inspection info: Reports identical catch sections in try blocks under JDK 7. A quickfix is available to collapse the sections into a multi-catch section.
This inspection only reports if the project or module is configured to use a language level of 7.0 or higher.
    
    try {
            Properties pro = new Properties();
            ClassLoader classLoader = JDBCUtils.class.getClassLoader();
            URL resource = classLoader.getResource("jdbc.properties");
            assert resource != null;
            String path1 = resource.getPath();
            System.out.println(path1);
            pro.load(new FileReader(path1));
            url = pro.getProperty("url");
            user = pro.getProperty("user");
            password = pro.getProperty("password");
            Class.forName(pro.getProperty("driver"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) { // ISSUE point
            e.printStackTrace();
        }
```

> Resolution:
>
> the same warning in IntelliJ (and I think you're using IntelliJ too), why not let Alt+Enter (or Option+Return if you rather) show you what it means?
>
> ```java
> try {
> 	...
> }catch(ClassNotFoundException | IOException e){
>       e.printStackTrace();
> }
> ```



**issue-05:**

```
Intellij idea 出现错误 error:java: 无效的源发行版: 11解决方法!!
```

> Select the project, then File > ProjectStructure > ProjectSettings > Modules -> sources You probably have the Language Level set at 8: 根据当前jdk版本而定！！！