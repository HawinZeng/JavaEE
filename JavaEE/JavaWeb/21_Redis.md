# 第二十一节  Redis

## 一、概念：

#### redis是一款高性能的NOSQL系列的非关系型数据库；

### 1.1、什么是NOSQL？

##### NoSQL(NoSQL = Not Only SQL)，意即“不仅仅是SQL”，是一项全新的数据库理念，泛指非关系型的数据库。

​    随着互联网web2.0网站的兴起，传统的关系数据库在应付web2.0网站，特别是超大规模和高并发的SNS类型的web2.0纯动态网站已经显得力不从心，暴露了很多难以克服的问题，而非关系型的数据库则由于其本身的特点得到了非常迅速的发展。NoSQL数据库的产生就是为了解决大规模数据集合多重数据种类带来的挑战，尤其是大数据应用难题。

#### 1.1.1、NOSQL和关系型数据库比较

##### 优点：

1. 成本：nosql数据库简单易部署，基本都是开源软件，不需要像使用oracle那样花费大量成本购买使用，相比关系型数据库价格便宜。
2. 查询速度：nosql数据库将数据存储于缓存之中，关系型数据库将数据存储在硬盘中，自然查询速度远不及nosql数据库。
3. 存储数据的格式：nosql的存储格式是key,value形式、文档形式、图片形式等等，所以可以存储基础类型以及对象或者是集合等各种格式，而数据库则只支持基础类型。
4. 扩展性：关系型数据库有类似join这样的多表查询机制的限制导致扩展很艰难。

##### 缺点：

1. 维护的工具和资料有限，因为nosql是属于新的技术，不能和关系型数据库10几年的技术同日而语。
2. 不提供对sql的支持，如果不支持sql这样的工业标准，将产生一定用户的学习和使用成本。
3. 有些NOSQL数据库不提供关系型数据库对事务的处理，但是redis支持。

#### 1.1.2、非关系型数据库的优势：

1. 性能NOSQL是基于键值对的，可以想象成表中的主键和值的对应关系，而且不需要经过SQL层的解析，所以性能非常高。
2. 可扩展性同样也是因为基于键值对，数据之间没有耦合性，所以非常容易水平扩展。

#### 1.1.3、关系型数据库的优势：

1. 复杂查询可以用SQL语句方便的在一个表以及多个表之间做非常复杂的数据查询。
2. 事务支持使得对于安全性能很高的数据访问要求得以实现。对于这两类数据库，对方的优势就是自己的弱势，反之亦然。

#### 1.1.4、总结

1. 关系型数据库与NoSQL数据库并非对立而是互补的关系，即通常情况下使用关系型数据库，在适合使用NoSQL的时候使用NoSQL数据库；
2. 让NoSQL数据库对关系型数据库的不足进行弥补；
3. 一般会将数据存储在关系型数据库中，在nosql数据库中备份存储关系型数据库的数据；



### 1.2、主流的NOSQL产品

- #### 键值(Key-Value)存储数据库

  1. 相关产品： Tokyo Cabinet/Tyrant、Redis、Voldemort、Berkeley DB;
  2. 典型应用： 内容缓存，主要用于处理大量数据的高访问负载。 
  3. 数据模型： 一系列键值对
  4. 优势： 快速查询
  5. 劣势： 存储的数据缺少结构化

- #### 列存储数据库

  1. 相关产品：Cassandra, HBase, Riak
  2. 典型应用：分布式的文件系统
  3. 数据模型：以列簇式存储，将同一列数据存在一起
  4. 优势：查找速度快，可扩展性强，更容易进行分布式扩展
  5. 劣势：功能相对局限

- #### 文档型数据库

  1. 相关产品：CouchDB、MongoDB
  2. 典型应用：Web应用（与Key-Value类似，Value是结构化的）
  3. 数据模型： 一系列键值对
  4. 优势：数据结构要求不严格
  5. 劣势： 查询性能不高，而且缺乏统一的查询语法

- #### 图形(Graph)数据库

  1. 相关数据库：Neo4J、InfoGrid、Infinite Graph
  2. 典型应用：社交网络
  3. 数据模型：图结构
  4. 优势：利用图结构相关算法。
  5. 劣势：需要对整个图做计算才能得出结果，不容易做分布式的集群方案。



### 1.3、什么是Redis？

​	Redis是用C语言开发的一个开源的高性能键值对（key-value）数据库，官方提供测试数据，50个并发执行100000个请求,读的速度是110000次/s,写的速度是81000次/s ，且Redis通过提供多种键值数据类型来适应不同场景下的存储需求，目前为止Redis支持的键值数据类型如下：

