# 品优10_搜索解决方案-Solr2

## 一、品优购-高亮显示 

- 后端代码

  ```java
  @Service(timeout=5000) // dubbo 链接默认是1000就出现链接超时异常，由于web 连dubbo ，dubbo又要连solr,此时很容易链接超1s
  public class ItemSearchServiceImpl implements ItemSearchService {
  	
  	@Autowired
  	private SolrTemplate solrTemplate;
  
  	@Override
  	public Map search(Map searchMap) {
  		
  		Map<String,Object> map=new HashMap<>();
  //		Query query=new SimpleQuery();
  //		//添加查询条件
  //		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
  //		query.addCriteria(criteria);
  //		ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
  		
  		// 高亮搜索
  		HighlightQuery query=new SimpleHighlightQuery();
  		HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//设置高亮的域
  		highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀 
  		highlightOptions.setSimplePostfix("</em>");//高亮后缀
  		query.setHighlightOptions(highlightOptions);//设置高亮选项
  		//按照关键字查询
  		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
  		query.addCriteria(criteria);
  		
  		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
  		for(HighlightEntry<TbItem> h: page.getHighlighted()){//循环高亮入口集合
  			TbItem item = h.getEntity();//获取原实体类		
  			
  //			List<Highlight> hList = h.getHighlights(); // 我们可能同时设置多个高亮域
  			
  //			for(Highlight hlt: hList) {
  //				List<String> sns = hlt.getSnipplets(); // 每个域可能存储多值
  //				System.out.println(sns);
  //			}
  			
  			// 由于我们设置的高亮域只有一个， 同时域又是单值
  			if(h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){ // 使用前非空判断
  				item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
  			}			
  		}		
  
  		map.put("rows", page.getContent()); // 这个只是一个原生结果
  		return map;
  	}
  }
  ```

- #### 前端html信任

  由于angularJs安全机制会防止html,js 等系列攻击，当文本有html内容会以文本形式显示。那么该如何解决呢？ 

  我们使用angularJS的一个$sce服务，信任机制服务，通过trustAsHtml方法来实现转换；

  使用$sce其实就是一个对文本数据进行过滤，所以需要用到angularJS的过滤器，实现代码如下：

  ```js
  // 定义模块:
  var app = angular.module("pinyougou",[]);
  /*$sce服务写成过滤器*/
  app.filter('trustHtml',['$sce',function($sce){
      return function(data){
          return $sce.trustAsHtml(data);
      }
  }]);
  ```

  ##### 在前端页面调用：ng-bind-html指令用于显示html内容 

  ```html
  <div class="attr" ng-bind-html="item.title | trustHtml"></div>
  ```



## 二、搜索业务规则分析 

目标是在关键字搜索的基础上添加面板搜索功能。 

面板上有商品分类、品牌、各种规格和价格区间等条件 。

#### 实现思路：

（1）搜索面板的商品分类需要使用Spring Data Solr的分组查询来实现

（2）为了能够提高查询速度，我们需要把查询面板的品牌、规格数据提前放入redis

（3）查询条件的构建、面板的隐藏需要使用angularJS来实现

（4）后端的分类、品牌、规格、价格区间查询需要使用过滤查询来实现

#### 业务规则： 

##### （1）当用户输入关键字搜索后，除了显示列表结果外，还应该显示通过这个关键字搜索到的记录都有哪些商品分类。

```java
// 1. 前面我们已经将商品tb_item表的搜索字段数据加载到了solr，那么根据关键词是复制域查询，那么就一定会有item_category覆盖，故能以关键词搜索到商品分类项目，而非所有的商品分类项目；所以，对于商品分类连带查询，本来已经到了solr数据库，就不需要什么redis缓存。

// 2. 在数据库查询时，有一个group by关键词，查询分组的名称；那么solr也类似，
private  List searchCategoryList(Map searchMap){
    List<String> list=new ArrayList();	
    Query query=new SimpleQuery();		
    //按照关键字查询
    Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
    query.addCriteria(criteria);
    //设置分组选项
    GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");
    query.setGroupOptions(groupOptions);
    //得到分组页
    GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
    //根据列得到分组结果集
    GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
    //得到分组结果入口页
    Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
    //得到分组入口集合
    List<GroupEntry<TbItem>> content = groupEntries.getContent();
    for(GroupEntry<TbItem> entry:content){
        list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中	
    }
    return list;
}
```

