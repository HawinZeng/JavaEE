# 01_Eclipse

## 一、安装／卸载

Eclipse 是一个开放源代码的、基于Java的可扩展开发平台。就其本身而言，它只是一个框架和一组服务，用于通过插件组件构建开发环境。幸运的是，Eclipse 附带了一个标准的插件集，包括Java开发工具（Java Development Kit，JDK）。

下载：［ 4.7 version／oxygen ]

### [JavaEE: Eclipse IDE for Java EE Developers](https://www.eclipse.org/downloads/packages/release/oxygen/3a/eclipse-ide-java-ee-developers)

### [JavaSE: Eclipse IDE for Java Developers](https://www.eclipse.org/downloads/packages/release/oxygen/3a/eclipse-ide-java-developers)

由于都是绿色版：解压即安装，删除即卸载！

## 二、配置

- ##### 工作空间workspace：

  /Users/iMac/eclipse-workspace

- ##### 字符集设置：

  preferences -->general/workspace --> 若是其他编码，如GBK，一般都修改设置为utf-8。

- ##### 代码补全：输入任务字母都有代码提示补全

  preferences-->java/editor/content Assist--> auto activation triggers for java:.abcdefg...

- ##### jdk集成：

  preferences--> java/installed JREs -->add/standard VM--> 选择jdk路径

  ```
  /Library/Java/JavaVirtualMachines/jdk1.8.0_25.jdk/Contents/Home
  ```

- ##### tomcat集成：

  1、preferences-->server/runtime Environments-->首先：选择tomcat版本名称-->然后：选择对应版本的安装路径-->再：选择对应jre版本；

  2、window-->showView/servers--> 选择相应的tomcat版本／安装路径／jre版本-->next选择工程项目-->finish. 

  此时：servers状态栏出现，选中即可在右侧启动／debug也可以！

  3、端口号修改：双击servers对应tomcat，修改http 1.1对应的端口号即可！

- ##### maven集成：

  1、 preferences-->maven/User Settings-->选择：maven安装路径/ conf/settings.xml。

  此时，会自动关联本地仓库，若正确即可不需要修改“Local Repository”。

  2、window-->showView/Others-->搜索maven -->Maven Repository 即可看到本地仓库。

  但此时，还没有关联到本地库，需要右击“local repository” --> rebuild index. 即可查看本地库的所有jar。



## 三、创建工程项目

#### 1、 创建JavaSE工程： 

此时发现没有创建入口，咋办？eclipse 右上角的左侧“＋”，点击后，弹出工程类型选择，我们选择java，即是javaSE工程创建窗口了。

选中后，右上角会多出一个javaSE选择。由此我们可以切换javaEE／javaSE工程窗口，创建不同项目。

#### 2、如何将javaSE视图固定在左侧？

当javaEE开发时，project Explorer 显示结构比较混乱。我们需要以javaSE视图窗口查看时，window-->showView 搜索package Explorer 选择即可--> 拉动到左侧栏即可！

#### 3、eclipse配置schema离线约束：

#### 4、修改项目jre版本和编译版本：（三个jdk版本一致）

右击项目：propertites即可查看下面三个，同时设置！

- JRE System Library :  选择workspace同版本的jdk 1.8

- Java Complier：同JRE一致

- project Facet：java 1.8

  ```java
  Facet 定义了 java ee 项目的特性和要求
  1. 为项目添加 EAR facet 会自动添加 web.xml (deployment descriptor file) ，并重新设置classpath；
  2. 项目创建时至少已经有一个 facet，开发人员可以按需添加其他 facet；
  3. 有的 facet 会依赖其他 facet、有的 facet 可能和其他 facet 互斥、facet 可以设置版本；
  4、项目需要哪些应用，添加对应的特性即可。
  ```



#### 5、maven项目创建：父子工程

#### 6、eclipse集成svn：



## 四，错误总结

error1: 

```
maven install : invalid CEN header (bad signature)

一般是加载对应依赖出错。
解决：找到对应的本地库位置，删除对应的依赖jar的package，然后重新install，出现重新下载，即可！
```










