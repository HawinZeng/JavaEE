# Java 基础（核心卷1）
## 第1章 Java的概述及设计环境
### 1. 特性
#### 1.1 简单性  
> 对比C／C++，没有了头文件，指针运算，结构，联合等；但是相对于Visual Basic可能并不是那么简单（在这里后续可以去了解下Visual Basic）;  
> Java另一方面，支持在小型机器上独立运行，Java微型版（Java Micro Edition）用于嵌入式设备；
  
#### 1.2 面向对象
> 现代语言都采用面向对象，相对于老式C／Basic是面向过程；

#### 1.3 网络技能
> Java有扩展的例程序，处理Http/FTP之类的TCP/IP协议，通过URL即能打开和访问网络上的对象；同时，也可以很轻易使用相关类打开socket连接这类繁重的任务;

#### 1.4 健壮性
> 相对于C/C++，Java采用了指针模型，消除了重写内存饿损坏数据的可能性；Java编译器能检测许多运行时的错误；相对于Visual Basic根本不需要指针访问字符串，数组，对象等，也不需要关心内存分配的等问题，但是VB没有指针，很难实现复杂数据结构，如链表；而Java具有双方的优势；

#### 1.5 安全性
> 防范各种攻击，如蠕虫病毒致使的运行时堆栈溢出，在自己的处理空间之外破坏内存，未经授权读写文件等；采用数字签名类，确定作者信息，是否可以执行操作。同时比微软ActiveX要强多，运行时加以控制并制止恶意性破坏；

#### 1.6 体系结构中立
> 编译器生成一个体系结构中立的目标文件格式，只要有Java运行时系统，就可以在许多处理器上运行；Java编译器生成是与计算机体系结构无关的字节码；这些字节码，容易在任何机器上解释执行，而且还能快速翻译成本地机器的代码；然而，解释字节码比全速运行机器指令要慢得多，怎么办？Java虚拟机有一个选项－－即时编译：可以将使用的字节码序列翻译成机器码；

#### 1.7 可移植性
> Java中的数据类型具有固定的大小，不像C/C++,如int可能是16，也可能是32位；二进制数据以固定的格式进行存储和传输，消除字节顺序的困扰；

#### 1.8 解释型
> 编译型语言：把做好的源程序全部编译成二进制代码的可运行程序。然后，可直接运行这个程序；执行速度快，效率高；依赖编译器，跨平台差些，如C/C++,Delphi,Pascal等；
> 解释型语言：把做好的源程序翻译一句，然后执行一句，直至结束；执行速度慢，效率低；依赖解释器，跨平台好，如Java Basic，C#；
> Java很特殊，java程序也需要编译，但是没有直接编译称为机器语言，而是编译称为字节码，然后用解释方式执行字节码。

#### 1.9 高性能
> 不像其他解释型语言，解释的是字节码，效率要提高；同时，字节码有时还可以快速翻译成运行应用程序的特定CPU的机器码；（内含即时编译）

#### 1.10 多线程
> 多线程编译简单；

#### 1.11 动态性
> 便于扩展，可以讲某些代码添加到正在运行的程序中，而对客户端却没有任何影响；

### 2. 概念理解
1. JDK(Java Development Kit):包含了Java SDK（Software Development Kit）＋ Java运行环境（JRE），即编写java程序时程序员使用的软件；
2. JRE（Java Runtime Environment）:运行Java程序的用户使用的软件，即java程序运行的环境；**当我们在安装JDK时，会发现安装目录下会有两个JRE包，在Java/jdk1.8.0目录下的JRE为专有环境，是本地开发程序运行时的环境，而java/jre1.8为公用环境，其他java程序运行时，调用这个运行环境。两个jre是不同的，不能随便替换，同时平时java自动更新就是更新公用的jre，并非更新jdk，因为更新公用jre，当其他java 程序运行就不会出现异常。若要更新jdk，只能到官方下载，重新安装咯。**
3. NetBeans：Oracle的集成开发环境。一般也可以使用eclipse & MyEclipse来开发；

### 3. 环境配置
1. JAVA_HOME＝C:\Program Files\Java\jdk1.7.0，这个替代品，简化Classpath,Path的配置；
2. Classpath=.;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\lib\tools.jar; 目的是为了程序能找到相应的“.class”文件；**前面"."代表在当前目录下执行java程序，这也是Linux安全机制引起的。记住编译时可以不在当前目录下，但是运行一定要.class的当前目录，否则就会java.lang.NoClassDefFoundError ‘［错误: 找不到或无法加载主类 .Volumes.D.JavaTest.WelcomeJava.class］’**；
3. Path＝%JAVA_HOME%\bin;%JAVA_HOME%\jre\bin;配置这个，主要是为在任何目录下能使用bin目录中的命令，如java命令；jre\bin一般可以不用配置，其中的命令使用也少；

Windows下配置：同上，JAVA_HOME，Classpath是新建变量，Path将跟随到系统path变量即可；
Linux／Unix配置：打开终端－－>open ~/.bashrc或者~/.bash_profile(在这个文件下配置环境变量)－－> 

	JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_25.jdk/Contents/Home
	PATH=$PATH:$JAVA_HOME/bin
	CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
	export JAVA_HOME
	export PATH
	export CLASSPATH
