##### （2）根据第一个商品分类查询对应的模板，根据模板查询出品牌列表；

##### （3）根据第一个商品分类查询对应的模板，根据模板查询出规格列表;

```java
// 1. 上面两项一起查询： 我们从（1）中得到了商品分类，tb_item_category 对应一个type_id, 即每个商品分类对应一种规格模板，规格模板包含了具体的品牌及规格信息；
// 2. 查询对应商品分类的品牌列表，是依据type_id查询tb_type_template表即可；每次若都去查询数据库，会造成后台压力过大，所以此时，我们将tb_type_template缓存到redis，然后搜索时，查询reids即可。
// 3. 如何缓存呢？ solr只有商品分类名称，那么就需要缓存一个商品分类名称为key, 模板id为value；
```

```java
// 第一步就是，redis 缓存商品分类名称为key, 模板id为value; 根据名称找type_id;
// 不管是商品的CRUD都需要走此路，那么redis在这里缓存最好
@Override
public List<TbItemCat> findByParentId(Long parentId) {
    TbItemCatExample example = new TbItemCatExample();
    TbItemCatExample.Criteria criteria = example.createCriteria();
    criteria.andParentIdEqualTo(parentId);

    //每次执行查询的时候，一次性读取缓存进行存储 (因为每次增删改都要执行此方法)
    List<TbItemCat> list = findAll();
    for(TbItemCat itemCat:list){
        redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
    }
    return itemCatMapper.selectByExample(example);
}
```

```java
// 第二步就是，根据type_id, 从redis缓存 key=type_id, value=brand_ids(对象{id:'',name:''})
//  key=type_id, value=spec_list(对象{id:'',name:''})
private void saveToRedis(){
    //获取模板数据
    List<TbTypeTemplate> typeTemplateList = findAll();
    //循环模板
    for(TbTypeTemplate typeTemplate :typeTemplateList){				
        //存储品牌列表		
        List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);			
        redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);
        //存储规格列表
        List<Map> specList = findSpecList(typeTemplate.getId());//根据模板ID查询规格列表
        redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specList);		
    }
}
```

```java
// 第三步、根据商品分类名称从redis查询 品牌列表，规格列表 (很简单)
private Map searchBrandAndSpecList(String category) {
    Map map = new HashMap();
    Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);//获取模板ID

    if(typeId!=null){
        //根据模板ID查询品牌列表 
        List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
        map.put("brandList", brandList);//返回值添加品牌列表
        //根据模板ID查询规格列表
        List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
        map.put("specList", specList);				
    }			
    return map;
}
```

##### （4）当用户点击搜索面板的商品分类时，显示按照这个关键字查询结果的基础上，筛选此分类的结果。

```js
// 前面依据商品分类名称，查询了所有的品牌及规格，现在我们要再加条件，相当于对结果进行过滤。 下面仅展示前端过滤. 在searchController添加两个方法

$scope.searchMap={'keywords':'','category':'','brand':'','spec':{}};//搜索对象
//添加搜索项
$scope.addSearchItem=function(key,value){
    if(key=='category' || key=='brand'){//如果点击的是分类或者是品牌
        $scope.searchMap[key]=value;
    }else{
        $scope.searchMap.spec[key]=value;
    }	

    $scope.search();
}

//移除复合搜索条件
$scope.removeSearchItem=function(key){
    if(key=="category" ||  key=="brand"){//如果是分类或品牌
        $scope.searchMap[key]="";		
    }else{//否则是规格
        delete $scope.searchMap.spec[key];//移除此属性
    }	
    $scope.search();
}
```

