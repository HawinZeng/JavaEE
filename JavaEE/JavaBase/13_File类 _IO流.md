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

> **new一个文件，有两种情况：**
>
> 1. 文件已存在硬盘，那么对内存的该文件对象系列操作，就不会有问题；
>
> 2. ##### 文件不存在硬盘，其实际是在内存中，那就不要轻易操作该文件，尽管自身不会引发nullPointerException。但是通过该文件，得到的对象一定是null对象！
>
> ##### 故，在操作文件前，一定要判断文件是否存在硬盘中，exist()方法；

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
  - `public String getCanonicalPath()`：返回File的标准路径名字符串；
  - ` public String getPath() ` ：将此File转换为路径名字符串。 
  - `public String getName()`  ：返回由此File表示的文件或目录的名称。  
  - `public long length()`  ：返回由此File表示的文件的长度。 

  1. 

- 判断功能的方法

  - `public boolean createNewFile()` ：当且仅当具有该名称的文件尚不存在时，创建一个新的空文件。 
  - `public boolean delete()` ：删除由此File表示的文件或目录。  
  - `public boolean mkdir()` ：创建由此File表示的目录。
  - `public boolean mkdirs()` ：创建由此File表示的目录，包括任何必需但不存在的父目录。

- 创建删除功能的方法

  - `public String[] list()` ：返回一个String数组，表示该File目录中的所有子文件或目录。
  - `public File[] listFiles()` ：返回一个File数组，表示该File目录中的所有的子文件或目录。  

3. #### 举例重点：

- 在当前目录下new 一个文件

```
// 1. 直接文件，非文件夹
File file = new File("mm.txt");
System.out.println(file.getAbsolutePath());// .../day11/02_代码/day11-code/mm.txt
System.out.println(file.getCanonicalPath());// .../day11/02_代码/day11-code/mm.txt
System.out.println(file.getPath()); // mm.txt
System.out.println(file.getName());// mm.txt
if(!file.exists()){
    file.mkdir(); // 把当前文件当成一个directory创建，不创建上级目录，也就是父级目录不存在，此法无效；
    file.mkdirs();// 与上面方法一致，它还同时把父级目录一并创建。开发时，常用此法，保证文件目录必创建；
    createNewFile();// 才是创建该文件，而不是directory；是文件一定要用此法；
}

// 2. 错误写法，带文件夹的文件，但是父级文件夹不存在。new就疯了。
File file = new File("/test/mm.txt"); 
System.out.println(file.getAbsolutePath());//  /test/mm.txt
System.out.println(file.getCanonicalPath());// /test/mm.txt
System.out.println(file.getPath()); // /test/mm.txt
file.mkdirs(); // 可以成功创建，是在当前目录下创建一个 test／mm.txt的非正常的文件夹，一般情况是打不开，将后缀去掉，才可以打开；

// 3. 正确写法，带文件夹的文件，但是父级文件夹不存在。
File file = new File("test/mm.txt"); 
System.out.println(file.getAbsolutePath());//  ../day11/02_代码/day11-code//test/mm.txt
System.out.println(file.getCanonicalPath());// ../day11/02_代码/day11-code//test/mm.txt
System.out.println(file.getPath()); // test/mm.txt
System.out.println(file.getName());// mm.txt --- 这个依然不变
```

> **getAbsolutePath vs getCanonicalPath 好像没有区别；其实有区别，参考如下：**
>
> File directory = new File("."); 
> directory.getCanonicalPath(); //得到的是C:/test 
> directory.getAbsolutePath();    //得到的是C:/test/. 
> direcotry.getPath();                    //得到的是. 
>
> File directory = new File(".."); 
> directory.getCanonicalPath(); //得到的是C:/ 
> directory.getAbsolutePath();    //得到的是C:/test/.. 
> direcotry.getPath();                    //得到的是.. 

- new 任意一个文件，非当前目录下 －－－－> 一定是绝对路径**



## 二、递归

