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



