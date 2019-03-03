# SSM综合案例_02 订单／分页PageHelper

## 一、订单

### 1.1、 订单数据表

```sql
CREATE TABLE orders(
  id varchar2(32) default SYS_GUID() PRIMARY KEY,
  orderNum VARCHAR2(20) NOT NULL UNIQUE,
  orderTime timestamp,
  peopleCount INT,
  orderDesc VARCHAR2(500),
  payType INT,
  orderStatus INT,
  productId varchar2(32),
  memberId varchar2(32),
  FOREIGN KEY (productId) REFERENCES product(id),
  FOREIGN KEY (memberId) REFERENCES member(id)
)
```

> 分析：订单表关联了product、member表
>
> 所以实体类设计如下：关联一个产品类，一个会员类进去
>
> ```java
> public class Order implements Serializable {
> 
>     private String id;
>     private String orderNum;
> 
>     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
>     private Date orderTime;
>     private String orderTimeStr;
> 
>     private Integer orderStatus;
>     private String orderStatusStr;
> 
>     private Integer peopleCount;
> 
>     private Integer payType;
>     private String payTypeStr;
> 
>     private String orderDesc;
> 
>     private Product product;  // 假设本次一个订单只对应一个产品 （旅游系列）
>     // 行程的旅客 集合
>     private List<Traveller> travellers;
>     // 会员
>     private Member member;
>     ....
> }
> ```

### 1.2、 IOrderDao

```java
public interface IOrderDao {
    
    @Select(" select * from orders ")
    @Results(id = "ordersMap",value = {
            @Result(id=true,column = "id",property = "id"),
            @Result(column = "orderNum",property = "orderNum"),
            @Result(column = "orderTime",property = "orderTime"),
            @Result(column = "orderStatus",property = "orderStatus"),
            @Result(column = "peopleCount",property = "peopleCount"),
            @Result(column = "payType",property = "payType"),
            @Result(column = "orderDesc",property = "orderDesc"),
            @Result(property = "product",column = "productId",one = @One(select = "com.eoony.dao.IProductDao.findById",fetchType = FetchType.EAGER))
    })
    List<Order> findAll() throws Exception;

    @Select(" select * from orders where id = #{ordersId}")
    @Results(value = {
            @Result(id=true,column = "id",property = "id"),
            @Result(column = "orderNum",property = "orderNum"),
            @Result(column = "orderTime",property = "orderTime"),
            @Result(column = "orderStatus",property = "orderStatus"),
            @Result(column = "peopleCount",property = "peopleCount"),
            @Result(column = "payType",property = "payType"),
            @Result(column = "orderDesc",property = "orderDesc"),
            @Result(property = "product",column = "productId",javaType = Product.class,one = @One(select = "com.eoony.dao.IProductDao.findById",fetchType = FetchType.EAGER)),
            @Result(property = "member",column = "memberId",javaType = Member.class,one = @One(select = "com.eoony.dao.IMemberDao.findById",fetchType = FetchType.EAGER)),
            @Result(property = "travellers",column = "id",javaType = List.class,many = @Many(select = "com.eoony.dao.ITravellerDao.findByOrdersId",fetchType = FetchType.LAZY))
    })
    Order findById(String ordersId) throws Exception;
}
```

### 1.3、OrderServiceImpl

```java
@Service
@Transactional
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private IOrderDao orderDao;

    @Override
    public List<Order> findAll() throws Exception{
        return orderDao.findAll();
    }

    @Override
    public List<Order> findPage(int page, int size) throws Exception {
        // 注意此句一定是在dao操作的前一句，中间若还有代码是无效的
        PageHelper.startPage(page,size); 
        return orderDao.findAll();
    }

    @Override
    public Order findById(String ordersId) throws Exception {
        return orderDao.findById(ordersId);
    }
}
```

### 1.4、OrderController