###2.1 概述

- **递归**：指在当前方法内调用自己的这种现象。
- **递归的分类:**
  - 递归分为两种，直接递归和间接递归。
  - 直接递归称为方法自身调用自己。
  - 间接递归可以A方法调用B方法，B方法调用C方法，C方法调用A方法。
- **注意事项**：
  - 递归一定要有条件限定，保证递归能够停止下来，否则会发生栈内存溢出。
  - 在递归中虽然有限定条件，但是递归次数不能太多。否则也会发生栈内存溢出。
  - 构造方法,禁止递归

#### 2.2、递归打印文件夹下多级子目录

```java
    static void getAllFile(File dir){ //传入的肯定是目录
        File[] fileList = dir.listFiles(); // 若目录不存在，则fileList是null
        if(fileList == null || fileList.length == 0){
            return;
        }
        for(File f:fileList){
            if(f.isDirectory()) {
                System.out.println(f.getAbsoluteFile());
                getAllFile(f);
            }else{
                System.out.println(f.getAbsoluteFile());
            }
        }
    }
```

## 三、文件搜索

### 3.1、文件搜索

搜索目标目录下以`.java`结尾的文件！

```java
    static void printDir(File file) {
        String pathName = file.getAbsolutePath();
        if (pathName.endsWith(".java")) {
            System.out.println(pathName);
            return;
        }

        if (file.isFile()) return;

        File[] fileList = file.listFiles();
        if (null != fileList) {
            for (File f : fileList) {
                printDir(f);
            }
        }
    }
```

### 3.2、文件过滤器

`java.io.FileFilter`是一个接口，是File的过滤器。 该接口的对象可以传递给File类的`listFiles(FileFilter)` 作为参数， 接口中只有一个方法。

`boolean accept(File pathname)  ` ：测试pathname是否应该包含在当前File目录中，符合则返回true。

```java
static void printDir(File file){
		File[] files = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory()||pathname.getAbsolutePath().endsWith(".java");
			}
		});

		for(File f:files){
			if(f.isFile()){
				System.out.println(f);
			}else{
				printDir(f);
			}
		}
	}
```

> `java.io.FilenameFilter`也是一个过滤器，File dir是父类文件夹，String name是当前文件的相对名称；



### 3.3、Lambda优化

```java
static void printDir(File file){
		File[] files = file.listFiles(pathname -> pathname.isDirectory()||
				pathname.getAbsolutePath().endsWith(".java"));
				
		for(File f:files){
			if(f.isFile()){
				System.out.println(f);
			}else{
				printDir(f);
			}
		}
	}
```

## 四、IO流

### 4.1、 什么是IO？

拷贝到你的电脑硬盘里。那么数据都是在哪些设备上的呢？键盘、内存、硬盘、外接设备等等。

我们把这种数据的传输，可以看做是一种数据的流动，按照流动的方向，以内存为基准，分为`输入input` 和`输出output` ，即流向内存是输入流，流出内存的输出流。

Java中I/O操作主要是指使用`java.io`包下的内容，进行输入、输出操作。**输入**也叫做**读取**数据，**输出**也叫做作**写出**数据。

### 4.2、 IO的分类

根据数据的流向分为：**输入流**和**输出流**。

- **输入流** ：把数据从`其他设备`上读取到`内存`中的流。 
- **输出流** ：把数据从`内存` 中写出到`其他设备`上的流。

格局数据的类型分为：**字节流**和**字符流**。

- **字节流** ：以字节为单位，读写数据的流。
- **字符流** ：以字符为单位，读写数据的流。

### 4.3、 顶级父类们

|    类型    |           **输入流**            |              输出流              |
| :--------: | :-----------------------------: | :------------------------------: |
| **字节流** | 字节输入流<br />**InputStream** | 字节输出流<br />**OutputStream** |
| **字符流** |   字符输入流<br />**Reader**    |    字符输出流<br />**Writer**    |

## 五、字节流