​	1）字符串类型 string；

​	2）哈希类型 hash

​	3）列表类型 list

​	4）集合类型 set

​	5）有序集合类型 sortedset

- #### redis的应用场景

  1. 缓存（数据查询、短连接、新闻内容、商品内容等等）
  2. 聊天室的在线好友列表
  3. 任务队列。（秒杀、抢购、12306等等）
  4. 应用排行榜
  5. 网站访问统计
  6. 数据过期处理（可以精确到毫秒)
  7. 分布式集群架构中的session分离



## 二、下载安装

1. 官网：https://redis.io
2. 中文网：http://www.redis.net.cn/
3. 解压直接可以使用：
  * redis.windows.conf：配置文件
  * redis-cli.exe：redis的客户端
  * redis-server.exe：redis服务器端



## 三、使用

### 3.1、redis的数据结构：

##### redis存储的是：key,value格式的数据，其中key都是字符串，value有5种不同的数据结构。

 value的数据结构：

- 字符串类型 string
- 哈希类型 hash ： map格式  
- 列表类型 list ： linkedlist格式。支持重复元素
- 集合类型 set  ： 不允许重复元素hashSet
- 有序集合类型 sortedset：不允许重复元素，且元素有顺序



### 3.2、字符串类型 string

- ##### 存储： set key value

  ```
  127.0.0.1:6379> set username zhangsan
  OK
  ```

- ##### 获取： get key

  ```
  127.0.0.1:6379> get username
  "zhangsan"
  ```

- #####  删除： del key

  ```
  127.0.0.1:6379> del username
  (integer) 1
  ```


### 3.3、哈希类型 hash

- ##### 存储： hset key field value

  ```
  127.0.0.1:6379> hset myhash username lisi
  (integer) 1
  127.0.0.1:6379> hset myhash password 123
  (integer) 1
  ```

- ##### 获取：hget key field: 获取指定的field对应的值

  ```java
  127.0.0.1:6379> hget myhash username
  "lisi"
  127.0.0.1:6379> hgetall myhash // 查询全部
  1) "username"
  2) "lisi"
  3) "password"
  4) "123"
  ```

- ##### 删除： hdel key field

  ```
  127.0.0.1:6379> hdel myhash username
  (integer) 1
  ```



#### 3.4、 列表类型 list  ---- （相当于linkedList）

##### 可以添加一个元素到列表的头部（左边）或者尾部（右边）！

- ##### 存储：

  1. ##### lpush key value: 将元素加入列表左表

  2. ##### rpush key value：将元素加入列表右边

  ```java
  127.0.0.1:6379> lpush myList alice
  (integer) 1
  127.0.0.1:6379> lpush myList hawin
  (integer) 2
  
  127.0.0.1:6379> rpush myList jorry
  (integer) 3
  ```

- ##### 获取：lrange key start end 范围获取

  ```
  127.0.0.1:6379> lrange myList 0 -1
  1) "hawin"
  2) "alice"
  3) "jorry"
  ```

- ##### 删除：

  1. ##### lpop key： 删除列表最左边的元素，并将元素返回;

  2. ##### rpop key： 删除列表最右边的元素，并将元素返回

  ```
  127.0.0.1:6379> lpop myList
  "hawin"
  
  127.0.0.1:6379> rpop myList
  "jorry"
  ```



### 3.5、集合类型 set ： 不允许重复元素

- ##### 存储：sadd key value

- ##### 获取：smembers key:获取set集合中所有元素

- ##### 删除：srem key value:删除set集合中的某个元素	



### 3.6、有序集合类型 sortedset：不允许重复元素，且元素有顺序.

##### 关键：每个元素都会关联一个double类型的分数。redis正是通过分数来为集合中的成员进行从小到大的排序。

- ##### 存储：zadd key score value

  ```java
  127.0.0.1:6379> zadd mysort 60 zhangsan
  (integer) 1
  127.0.0.1:6379> zadd mysort 40 lisi
  (integer) 1
  127.0.0.1:6379> zadd mysort 80 wangwu
  (integer) 1
  ```

- ##### 获取：zrange key start end [withscores]

  ```java
  127.0.0.1:6379> zrange mysort 0 -1
  1) "lisi"
  2) "zhangsan"
  3) "wangwu"
  
  127.0.0.1:6379> zrange mysort 0 -1 withscores
  1) "lisi"
  2) "40"
  3) "zhangsan"
  4) "60"
  5) "wangwu"
  6) "80"
  ```

