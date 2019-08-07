# 品优09_搜索解决方案-Solr

## 一、Solr安装与配置

### 1.1、什么是Solr

​	大多数搜索引擎应用都必须具有某种搜索功能，问题是搜索功能往往是巨大的资源消耗并且它们由于沉重的数据库加载而拖垮你的应用的性能。

​	这就是为什么转移负载到一个外部的搜索服务器是一个不错的主意，Apache Solr是一个流行的开源搜索服务器，它通过使用类似REST的HTTP API，这就确保你能从几乎任何编程语言来使用solr。

​	Solr是一个开源搜索平台，用于构建搜索应用程序。它建立在[Lucene](http://www.yiibai.com/lucene/)(全文搜索引擎)之上。 Solr是企业级的，快速的和高度可扩展的。使用Solr构建的应用程序非常复杂，可提供高性能。	

​	为了在CNET网络的公司网站上添加搜索功能，Yonik Seely于2004年创建了Solr。并在2006年1月，它成为Apache软件基金会下的一个开源项目。并于2016年发布最新版本Solr 6.0，支持并行SQL查询的执行。

​	Solr可以和[Hadoop](http://www.yiibai.com/hadoop/)一起使用。由于Hadoop处理大量数据，Solr帮助我们从这么大的源中找到所需的信息。不仅限于搜索，Solr也可以用于存储目的。像其他NoSQL数据库一样，它是一种非关系数据存储和处理技术。

​	总之，Solr是一个可扩展的，可部署，搜索/存储引擎，优化搜索大量以文本为中心的数据。

### 1.2、Solr安装

参考讲义！

### 1.3、中文 分析器 IK Analyzer

- p配置参考讲义；

- ##### 配置域：

  域相当于数据库的表字段，用户存放数据，因此用户根据业务需要去定义相关的Field（域），一般来说，每一种对应着一种数据，用户对同一种数据进行相同的操作。 

  域的常用属性： 

  ```properties
  1. name: 指定域的名称；
  2. type：指定域的类型；
  3. indexed: 是否索引； -- 用于查询
  4. stored：是否存储；
  5. required: 是否必须；
  6. multiValued: 是否多值；
  ```

- ##### 基本域 :  搜索使用的基本信息，如下

  id：搜索商品一定需要带出id，一般情况不会用id去搜索商品，所以indexed=false。但是特定情况，如我们根据id指定查询商品删除时，需要用id去搜索，所以项目中还是配置indexed=true; 

  title：标题这个不用说，而且是根据我们指定的分析器来进行查询；

  price：价格也是普通要求

  image：图片地址，不需要按此查询，indexed=false，但是是搜索的一个结果值；

  category：商品目录，必须；

  brand：品牌也是必须的，但是为啥type=string， 因为表中有一个first_char对应；

  ```xml
  <field name="item_goodsid" type="long" indexed="true" stored="true"/>
  <field name="item_title" type="text_ik" indexed="true" stored="true"/>
  <field name="item_price" type="double" indexed="true" stored="true"/>
  <field name="item_image" type="string" indexed="false" stored="true" />
  <field name="item_category" type="string" indexed="true" stored="true" />
  <field name="item_seller" type="text_ik" indexed="true" stored="true" />
  <field name="item_brand" type="string" indexed="true" stored="true" />
  ```

- ##### 复制域

  为啥要有复制域？我们搜索框往往只有一个，即关键词搜，但是这个关键词可能是title, price, category, seller, brand等，此时该怎么办？即基本域的并集搜索查询，我们引入复制域。设计这个复制域，然后复制到对应的基本域，从而实现关键词并集搜索。

  复制域：不需要存储，同时是多值的，因为要复制给多个基本域；

  ```XML
  <field name="item_keywords" type="text_ik" indexed="true" stored="false" multiValued="true"/>
  <copyField source="item_title" dest="item_keywords"/>
  <copyField source="item_category" dest="item_keywords"/>
  <copyField source="item_seller" dest="item_keywords"/>
  <copyField source="item_brand" dest="item_keywords"/>
  ```

- ##### 动态域

  像商品规格，不同商品的规格是不一样的，此时，我们无法根据基本域来固定模式来查询商品规格。此时就需要引入动态域。

  ```xml
  <dynamicField name="item_spec_*" type="string" indexed="true" stored="true" />	
  ```

  ```java
  {
      "id": "4",
      "item_title": "测试商品 移动4G 5寸",
      "item_price": 888,
      "item_goodsid": 293980564,
      "item_category": "手机",
      "item_spec网络制式": "3G",
      "item_spec屏幕尺寸": "5寸",
      "item_seller": "华为专卖店",
      "version": 192493
  }
  ```

  

## 二、Spring Data Solr入门 

一般搜索若使用sql like关键词来实现，会很低效，同时搜索一般访问量很大，都后台数据库压力会很大。从而我们使用专门的软件来实现搜索功能，即Solr。

​	官方提供了solrJ api来操作Solr。 而SpringDataSolr即是SolrJ进行了封装。我们简单调用SpringDataSolr即可。

- 依赖

  ```xml
  <dependencies>
      <dependency>
          <groupId>org.springframework.data</groupId>
          <artifactId>spring-data-solr</artifactId>
          <version>1.5.5.RELEASE</version>
      </dependency> 
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-test</artifactId>
          <version>4.2.4.RELEASE</version>
      </dependency>
      <dependency>
          <groupId>junit</groupId>
          <artifactId>junit</artifactId>
          <version>4.9</version>
      </dependency>
  </dependencies>
  ```

- ##### 配置applicationContext-solr.xml

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
         xmlns:context="http://www.springframework.org/schema/context"
         xmlns:solr="http://www.springframework.org/schema/data/solr"
         xsi:schemaLocation="http://www.springframework.org/schema/data/solr 
                      http://www.springframework.org/schema/data/solr/spring-solr-1.0.xsd
                      http://www.springframework.org/schema/beans 
                      http://www.springframework.org/schema/beans/spring-beans.xsd
                      http://www.springframework.org/schema/context 
                      http://www.springframework.org/schema/context/spring-context.xsd">
      <!-- solr服务器地址 -->
      <solr:solr-server id="solrServer" url="http://127.0.0.1:8080/solr" />
      <!-- solr模板，使用solr模板可对索引库进行CRUD的操作 -->
      <bean id="solrTemplate" class="org.springframework.data.solr.core.SolrTemplate">
          <constructor-arg ref="solrServer" />
      </bean>
  </beans>
  ```

- TEST CODE

  ```java
  @Test
  public void testAdd() { // 增加一条记录
      TbItem item = new TbItem();
      // 一定需要这个id ，因为solrhome有一个<unqiueKey>id</unqiueKey>, 必须配值。
      // 否则会错误，使用maven test即可看出真实原因！
      item.setId(1L);
      item.setBrand("华为");
      item.setGoodsId(12932L);
      item.setCategory("手机");
      item.setSeller("华为2号专卖店");
      item.setTitle("华为Mate9");
      item.setPrice(new BigDecimal(2000));		
      solrTemplate.saveBean(item);
      solrTemplate.commit();
  }
  
  @Test
  public void testFindOne(){ // 根据主键查找
      TbItem item = solrTemplate.getById(1, TbItem.class);
      System.out.println(item.getTitle());
  }
  
  
  @Test
  public void testDelete(){ // 根据主键删除
      solrTemplate.deleteById("1");
      solrTemplate.commit();
  }
  
  @Test
  public void testAddList(){ // 插入多条记录
      List<TbItem> list=new ArrayList<TbItem>();
  
      for(int i=0;i<100;i++){
          TbItem item=new TbItem();
          item.setId(i+1L);
          item.setBrand("华为");
          item.setCategory("手机");
          item.setGoodsId(1L);
          item.setSeller("华为2号专卖店");
          item.setTitle("华为Mate"+i);
          item.setPrice(new BigDecimal(2000+i));	
          list.add(item);
      }
  
      solrTemplate.saveBeans(list);
      solrTemplate.commit();
  }
  
  @Test
  public void testPageQuery(){ // 分页查询
      Query query=new SimpleQuery("*:*");
      query.setOffset(20);//开始索引（默认0）
      query.setRows(20);//每页记录数(默认10)
      ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
      System.out.println("总记录数："+page.getTotalElements());
      List<TbItem> list = page.getContent();
      showList(list);
  }	
  
  @Test
  public void testPageQueryMutil(){	// 条件查询
      Query query=new SimpleQuery("*:*");
      Criteria criteria=new Criteria("item_title").contains("2");
      criteria=criteria.and("item_title").contains("5");		
      query.addCriteria(criteria);
      //query.setOffset(20);//开始索引（默认0）
      //query.setRows(20);//每页记录数(默认10)
      ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
      System.out.println("总记录数："+page.getTotalElements());
      List<TbItem> list = page.getContent();
      showList(list);
  }
  
  @Test
  public void deleteAll() { // 删除所有
      Query query = new SimpleQuery("*:*");
      solrTemplate.delete(query);
      solrTemplate.commit();
  }
  ```

  

## 三、批量数据导 

方法一：使用solr插件，百度上有答案；

方法二：自定义代码实现；（可实现任意数据结构批量导入）

- ##### 创建一个SolrUtil jar工程，即通过手动一次导入

```java
@Component
public class SolrUtil {

	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	public void importItemData(){
		
		TbItemExample example=new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");//审核通过的才导入的
		List<TbItem> itemList = itemMapper.selectByExample(example);
		
		System.out.println("---商品列表---");
		for(TbItem item:itemList){
			System.out.println(item.getId()+" "+ item.getTitle()+ " "+item.getPrice());	
			Map specMap = JSON.parseObject(item.getSpec(), Map.class);//从数据库中提取规格json字符串转换为map
			item.setSpecMap(specMap);
		}
		
		solrTemplate.saveBeans(itemList);
		solrTemplate.commit();
		
		System.out.println("---结束---");
	}
	
	public static void main(String[] args) {
		ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil solrUtil=  (SolrUtil) context.getBean("solrUtil");
		solrUtil.importItemData();
	}
}
```

- ##### POJO修改 : 添加solr的基本配置域，复制域，动态域

```java
public class TbItem implements Serializable{
	
	@Field
    private Long id;

	@Field("item_title")
    private String title;

    private String sellPoint;

    @Field("item_price")
    private BigDecimal price;

    private Integer stockCount;

    private Integer num;

    private String barcode;

    @Field("item_image")
    private String image;

    private Long categoryid;

    private String status;

    private Date createTime;

    private Date updateTime;

    private String itemSn;

    private BigDecimal costPirce;

    private BigDecimal marketPrice;

    private String isDefault;

    @Field("item_goodsid")
    private Long goodsId;

    private String sellerId;

    private String cartThumbnail;

    @Field("item_category")
    private String category;

    @Field("item_brand")
    private String brand;

    private String spec;

    @Field("item_seller")
    private String seller;
    
    @Dynamic
    @Field("item_spec_*")
    private Map<String,String> specMap;
    
    public Map<String, String> getSpecMap() {
		return specMap;
	}

	public void setSpecMap(Map<String, String> specMap) {
		this.specMap = specMap;
	}
... 
}
```



## 四、品优购-关键字搜索 

（1）创建pinyougou-search-interface模块（搜索服务接口），依赖pinyougou-pojo

（2）创建pinyougou-search-service模块，war包

（3）创建pinyougou-search-web模块，war包

