###  一切皆为字节

一切文件数据(文本、图片、视频等)在存储时，都是以二进制数字的形式保存，都一个一个的字节，那么传输时一样如此。所以，字节流可以传输任意文件数据。在操作流的时候，我们要时刻明确，无论使用什么样的流对象，底层传输的始终为二进制数据。

## （一） 字节输出流【OutputStream】

`java.io.OutputStream `抽象类是表示字节输出流的所有类的超类，将指定的字节信息写出到目的地。它定义了字节输出流的基本共性功能方法。

- `public void close()` ：关闭此输出流并释放与此流相关联的任何系统资源。  
- `public void flush() ` ：刷新此输出流并强制任何缓冲的输出字节被写出。  
- `public void write(byte[] b)`：将 b.length字节从指定的字节数组写入此输出流。  
- `public void write(byte[] b, int off, int len)` ：从指定的字节数组写入 len字节，从偏移量 off开始输出到此输出流。  
- `public abstract void write(int b)` ：将指定的字节输出流。

> 小贴士：
>
> close方法，当完成流的操作时，必须调用此方法，释放系统资源。

#### 1、FileOutputStream类

`OutputStream`有很多子类，我们从最简单的一个子类开始。

`java.io.FileOutputStream `类是文件输出流，用于将数据写出到文件。

#### 1.1、构造方法

- `public FileOutputStream(File file)`：创建文件输出流以写入由指定的 File对象表示的文件。 
- `public FileOutputStream(String name)`： 创建文件输出流以指定的名称写入文件。  

当你创建一个流对象时，必须传入一个文件路径。该路径下，如果没有这个文件，会创建该文件。如果有这个文件，会清空这个文件的数据。

>- **写入数据的原理**(内存-->硬盘)
>
>​    java程序-->JVM(java虚拟机)-->OS(操作系统)-->OS调用IO写数据的方法-->把数据写入到文件中
>
>- **字节输出流的使用步骤**(重点):
>
>​    1.创建一个FileOutputStream对象,构造方法中传递写入数据的目的地
>​    2.调用FileOutputStream对象中的方法write,把数据写入到文件中
>​    3.释放资源(流使用会占用一定的内存,使用完毕要把内存清空,提供程序的效率)

#### 1.2、写出字节：

- `write(int b)` 方法，每次可以写出一个字节数据；

> fos.write(97); 在磁盘文件显示的是一个a；当b=0~127，查询ASCII码表，97对应a；当大于127时，则依据系统码表查询，若是简体中文，则是GBK码表；

- **写出字节数组**：`write(byte[] b)`，每次可以写出数组中的数据；

> 一次写多个字节:   
>
> 1.  如果写的第一个字节是正数(0-127),那么显示的时候会查询ASCII表    
> 2. 如果写的第一个字节是负数,那第一个字节会和第二个字节,两个字节组成一个中文显示,查询系统默认码表(GBK)
> 3. utf-8模式，中文是三个负数组合。若IDE工具是utf-8，发现代码能很好的组合三个负数的组合，则会输出一个utf-8的文档，即便原来是gbk-ASCII编码的。若不能很好组合，则用系统的默认的gbk-ASCII编码的;

- **写出指定长度字节数组**：`write(byte[] b, int off, int len)` ,每次写出从off索引开始，len个字节;

#### 1.3、数据追加

- `public FileOutputStream(File file, boolean append)`： 创建文件输出流以写入由指定的 File对象表示的文件。  
- `public FileOutputStream(String name, boolean append)`： 创建文件输出流以指定的名称写入文件。  

这两个构造方法，参数中都需要传入一个boolean类型的值，`true` 表示追加数据，`false` 表示清空原有数据。这样创建的输出流对象，就可以指定是否追加续写了；

#### 1.4、写出换行

- 回车符`\r`和换行符`\n` ：
  - 回车符：回到一行的开头（return）。
  - 换行符：下一行（newline）。
