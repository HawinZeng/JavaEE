# 品优_扩展  MongoDB

## 一、安装及配置

### 1.1、简介及安装

​	MongoDB 是一个跨平台的，面向文档的数据库，是当前 NoSQL 数据库产品中最热门的一种。它介于关系数据库和非关系数据库之间，是非关系数据库当中功能最丰富，最像关系数据库的产品。它支持的数据结构非常松散，是类似JSON  的 BSON 格式，因此可以存储比较复杂的数据类型。

MongoDB 的官方网站地址是：<http://www.mongodb.org/> 下载对应的安装包，也可以下载纯绿色版。

若下载msi安装包，下载后，就bin目录配置到环境变量中。



### 1.2、启动服务

- ##### 第一步，任意新建一个文件夹，如 d:\MongoDB\data

- ##### 第二步：使用mongod.exe命令 ， 指定数据保存到上述目录

  ```shell
  $ C:\Users\hp>mongod --dbpath=D:\MongoDB\data
  
  # 2019-04-03T17:22:06.141+0800 I CONTROL  [initandlisten] MongoDB starting : pid=6300 
  ...
  2019-04-03T17:22:06.147+0800 I CONTROL  [initandlisten] options: { storage: { dbPath: "D:\MongoDB\data" } }
  ...
  data capture with directory 'D:/MongoDB/data/diagnostic.data'
  2019-04-03T17:22:06.306+0800 I NETWORK  [initandlisten] waiting for connections on port 27017
  2019-04-03T17:22:37.619+0800 I NETWORK  [initandlisten] connection accepted from 127.0.0.1:50016 #1 (1 connection now open)
  ```

  ```shell
  # 默认端口：27017 若不想使用该端口
  mongod --port 12306 --dbpath D:\MongoDB\data
  ```

  

### 1.3、登录系统

令开启cmd， 输入mongo.exe命令

```shell
$ C:\Users\hp>mongo
# MongoDB shell version: 3.2.10
# connecting to: test
```

```shell
$ mongo 192.168.25.128  # 访问远程默认端口
$ mongo 192.168.25.128:12306  # 访问远程mongodb, 非默认端口
```



### 1.4、连接mongodb

##### 方式一：mongodb的使用方式是客户服务器模式，即使用一个客户端连接mongodb数据库（服务端）

```shell
$ mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?
options]]

mongodb:// 固定前缀
username：账号，可不填
password：密码，可不填
host：主机名或ip地址，只有host主机名为必填项。
port：端口，可不填，默认27017
/database：连接某一个数据库
?options：连接参数，key/value对

# 例子
mongodb://localhost 连接本地数据库27017端口 
mongodb://root:itcast@localhost 使用用户名root密码为itcast连接本地数据库27017端口 
mongodb://localhost,localhost:27018,localhost:27019，连接三台主从服务器，端口为27017、27018、27019
```

##### 方式二：使用mongodb自带的javascript shell（mongo.exe）连接

双击bin/mongo.exe即可（windows）

若是其他系统，则可以在命令行直接运行 mongo即可！（注意配置好系统变量！）

##### 方式三：使用studio3T连接

##### 方式四：使用java程序连接

- 依赖

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongo‐java‐driver</artifactId>
    <version>3.4.3</version>
</dependency>
```

- 代码

```java
@Test
public void testConnection(){
    //创建mongodb 客户端
    MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
    //或者采用连接字符串
    //MongoClientURI connectionString = new    
    MongoClientURI("mongodb://root:root@localhost:27017");

    //MongoClient mongoClient = new MongoClient(connectionString);   
    //连接数据库
    MongoDatabase database = mongoClient.getDatabase("test");
    // 连接collection
    MongoCollection<Document> collection = database.getCollection("student");
    //查询第一个文档
    Document myDoc = collection.find().first();
    //得到文件内容 json串
    String json = myDoc.toJson();
    System.out.println(json);
}
```



1.5、











## 二、基本增删改查操作 

### 2.1、选择或创建数据库

```shell
# 使用use 数据库名称即可选择数据库，如果该数据库不存在会自动创建
> use itcastdb
switched to db itcastdb
```

### 2.2、插入文档 

##### 格式：db.集合名称.save(变量); 

```shell
> r={name:"孙悟空",sex:"男",age:30,address:"花果山水帘洞"};
{ "name" : "孙悟空", "sex" : "男", "age" : 30, "address" : "花果山水帘洞" }
> db.student.save(r);
WriteResult({ "nInserted" : 1 })