- ##### 删除：zrem key value

  ```java
  127.0.0.1:6379> zrem mysort lisi
  (integer) 1
  
  127.0.0.1:6379> zrange mysort 0 -1
  1) "zhangsan"
  2) "wangwu"
  ```



### 3.7 、通用命令

- ##### keys * : 查询所有的键

- ##### type key ： 获取键对应的value的类型

- ##### del key：删除指定的key value



## 四、持久化

- ##### redis是一个内存数据库，当redis服务器重启，获取电脑重启，数据会丢失，我们可以将redis内存中的数据持久化保存到硬盘的文件中。

- ####  redis持久化机制：

  1. ##### RDB：默认方式，不需要进行配置，默认就使用这种机制

     在一定的间隔时间中，检测key的变化情况，然后持久化数据

     - 1 编辑redis.windwos.conf文件

     ```java
     #   after 900 sec (15 min) if at least 1 key changed
     save 900 1
     #   after 300 sec (5 min) if at least 10 keys changed
     save 300 10
     #   after 60 sec if at least 10000 keys changed
     save 60 10000
     ```

     - 2 重新启动redis服务器，并指定配置文件名称

     ```java
     windows:
     dos: D:\JavaWeb2018\day23_redis\资料\redis\windows-64\redis-2.8.9>redis-server.exe redis.windows.conf
     
     Mac:
     1. 直接关闭redis-server，是不能关闭的；重启依然发现redis-server还活着，占用端口；
     2. 要在redis-cli，调用shutdown命令，才能关闭redis-server；
     3. lsof -i tcp:6379   查看端口情况；
     
     $ /Volumes/C/redis-3.0.6/src/redis-server /Volumes/C/redis-3.0.6/redis.conf 
     ```

  2. ##### AOF：日志记录的方式，可以记录每一条命令的操作。每一次命令操作后，持久化数据，比较影响性能

     - 编辑redis.windwos.conf文件

       appendonly no（关闭aof） --> appendonly yes （开启aof）

     - 取值选择

       ＃appendfsync always： 每一次操作都进行持久化

       appendfsync everysec： 每隔一秒进行一次持久化

       ＃appendfsync no： 不进行持久化



## 五、Java客户端 Jedis

1. ##### Jedis: 一款java操作redis数据库的工具. （相当于JDBC）

2. ##### 使用步骤：

   - 下载jedis的jar包 : `commons-pool2-2.3.jar`, `jedis-2.7.0.jar`
   - 使用代码

   ```java
       public void test(){
           Jedis jedis = new Jedis("localhost",6379);
           jedis.set("username1","zhangfei");
           jedis.close();
       }
   ```

3. #### Jedis操作各种redis中的数据结构

   ##### 1） 字符串类型 string  

   ```java
   // 方法：set／get
   ```

   ##### 2）哈希类型 hash ：map格式   

   ```java
   // 方法：hset／hget／hgetAll
   ```

   ##### 3）列表类型 list ： linkedlist格式。支持重复元素 

   ```java
   // 方法：lpush / rpush、lpop / rpop、lrange start end (范围获取)
   ```

   ##### 4）集合类型 set  ： 不允许重复元素

   ```java
   // 方法：sadd ／ smembers:获取所有元素
   ```

   ##### 5）有序集合类型 sortedset：不允许重复元素，且元素有顺序

   ```java
   //  方法：zadd / zrange
   ```



## 六、jedis连接池： JedisPool

1. ##### redis自带连接池

2. ##### 使用：

   ```java
        public void test2(){
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(50);
            config.setMaxIdle(10);
   
            JedisPool jedisPool = new JedisPool(config,"localhost",6379);
            Jedis jedis = jedisPool.getResource();
   
            jedis.set("hello","world!");
        }
   ```

3. ##### 连接池工具类编写：雷同JDBCUtils

   ```java
   public class JedisPoolUtils {
       private static JedisPool jedisPool;
       static {
           try {
               Properties pro = new Properties();  pro.load(JedisPoolUtils.class.getClassLoader().getResourceAsStream("jedis.properties"));
               JedisPoolConfig config = new JedisPoolConfig();
               config.setMaxTotal(Integer.parseInt(pro.getProperty("maxTotal")));
               config.setMaxIdle(Integer.parseInt(pro.getProperty("maxIdle")));
   
               jedisPool = new JedisPool(config,pro.getProperty("host"),Integer.parseInt(pro.getProperty("port")));
   
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
   
       public static JedisPool getJedisPool(){
           return jedisPool;
       }
       
       public static Jedis getJedis(){
           return jedisPool.getResource();
       }
   }
   ```