- 系统中的换行：
  - Windows系统里，每行结尾是 `回车+换行` ，即`\r\n`；
  - Unix系统里，每行结尾只有 `换行` ，即`\n`；
  - Mac系统里，每行结尾是 `回车` ，即`\r`。从 Mac OS X开始与Linux统一。

```java
 public static void main(String[] args) throws IOException {
        FileOutputStream fos = new FileOutputStream("09_IOAndProperties\\c.txt",true);
        for (int i = 1; i <=10 ; i++) {
            fos.write("你好".getBytes());
            fos.write("\r\n".getBytes());
        }
        fos.close();
    }
```



## （二）、字节输入流【InputStream】

`java.io.InputStream `抽象类是表示字节输入流的所有类的超类，可以读取字节信息到内存中。它定义了字节输入流的基本共性功能方法。

- `public void close()` ：关闭此输入流并释放与此流相关联的任何系统资源。    
- `public abstract int read()`： 从输入流读取数据的下一个字节。 
- `public int read(byte[] b)`： 从输入流中读取一些字节数，并将它们存储到字节数组 b中 。

> 小贴士：
>
> close方法，当完成流的操作时，必须调用此方法，释放系统资源。

### 1.FileInputStream类

`java.io.FileInputStream `类是文件输入流，从文件中读取字节。

#### 1.1、构造方法

- `FileInputStream(File file)`： 通过打开与实际文件的连接来创建一个 FileInputStream ，该文件由文件系统中的 File对象 file命名。 
- `FileInputStream(String name)`： 通过打开与实际文件的连接来创建一个 FileInputStream ，该文件由文件系统中的路径名 name命名。  

当你创建一个流对象时，必须传入一个文件路径。该路径下，如果没有该文件,会抛出`FileNotFoundException` 。

#### 1.2、读取字节数据

- int read(): 单个读取

- int read(btye[] b)：多个读取

  > 1.方法的参数byte[]的作用?
  > ​    起到缓冲作用,存储每次读取到的多个字节
  > ​    数组的长度一把定义为1024(1kb)或者1024的整数倍
  > 2.方法的返回值int是什么?
  > ​    每次读取的有效字节个数

- 最后一个字节为-1，即fis.read()==-1,

```java
 public static void main(String[] args) throws IOException{
      	// 使用文件名称创建流对象.
       	FileInputStream fis = new FileInputStream("read.txt"); // 文件中为abcde
      	// 定义变量，作为读取字节的有效个数
        int len ；
        // 定义字节数组，作为装字节数据的容器   
        byte[] b = new byte[2];
        // 循环读取
        while (( len= fis.read(b))!=-1) {
            // 每次读取后,把数组变成字符串打印
            // System.out.println(new String(b)); -- 若不规定长度，则会把最后一个非法缓冲也读进来
           	// 每次读取后,把数组的有效字节部分，变成字符串打印
            System.out.println(new String(b，0，len));//  len 每次读取的有效字节个数
        }
		// 关闭资源
        fis.close();
    }
```

复制图片文件，代码使用演示：

```java
public class Copy {
    public static void main(String[] args) throws IOException {
        // 1.创建流对象
        // 1.1 指定数据源
        FileInputStream fis = new FileInputStream("D:\\test.jpg");
        // 1.2 指定目的地
        FileOutputStream fos = new FileOutputStream("test_copy.jpg");

        // 2.读写数据
        // 2.1 定义数组
        byte[] b = new byte[1024];
        // 2.2 定义长度
        int len;
        // 2.3 循环读取
        while ((len = fis.read(b))!=-1) {
            // 2.4 写出数据
            fos.write(b, 0 , len);
        }

        // 3.关闭资源
        fos.close();
        fis.close();
    }
}
```

> 小贴士：
>
> **流的关闭原则：先开后关，后开先关**。



## 六、字符流