> db.student.save({_id:1,name:"红孩儿",sex:"男",age:16,address:"火云洞"});
```

### 2.3、查询集合 

##### 格式：db.集合名称.find(); 

```shell
> db.student.find();
{ "_id" : ObjectId("5ca476f6e285f101792fff0c"), "name" : "孙悟空", "sex" : "男", "age" : 30, "address" : "花果山水帘洞" }
{ "_id" : ObjectId("5ca477ade285f101792fff0d"), "name" : "孙悟空", "sex" : "男", "age" : 30, "address" : "花果山水帘洞" }
{ "_id" : ObjectId("5ca477b8e285f101792fff0e"), "name" : "猪八戒", "sex" : "男", "age" : 28, "address" : "高老庄旅游度假村" }
{ "_id" : ObjectId("5ca477b8e285f101792fff0f"), "name" : "沙和尚", "sex" : "男", "age" : 25, "address" : "流沙河路11号" }
{ "_id" : ObjectId("5ca477b8e285f101792fff10"), "name" : "唐僧", "sex" : "男", "age" : 35, "address" : "东土大唐" }
{ "_id" : ObjectId("5ca477b8e285f101792fff11"), "name" : "白骨精", "sex" : "女", "age" : 18, "address" : "白骨洞" }
{ "_id" : ObjectId("5ca477b8e285f101792fff12"), "name" : "白龙马", "sex" : "男", "age" : 20, "address" : "西海" }
{ "_id" : ObjectId("5ca477b8e285f101792fff13"), "name" : "哪吒", "sex" : "男", "age" : 15, "address" : "莲花湾小区" }
{ "_id" : 1, "name" : "红孩儿", "sex" : "男", "age" : 16, "address" : "火云洞" }
```

​	这里你会发现每条文档会有一个叫_id的字段，这个相当于我们原来关系数据库中表的主键，当你在插入文档记录时没有指定该字段，MongDB会自动创建，其类型是ObjectID类型。

​	如果我们在插入文档记录时指定该字段也可以，其类型可以使ObjectID类型，也可以是MongoDB支持的任意类型。

- ##### 指定查找

  ```shell
  > db.student.find({sex:"女"});
  { "_id" : ObjectId("5ca477b8e285f101792fff11"), "name" : "白骨精", "sex" : "女", "age" : 18, "address" : "白骨洞" }
  ```

- ##### 为了避免游标可能带来的开销，MongoDB还提供了一个叫findOne()的方法，用来返回结果集的第一条记录。 

  ```shell
  > db.student.findOne({sex:"男"});
  {
          "_id" : ObjectId("5ca476f6e285f101792fff0c"),
          "name" : "孙悟空",
          "sex" : "男",
          "age" : 30,
          "address" : "花果山水帘洞"
  }
  ```

- ##### 当我们需要返回查询结果的前几条记录时，可以使用limit方法，例如：

  ```shell
  > db.student.find().limit(3);
  { "_id" : ObjectId("5ca476f6e285f101792fff0c"), "name" : "孙悟空", "sex" : "男", "age" : 30, "address" : "花果山水帘洞" }
  { "_id" : ObjectId("5ca477ade285f101792fff0d"), "name" : "孙悟空", "sex" : "男", "age" : 30, "address" : "花果山水帘洞" }
  { "_id" : ObjectId("5ca477b8e285f101792fff0e"), "name" : "猪八戒", "sex" : "男", "age" : 28, "address" : "高老庄旅游度假村" }
  ```

### 2.4、修改文档 

##### 格式：db.集合名称.update({条件},{修改值}); 

```shell
> db.student.update({name:"孙悟空"},{age:31});
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })

> db.student.find().limit(3);              });
{ "_id" : ObjectId("5ca476f6e285f101792fff0c"), "age" : 31 }
{ "_id" : ObjectId("5ca477ade285f101792fff0d"), "name" : "孙悟空", "sex" : "男", "age" : 30, "address" : "花果山水帘洞" }
{ "_id" : ObjectId("5ca477b8e285f101792fff0e"), "name" : "猪八戒", "sex" : "男", "age" : 28, "address" : "高老庄旅游度假村" }
```

哦，悲剧了~~ 原来的孙悟空的文档只剩下_id 和age两个字段了。(**同时注意只修改一条数据**！)

那如何保留其它字段值呢？

我们需要使用MongoDB提供的修改器$set 来实现，请看下列代码。

```shell
> db.student.update({name:"猪八戒"},{$set:{age:31}});
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
```

### 2.5、删除文档 

##### 格式：db.集合名称.remove( 条件 );   这个是影响多行！

```shell
> db.student.remove({age:31});
WriteResult({ "nRemoved" : 2 })
```



## 三、高级查询 