```HTML
<!--前端筛选-->
<ul class="fl sui-breadcrumb">
    <li>
        <a href="#">搜索条件：</a>
    </li>					
</ul>
<ul class="tags-choose">
    <li class="tag" ng-if="searchMap.category!=''" ng-click="removeSearchItem('category')">商品分类：{{searchMap.category}}<i class="sui-icon icon-tb-close"></i></li>
    <li class="tag" ng-if="searchMap.brand!=''" ng-click="removeSearchItem('brand')">品牌：{{searchMap.brand}}<i class="sui-icon icon-tb-close"></i></li>
    <li class="tag" ng-repeat="(key,value) in searchMap.spec" ng-click="removeSearchItem(key)">{{key}}:{{value}}<i class="sui-icon icon-tb-close"></i></li>
</ul>

<div class="clearfix selector">
<div class="type-wrap"  ng-if="resultMap.categoryList!=null && searchMap.category == ''" >
        <div class="fl key">商品分类</div>
        <div class="fl value">
            <a href="#" ng-repeat="category in resultMap.categoryList" ng-click="addSearchItem('category',category)">{{category}}  </a>
        </div>
        <div class="fl ext"></div>
</div>
<div class="type-wrap logo" ng-if="resultMap.brandList!=null && searchMap.brand == ''">
        <div class="fl key brand">品牌</div>
        <div class="value logos">
            <ul class="logo-list">
                <li ng-repeat="brand in resultMap.brandList" ng-click="addSearchItem('brand',brand.text)">
                    <a href="#">{{brand.text}}</a>
                </li>
            </ul>
        </div>
        <div class="ext">
            <a href="javascript:void(0);" class="sui-btn">多选</a>
            <a href="javascript:void(0);">更多</a>
        </div>
</div>
    <div class="type-wrap" ng-repeat="spec in resultMap.specList" ng-if="searchMap.spec[spec.text]==null">
        <div class="fl key">{{spec.text}}</div>
        <div class="fl value">
            <ul class="type-list" >
                <li ng-repeat="option in spec.options" ng-click="addSearchItem(spec.text,option.optionName)">
                    <a>{{option.optionName}}</a>
                </li>
            </ul>
        </div>
        <div class="fl ext"></div>
    </div>
</div>
```

##### （5）当用户点击搜索面板的品牌时，显示在以上结果的基础上，筛选此品牌的结果

##### （6）当用户点击搜索面板的规格时，显示在以上结果的基础上，筛选此规格的结果

```java
private Map searchList(Map searchMap) {
    Map<String, Object> map = new HashMap<>();
    // 高亮搜索初始化
    HighlightQuery query = new SimpleHighlightQuery();
    HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");// 设置高亮的域
    highlightOptions.setSimplePrefix("<em style='color:red'>");// 高亮前缀
    highlightOptions.setSimplePostfix("</em>");// 高亮后缀
    query.setHighlightOptions(highlightOptions);// 设置高亮选项

    // 1.1 按照关键字查询
    Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
    query.addCriteria(criteria);

    // 1.2 按分类筛选
    if (!"".equals(searchMap.get("category"))) {
        Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
        FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
        query.addFilterQuery(filterQuery);
    }

    // 1.3按品牌筛选
    if (!"".equals(searchMap.get("brand"))) {
        Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
        FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
        query.addFilterQuery(filterQuery);
    }

    // 1.4过滤规格
    if (searchMap.get("spec") != null) {
        Map<String, String> specMap = (Map) searchMap.get("spec");
        for (String key : specMap.keySet()) {
            Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
    }

    // 高亮结果集处理
    HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
    for (HighlightEntry<TbItem> h : page.getHighlighted()) {// 循环高亮入口集合
        TbItem item = h.getEntity();// 获取原实体类
        if (h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size() > 0) { // 使用前非空判断
            item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));// 设置高亮的结果
        }
    }

    map.put("rows", page.getContent()); // 这个只是一个原生结果
    return map;
}

public Map search(Map searchMap) {
    Map<String, Object> map = new HashMap<>();
    map.putAll(searchList(searchMap));

    // 2.根据关键字查询商品分类
    List<String> categoryList = searchCategoryList(searchMap);
    map.put("categoryList", categoryList);

    // 3. 查询品牌 规格
    String category = (String)searchMap.get("category");
    if("".equals(category)) {
        if (categoryList.size() > 0) {
            map.putAll(searchBrandAndSpecList(categoryList.get(0)));
        }
    }else { // 若传递了商品分类，按照商品分类名称查询商品品牌和规格
        map.putAll(searchBrandAndSpecList(category));
    }
    return map;
}
```

##### （7）当用户点击价格区间时，显示在以上结果的基础上，按价格进行筛选的结果;

​	下一章节详解

##### （8）当用户点击搜索面板的相应条件时，隐藏已点击的条件。

```html
<!-- 此项是前端页面的具体操作: searchMap.category=''即可，更多参考（4） -->
<div class="type-wrap"  ng-if="resultMap.categoryList!=null && searchMap.category == ''" >
    <div class="fl key">商品分类</div>
    <div class="fl value">
        <a href="#" ng-repeat="category in resultMap.categoryList" ng-click="addSearchItem('category',category)">{{category}}  </a>
    </div>
    <div class="fl ext"></div>
</div>
```





