当使用字节流读取文本文件时，可能会有一个小问题。就是遇到中文字符时，可能不会显示完整的字符，那是因为一个中文字符可能占用多个字节存储。所以Java提供一些字符流类，以字符为单位读写数据，专门用于处理文本文件。

> utf-8模式下，每次读取1/3个中文，所以造成乱码！

## （一） 字符输入流【Reader】

`java.io.Reader`抽象类是表示用于读取字符流的所有类的超类，可以读取字符信息到内存中。它定义了字符输入流的基本共性功能方法。

- `public void close()` ：关闭此流并释放与此流相关联的任何系统资源。    
- `public int read()`： 从输入流读取一个字符。 
- `public int read(char[] cbuf)`： 从输入流中读取一些字符，并将它们存储到字符数组 cbuf中 。

###  1、FileReader类

继承关系：`java.io.FileReader extends InputStreamReader extends Reader`

**`FileReader `**类是读取字符文件的便利类。构造时使用系统默认的字符编码和默认字节缓冲区。

> 小贴士：
>
> 1. 字符编码：字节与字符的对应规则。Windows系统的中文编码默认是GBK编码表。
>
>    idea中UTF-8
>
> 2. 字节缓冲区：一个字节数组，用来临时存储字节数据。

#### 1.1、构造方法

- `FileReader(File file)`： 创建一个新的 FileReader ，给定要读取的File对象。   
- `FileReader(String fileName)`： 创建一个新的 FileReader ，给定要读取的文件的名称。  

当你创建一个流对象时，必须传入一个文件路径。类似于FileInputStream 。

#### 1.2、读取字符数据

1. **读取字符**：`read`方法，每次可以读取一个字符的数据，提升为int类型，读取到文件末尾，返回`-1`，
2. **使用字符数组读取**：`read(char[] cbuf)`，每次读取b的长度个字符到数组中，返回读取到的有效字符个数，读取到末尾时，返回`-1` ，

```java
public class FISRead {
    public static void main(String[] args) throws IOException {
      	// 使用文件名称创建流对象
       	FileReader fr = new FileReader("read.txt");
      	// 定义变量，保存有效字符个数
        int len ；
        // 定义字符数组，作为装字符数据的容器
        char[] cbuf = new char[2];
        // 循环读取
        while ((len = fr.read(cbuf))!=-1) {
            System.out.println(new String(cbuf,0,len));
        }
    	// 关闭资源
        fr.close();
    }
}
```



## （二）字符输出流【Writer】

`java.io.Writer `抽象类是表示用于写出字符流的所有类的超类，将指定的字符信息写出到目的地。它定义了字节输出流的基本共性功能方法。

- `void write(int c)` 写入单个字符。
- `void write(char[] cbuf) `写入字符数组。 
- `abstract  void write(char[] cbuf, int off, int len) `写入字符数组的某一部分,off数组的开始索引,len写的字符个数。 
- `void write(String str) `写入字符串。 
- `void write(String str, int off, int len)` 写入字符串的某一部分,off字符串的开始索引,len写的字符个数。
- `void flush() `刷新该流的缓冲。  
- `void close()` 关闭此流，但要先刷新它。 

### 1、FileWriter类

`java.io.FileWriter `类是写出字符到文件的便利类。构造时使用系统默认的字符编码和默认字节缓冲区。

#### 1.1、构造方法

- `FileWriter(File file)`： 创建一个新的 FileWriter，给定要读取的File对象。   
- `FileWriter(String fileName)`： 创建一个新的 FileWriter，给定要读取的文件的名称。  

当你创建一个流对象时，必须传入一个文件路径，类似于FileOutputStream。

#### 1.2、字符输出流的使用步骤(重点):    

1. 创建FileWriter对象,构造方法中绑定要写入数据的目的地   
2.  使用FileWriter中的方法write,把数据写入到内存缓冲区中(字符转换为字节的过程)    
3. 使用FileWriter中的方法flush,把内存缓冲区中的数据,刷新到文件中    
4. 释放资源(会先把内存缓冲区中的数据刷新到文件中)

