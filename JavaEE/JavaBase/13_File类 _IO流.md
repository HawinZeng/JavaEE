# 第十三节  File类 、IO流

##一、File类

###1.1、 概述

`java.io.File` 类是文件和目录路径名的抽象表示，主要用于文件和目录的创建、查找和删除等操作。

- File类是一个与系统无关的类,任何的操作系统都可以使用这个类中的方法

- 重点:记住这三个单词
  ​    file:文件
  ​    directory:文件夹/目录
  ​    path:路径

- 两个static字符串说明：

  ```java
  static String pathSeparator (:)与系统有关的路径分隔符，为了方便，它被表示为一个字符串。
  static char pathSeparatorChar 与系统有关的路径分隔符。
  
  static String separator (\ or /)与系统有关的默认名称分隔符，为了方便，它被表示为一个字符串。
  static char separatorChar 与系统有关的默认名称分隔符。
  
  操作路径:路径不能写死了
  C:\develop\a\a.txt  windows
  C:/develop/a/a.txt  linux
  "C:"+File.separator+"develop"+File.separator+"a"+File.separator+"a.txt"
  ```

- 路径:
  ​    绝对路径:是一个完整的路径
  ​        以盘符(c:,D:)开始的路径
  ​            c:\\a.txt
  ​            C:\\Users\itcast\\IdeaProjects\\shungyuan\\123.txt
  ​    相对路径:是一个简化的路径
  ​        相对指的是相对于当前项目的根目录(C:\\Users\itcast\\IdeaProjects\\shungyuan)
  ​        如果使用当前项目的根目录,路径可以简化书写
  ​        C:\\Users\itcast\\IdeaProjects\\shungyuan\\123.txt-->简化为: 123.txt(可以省略项目的根目录)
  ​    注意:
  ​        1.路径是不区分大小写
  ​        2.路径中的文件名称分隔符windows使用反斜杠,反斜杠是转义字符,两个反斜杠代表一个普通的反斜杠

###1.2、 File类的使用

1. **构造方法**

- `public File(String pathname) ` ：通过将给定的**路径名字符串**转换为抽象路径名来创建新的 File实例。  
- `public File(String parent, String child) ` ：从**父路径名字符串和子路径名字符串**创建新的 File实例。
- `public File(File parent, String child)` ：从**父抽象路径名和子路径名字符串**创建新的 File实例。  

```java
// 文件路径名
String pathname = "D:\\aaa.txt";
File file1 = new File(pathname); 

// 文件路径名
String pathname2 = "D:\\aaa\\bbb.txt";
File file2 = new File(pathname2); 

// 通过父路径和子路径字符串
 String parent = "d:\\aaa";
 String child = "bbb.txt";
 File file3 = new File(parent, child);

// 通过父级File对象和子路径字符串
File parentDir = new File("d:\\aaa");
String child = "bbb.txt";
File file4 = new File(parentDir, child);
```

> 小贴士：
>
> 1. 一个File对象代表硬盘中实际存在的一个文件或者目录。
> 2. 无论该路径下是否存在文件或者目录，都不影响File对象的创建。
> 3. 请注意，构造创建的文件是在当时的内存中，还并未写入磁盘中，若文件开始没有，我们是看不到该文件的。必须通过IO流写入信息或着调用createNewFile()强制创建；

2. **常用方法**

- 获取功能方法：

  - `public String getAbsolutePath() ` ：返回此File的绝对路径名字符串。
  - ` public String getPath() ` ：将此File转换为路径名字符串。 
  - `public String getName()`  ：返回由此File表示的文件或目录的名称。  
  - `public long length()`  ：返回由此File表示的文件的长度。 

- 判断功能的方法

  - `public boolean createNewFile()` ：当且仅当具有该名称的文件尚不存在时，创建一个新的空文件。 
  - `public boolean delete()` ：删除由此File表示的文件或目录。  
  - `public boolean mkdir()` ：创建由此File表示的目录。
  - `public boolean mkdirs()` ：创建由此File表示的目录，包括任何必需但不存在的父目录。

- 创建删除功能的方法

  - `public String[] list()` ：返回一个String数组，表示该File目录中的所有子文件或目录。

  - `public File[] listFiles()` ：返回一个File数组，表示该File目录中的所有的子文件或目录。  







