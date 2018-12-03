# 执行java代码插入数据到mysql中文乱码问题

#### （重点：统一解决，统一使用utf-8字符集）

## 一、mysql创建database时设置编码

```sql
create database mydb default character set utf8 collate utf8_general_ci;
```

#### 若db已创建，修正字符集:

```sql
-- 查看mysql默认编码的格式
show VARIABLES like "%char%"

+--------------------------+---------------+
| Variable_name | Value |
+--------------------------+---------------+
| character_set_client | gbk |
| character_set_connection | gbk |
| character_set_database | utf8 |
| character_set_filesystem | binary |
| character_set_results | gbk |
| character_set_server | latin1 |
| character_set_system | utf8 |
+--------------------------+-------------+
-- 若有不是utf8，都修正为utf8
set character_set_client='utf8'
set character_set_connection='utf8'
set character_set_results='utf8'
set character_set_server='utf8'
```



## 二、创建表时设置编码

```sql
CREATE TABLE `type` ( 
`id` int(10) unsigned NOT NULL auto_increment, 
`flag_deleted` enum('Y','N') character set utf8 NOT NULL default 'N', 
`flag_type` int(5) NOT NULL default '0', 
`type_name` varchar(50) character set utf8 NOT NULL default '', 
PRIMARY KEY (`id`) 
)  DEFAULT CHARSET=utf8; 
```

#### 若table已创建，修正字符集:

```sql
-- 查看表编码
show create table type

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  `sex` char(1) DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1

-- 修改(default可以省去)
alter table user default character set 'utf8'
```



## 三、连接数据库时设置 （重点）



URL最后一定要有：useUnicode=true&characterEncoding=UTF-8

```java
public class TestJdbc {
    private static String URL = "jdbc:mysql://localhost:3306/studentmanage?useUnicode=true&characterEncoding=UTF-8";
    useUnicode=true&characterEncoding=UTF-8
    private static String USER = "root";
    private static String PASSWORD = "root";

    public static void main(String[] args) {
        Connection con = null;

        String sql = "insert into user(uid,uname,password) values(?,?,?)";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
```

若是配置文件：

```properties
driverClassName=com.mysql.jdbc.Driver
url=jdbc:mysql:///day14?useUnicode=true&characterEncoding=UTF-8
username=root
password=root
initialSize=5
maxActive=10
maxWait=3000
```

> xml配置注意字符转义：<property name="jdbcUrl" value="url=jdbc:mysql:///day14?useUnicode=true[&amp;]characterEncoding=UTF-8"