#### 1.3、关闭close和刷新flush区别

因为内置缓冲区的原因，如果不关闭输出流，无法写出字符到文件中。但是关闭的流对象，是无法继续写出数据的。如果我们既想写出数据，又想继续使用流，就需要`flush` 方法了。

- `flush` ：刷新缓冲区，流对象可以继续使用。
- `close `:先刷新缓冲区，然后通知系统释放资源。流对象不可以再被使用了。

#### 1.4、写出数据

- **写出字符：**`write(int b)` 方法，每次可以写出一个字符数据；
- **写出字符数组：** `write(char[] cbuf)` 和 `write(char[] cbuf, int off, int len)` ，每次可以写出字符数组中的数据，用法类似FileOutputStream，
- **写出字符串**：`write(String str)` 和 `write(String str, int off, int len)` ，每次可以写出字符串中的数据，更为方便；

```java
public class FWWrite {
    public static void main(String[] args) throws IOException {
        // 使用文件名称创建流对象
        FileWriter fw = new FileWriter("fw.txt");     
      	// 字符串
      	String msg = "黑马程序员";
      
      	// 写出字符数组
      	fw.write(msg); //黑马程序员
      
		// 写出从索引2开始，2个字节。索引2是'程'，两个字节，也就是'程序'。
        fw.write(msg,2,2);	// 程序
      	
        // 关闭资源
        fos.close();
    }
}
```

- **续写和换行**：操作类似于FileOutputStream。

> 小贴士：字符流，只能操作文本文件，不能操作图片，视频等非文本文件。
>
> 当我们单纯读或者写文本文件时  使用字符流 其他情况使用字节流



## 七、IO异常的处理

### 1、JDK7前处理

```java
public class HandleException1 {
    public static void main(String[] args) {
      	// 声明变量
        FileWriter fw = null;
        try {
            //创建流对象
            fw = new FileWriter("fw.txt");
            // 写出数据
            fw.write("黑马程序员"); //黑马程序员
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

### 2、JDK7的处理(扩展知识点了解内容)

还可以使用JDK7优化后的`try-with-resource` 语句，该语句确保了每个资源在语句结束时关闭。所谓的资源（resource）是指在程序完成后，会自动把流对象释放，无需手动close。

**why? JDK 7 流的超类都实现了AutoClosable, 所以在很多情况下，都可以自动关闭流，释放资源！**

格式：

```java
try (创建流对象语句，如果多个,使用';'隔开) {
	// 读写数据
} catch (IOException e) {
	e.printStackTrace();
}
```

代码使用演示：

```java
public class HandleException2 {
    public static void main(String[] args) {
      	// 创建流对象
        try ( FileWriter fw = new FileWriter("fw.txt"); ) { // 这段代码优化
            // 写出数据
            fw.write("黑马程序员"); //黑马程序员
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### 3、JDK9的改进(扩展知识点了解内容)

JDK9中`try-with-resource` 的改进，对于**引入对象**的方式，支持的更加简洁。被引入的对象，同样可以自动关闭，无需手动close，我们来了解一下格式。

改进格式：

```java
// 被final修饰的对象、【普通对象也行】
final Resource resource1 = new Resource("resource1");
// 普通对象
Resource resource2 = new Resource("resource2");
// 引入方式：创建新的变量保存
try (r1 ; r2 ) {
     // 使用对象
}
```

改进后Demo：

```java
public class TryDemo {
    public static void main(String[] args) throws IOException {
       	// 创建流对象
        final  FileReader fr  = new FileReader("in.txt");
        FileWriter fw = new FileWriter("out.txt");
       	// 引入到try中
        try (fr; fw) {
          	// 定义变量
            int b;
          	// 读取数据
          	while ((b = fr.read())!=-1) {
            	// 写出数据
            	fw.write(b);
          	}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

> **JDK9 的改进不太科学，既要try...catch..., 还得throws IOException;**