```java
@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    // 查询所有
    @RequestMapping("/findAll.do")
    public ModelAndView findAll() throws Exception {
        ModelAndView mv = new ModelAndView();
        List<Order> orders = orderService.findAll();
        mv.addObject("ordersList",orders);
        mv.setViewName("orders-list");
        return mv;
    }

    // 分页查询
    @RequestMapping("/findPage.do")
    public ModelAndView findPage(@RequestParam(name = "page", required = true, defaultValue = "1") int page,
                                 @RequestParam(name = "size", required = true, defaultValue = "4") int size) throws Exception {
        ModelAndView mv = new ModelAndView();
        List<Order> orders = orderService.findPage(page,size);
        PageInfo<Order> pageInfo = new PageInfo<Order>(orders);
//        pageInfo.setList(orders); 这种写法无效
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("orders-page-list");
        return mv;
    }

    // 查询订单详情
    @RequestMapping("/findById.do")
    public ModelAndView findById(@RequestParam(name = "id", required = true) String ordersId) throws Exception {
        ModelAndView mv = new ModelAndView();
        Order orders = orderService.findById(ordersId);
        mv.addObject("orders",orders);
        mv.setViewName("orders-show");
        return mv;
    }

}
```



## 二、PageHepler

订单查询时，我们使用了分页，这次分页操作，我们使用PageHepler插件来协助，简化代码！

### 2.1、PageHelper介绍

PageHelper是国内非常优秀的一款开源的mybatis分页插件,它支持基本主流与常用的数据库,例如mysql、 oracle、mariaDB、DB2、SQLite、Hsqldb等。 

本项目在 github 的项目地址:https://github.com/pagehelper/Mybatis-PageHelper 

本项目在 gitosc 的项目地址:http://git.oschina.net/free/Mybatis_PageHelper 

### 2.2、 PageHelper集成

- ##### 集成：引入jar，Maven配置2种。推荐使用 Maven 方式；

- ##### 引入jar方式：

```properties
你可以从下面的地址中下载最新版本的jar包:
https://oss.sonatype.org/content/repositories/releases/com/github/pagehelper/pagehelper/ 
http://repo1.maven.org/maven2/com/github/pagehelper/pagehelper/
由于使用了sql 解析工具,你还需要下载 jsqlparser.jar: http://repo1.maven.org/maven2/com/github/jsqlparser/jsqlparser/0.9.5/
```

- ##### 使用 Maven : pom.xml

```xml
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper</artifactId>
    <version>5.1.2</version>
</dependency>
```

### 2.3、PageHelper配置

- ##### 在 MyBatis 配置 xml 中配置拦截器插件:

```xml
<!-- 
    plugins在配置文件中的位置必须符合要求,否则会报错,顺序如下: 
    properties?, settings?,
    typeAliases?, typeHandlers?, 
    objectFactory?,objectWrapperFactory?,
    plugins?,
    environments?, databaseIdProvider?, mappers?
-->
<plugins>
    <!-- com.github.pagehelper为PageHelper类所在包名 -->
    <plugin interceptor="com.github.pagehelper.PageInterceptor">
        <!-- 使用下面的方式配置参数,后面会有所有的参数介绍 -->
        <property name="param1" value="value1"/>
    </plugin>
</plugins>
```

- ##### 在 Spring 配置文件中配置拦截器插件:

```xml
<!--mybatis集成-->
<!-- 把SqlSessionFactory交给IOC管理  -->
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource"/>
    <!-- 注意其他配置:分页插件 -->
    <property name="plugins">
        <array>
            <bean class="com.github.pagehelper.PageInterceptor">
                <property name="properties">
                    <props>
                        <prop key="helperDialect">oracle</prop>
                        <prop key="reasonable">true</prop>
                    </props>
                </property>
            </bean>
        </array>
    </property>
</bean>
```

> 参数说明：
>
> helperDialect： 分页插件会自动检测当前的数据库链接,自动选择合适的分页方式。指定分页插件使用哪种方言。
>
> reasonable： 分页合理化参数,默认值为 false 。当该参数设置为 true 时, pageNum<=0 时会查询第一
> 页, pageNum>pages (超过总数时),会查询最后一页。默认 false 时,直接根据参数进行查询。
>
> 其他参数，参考讲义！

### 2.4、PageHelper 单独测试Mybatis（补充）

